package com.restapi.gestion_bons.service.stock;

import com.restapi.gestion_bons.dao.LotDAO;
import com.restapi.gestion_bons.dao.ProduitDAO;
import com.restapi.gestion_bons.dao.MouvementStockDAO;
import com.restapi.gestion_bons.dto.stock.StockGlobalDTO;
import com.restapi.gestion_bons.dto.stock.StockProduitDetailDTO;
import com.restapi.gestion_bons.dto.stock.StockValorisationDTO;
import com.restapi.gestion_bons.entitie.Lot;
import com.restapi.gestion_bons.entitie.Produit;
import com.restapi.gestion_bons.entitie.enums.LotStatus;
import com.restapi.gestion_bons.mapper.MouvementStockMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private LotDAO lotDAO;

    @Mock
    private ProduitDAO produitDAO;

    @Mock
    private MouvementStockDAO mouvementStockDAO;

    @Mock
    private MouvementStockMapper mouvementStockMapper;

    @InjectMocks
    private StockService stockService;

    private Produit produit;
    private Lot lot1, lot2, lot3;

    @BeforeEach
    void setUp() {
        // Setup Produit
        produit = Produit.builder()
                .id(1L)
                .nom("Produit Test")
                .reference("REF-TEST")
                .build();

        // Setup Lots avec differents prix (methode FIFO - plus anciens en premier)
        lot1 = Lot.builder()
                .id(1L)
                .numeroLot("LOT-001")
                .dateEntree(LocalDateTime.now().minusDays(10)) // Plus ancien
                .quantiteInitiale(100)
                .quantiteRestante(50) // 50 unites restantes
                .prixAchatUnitaire(new BigDecimal("10.00")) // Prix le plus bas
                .produit(produit)
                .statut(LotStatus.DISPONIBLE)
                .build();

        lot2 = Lot.builder()
                .id(2L)
                .numeroLot("LOT-002")
                .dateEntree(LocalDateTime.now().minusDays(5)) // Plus recent
                .quantiteInitiale(80)
                .quantiteRestante(30) // 30 unites restantes
                .prixAchatUnitaire(new BigDecimal("15.50")) // Prix moyen
                .produit(produit)
                .statut(LotStatus.DISPONIBLE)
                .build();

        lot3 = Lot.builder()
                .id(3L)
                .numeroLot("LOT-003")
                .dateEntree(LocalDateTime.now().minusDays(2)) // Le plus recent
                .quantiteInitiale(60)
                .quantiteRestante(20) // 20 unites restantes
                .prixAchatUnitaire(new BigDecimal("20.75")) // Prix le plus eleve
                .produit(produit)
                .statut(LotStatus.DISPONIBLE)
                .build();
    }

    @Test
    void getStockByProduitId_ShouldCalculateValuationByMultiplyingQuantitiesByUnitPrices() {
        // Arrange
        List<Lot> lots = Arrays.asList(lot1, lot2, lot3);

        when(produitDAO.findById(1L)).thenReturn(Optional.of(produit));
        when(lotDAO.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(lots);

        // Act
        StockProduitDetailDTO result = stockService.getStockByProduitId(1L);

        // Assert
        assertNotNull(result);

        // Verifier le calcul : quantites restantes × prix d'achat unitaires
        // Lot1: 50 × 10.00 = 500.00
        // Lot2: 30 × 15.50 = 465.00  
        // Lot3: 20 × 20.75 = 415.00
        // TOTAL: 500.00 + 465.00 + 415.00 = 1380.00
        BigDecimal valorisationAttendue = new BigDecimal("1380.00");
        assertEquals(0, valorisationAttendue.compareTo(result.getValorisationFIFO()));
    }

    @Test
    void getStockByProduitId_ShouldUseFIFOValuationBasedOnOldestLots() {
        // Arrange
        List<Lot> lots = Arrays.asList(lot1, lot2, lot3); // Tries par date d'entree ascendante

        when(produitDAO.findById(1L)).thenReturn(Optional.of(produit));
        when(lotDAO.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(lots);

        // Act
        StockProduitDetailDTO result = stockService.getStockByProduitId(1L);

        // Assert
        assertNotNull(result);

        // Verifier que la valorisation FIFO inclut tous les lots disponibles
        // dans l'ordre des plus anciens aux plus recents
        assertEquals(3, result.getLots().size());
        assertEquals("LOT-001", result.getLots().get(0).getNumeroLot()); // Plus ancien
        assertEquals("LOT-002", result.getLots().get(1).getNumeroLot());
        assertEquals("LOT-003", result.getLots().get(2).getNumeroLot()); // Plus recent

        // Verifier que chaque lot contribue à la valorisation selon son prix
        BigDecimal valorisationLot1 = new BigDecimal("500.00"); // 50 × 10.00
        BigDecimal valorisationLot2 = new BigDecimal("465.00"); // 30 × 15.50
        BigDecimal valorisationLot3 = new BigDecimal("415.00"); // 20 × 20.75
        BigDecimal valorisationTotaleAttendue = valorisationLot1.add(valorisationLot2).add(valorisationLot3);

        assertEquals(0, valorisationTotaleAttendue.compareTo(result.getValorisationFIFO()));
    }

    @Test
    void getStockByProduitId_WithMultipleLotsAtDifferentPrices_ShouldCalculateCorrectValuation() {
        // Arrange - Lots avec differents prix d'achat unitaires
        List<Lot> lotsMultiPrix = Arrays.asList(lot1, lot2, lot3);

        when(produitDAO.findById(1L)).thenReturn(Optional.of(produit));
        when(lotDAO.findByProduitIdOrderByDateEntreeAsc(1L)).thenReturn(lotsMultiPrix);

        // Act
        StockProduitDetailDTO result = stockService.getStockByProduitId(1L);

        // Assert
        assertNotNull(result);

        // Verifier le calcul avec differents prix
        // Prix: 10.00, 15.50, 20.75
        // Quantites: 50, 30, 20
        BigDecimal calculManuel = new BigDecimal("50").multiply(new BigDecimal("10.00"))
                .add(new BigDecimal("30").multiply(new BigDecimal("15.50")))
                .add(new BigDecimal("20").multiply(new BigDecimal("20.75")));

        assertEquals(0, calculManuel.compareTo(result.getValorisationFIFO()));

        // Verifier que la quantite totale est correcte
        assertEquals(100, result.getQuantiteTotale()); // 50 + 30 + 20
    }

    @Test
    void getValorisation_ShouldCalculateTotalStockValuationUsingFIFO() {
        // Arrange
        List<Lot> tousLotsDisponibles = Arrays.asList(lot1, lot2, lot3);

        when(lotDAO.findByStatut(LotStatus.DISPONIBLE)).thenReturn(tousLotsDisponibles);

        // Act
        StockValorisationDTO result = stockService.getValorisation();

        // Assert
        assertNotNull(result);

        // Verifier la valorisation totale selon FIFO
        BigDecimal valorisationTotaleAttendue = new BigDecimal("1380.00");
        assertEquals(0, valorisationTotaleAttendue.compareTo(result.getValorisationTotale()));

        // Verifier que la methode FIFO est utilisee
        assertEquals("FIFO", result.getMethodeVlorisation());
    }
}