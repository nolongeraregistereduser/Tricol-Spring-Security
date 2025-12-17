package com.restapi.gestion_bons.service.bondesortie;

import com.restapi.gestion_bons.dao.*;
import com.restapi.gestion_bons.entitie.*;
import com.restapi.gestion_bons.entitie.enums.BonDeSortieStatus;
import com.restapi.gestion_bons.entitie.enums.LotStatus;
import com.restapi.gestion_bons.mapper.BonDeSortieMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonDeSortieServiceTest {

    @Mock private BonDeSortieDAO bonDeSortieDAO;
    @Mock private LotDAO lotDAO;
    @Mock private MouvementStockDAO mouvementStockDAO;
    @Mock private AtelierDAO atelierDAO;
    @Mock private ProduitDAO produitDAO;
    @Mock private BonDeSortieMapper bonDeSortieMapper; // AJOUTe

    @InjectMocks
    private BonDeSortieService bonDeSortieService;

    private BonDeSortieLigne ligne;
    private BonDeSortie bonDeSortie;
    private Produit produit;

    @BeforeEach
    void setUp() {
        produit = Produit.builder()
                .id(1L)
                .nom("Produit A")
                .reference("REF-A")
                .build();

        ligne = BonDeSortieLigne.builder()
                .produit(produit)
                .quantiteDemandee(50)
                .build();

        bonDeSortie = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-TEST")
                .build();
    }

    // Vos tests existants (scenarios 1-4) restent inchanges...
    @Test
    void scenario1_SortiePartielleUnLot() {
        // Given
        Lot lot = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(100)
                .quantiteInitiale(100)
                .prixAchatUnitaire(BigDecimal.valueOf(15.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot));

        // When
        bonDeSortieService.traiterSortieFIFO(ligne, bonDeSortie);

        // Then
        assertEquals(50, lot.getQuantiteRestante());
        verify(mouvementStockDAO).save(any());
        System.out.println("Scenario 1 reussi: Sortie partielle d'un lot");
    }

    @Test
    void scenario2_SortieMultiplesLots() {
        // Given
        Lot lot1 = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(30)
                .quantiteInitiale(50)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        Lot lot2 = Lot.builder()
                .id(2L)
                .produit(produit)
                .quantiteRestante(40)
                .quantiteInitiale(60)
                .prixAchatUnitaire(BigDecimal.valueOf(12.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        ligne.setQuantiteDemandee(60);

        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot1, lot2));

        // When
        bonDeSortieService.traiterSortieFIFO(ligne, bonDeSortie);

        // Then
        assertEquals(0, lot1.getQuantiteRestante());
        assertEquals(10, lot2.getQuantiteRestante());
        verify(mouvementStockDAO, times(2)).save(any());
        System.out.println("Scenario 2 reussi: Sortie multiple lots");
    }

    @Test
    void scenario3_StockInsuffisant() {
        // Given
        Lot lot = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(30)
                .quantiteInitiale(30)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        ligne.setQuantiteDemandee(50);

        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bonDeSortieService.traiterSortieFIFO(ligne, bonDeSortie));

        assertTrue(exception.getMessage().contains("Stock insuffisant"));
        assertTrue(exception.getMessage().contains("Manque: 20"));
        System.out.println("Scenario 3 reussi: Gestion stock insuffisant");
    }

    @Test
    void scenario4_SortieEpuisantStockExact() {
        // Given
        Lot lot1 = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(20)
                .quantiteInitiale(50)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        Lot lot2 = Lot.builder()
                .id(2L)
                .produit(produit)
                .quantiteRestante(30)
                .quantiteInitiale(30)
                .prixAchatUnitaire(BigDecimal.valueOf(12.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        ligne.setQuantiteDemandee(50);

        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot1, lot2));

        // When
        bonDeSortieService.traiterSortieFIFO(ligne, bonDeSortie);

        // Then
        assertEquals(0, lot1.getQuantiteRestante());
        assertEquals(0, lot2.getQuantiteRestante());
        assertEquals(LotStatus.EPUISE, lot1.getStatut());
        assertEquals(LotStatus.EPUISE, lot2.getStatut());
        verify(mouvementStockDAO, times(2)).save(any());
        System.out.println("Scenario 4 reussi: Sortie epuisant stock exact");
    }

    // Tests de validation - CORRIGeS avec le mock du mapper
    @Test
    void validationBonDeSortie_DoitCreerMouvementsStock() {
        // Given
        BonDeSortie bonDeSortieBrouillon = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-001")
                .statut(BonDeSortieStatus.BROUILLON)
                .build();

        Produit produit1 = Produit.builder().id(1L).nom("Produit A").build();
        Produit produit2 = Produit.builder().id(2L).nom("Produit B").build();

        BonDeSortieLigne ligne1 = BonDeSortieLigne.builder()
                .produit(produit1)
                .quantiteDemandee(20)
                .build();

        BonDeSortieLigne ligne2 = BonDeSortieLigne.builder()
                .produit(produit2)
                .quantiteDemandee(15)
                .build();

        bonDeSortieBrouillon.setBonDeSortieLignes(List.of(ligne1, ligne2));

        Lot lot1 = Lot.builder()
                .id(1L)
                .produit(produit1)
                .quantiteRestante(30)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .build();

        Lot lot2 = Lot.builder()
                .id(2L)
                .produit(produit2)
                .quantiteRestante(20)
                .prixAchatUnitaire(BigDecimal.valueOf(15.0))
                .statut(LotStatus.DISPONIBLE)
                .build();

        when(bonDeSortieDAO.findById(1L)).thenReturn(Optional.of(bonDeSortieBrouillon));
        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot1));
        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(2L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot2));
        when(bonDeSortieDAO.save(any(BonDeSortie.class))).thenReturn(bonDeSortieBrouillon);
        when(bonDeSortieMapper.toResponseDto(any(BonDeSortie.class))).thenReturn(null); // AJOUTe

        // When
        bonDeSortieService.valider(1L);

        // Then
        assertEquals(BonDeSortieStatus.VALIDE, bonDeSortieBrouillon.getStatut());
        verify(mouvementStockDAO, times(2)).save(any(MouvementStock.class));
        verify(lotDAO, times(2)).save(any(Lot.class));
        System.out.println("Scenario 5 reussi: Validation bon declenche creation mouvements stock");
    }

    @Test
    void validationBonDeSortie_DoitMettreAJourQuantitesLots() {
        // Given
        BonDeSortie bonDeSortieBrouillon = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-001")
                .statut(BonDeSortieStatus.BROUILLON)
                .build();

        Produit produit = Produit.builder().id(1L).nom("Produit A").build();

        BonDeSortieLigne ligne = BonDeSortieLigne.builder()
                .produit(produit)
                .quantiteDemandee(25)
                .build();

        bonDeSortieBrouillon.setBonDeSortieLignes(List.of(ligne));

        Lot lot = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(40)
                .quantiteInitiale(40)
                .prixAchatUnitaire(BigDecimal.valueOf(12.5))
                .statut(LotStatus.DISPONIBLE)
                .build();

        when(bonDeSortieDAO.findById(1L)).thenReturn(Optional.of(bonDeSortieBrouillon));
        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot));
        when(bonDeSortieDAO.save(any(BonDeSortie.class))).thenReturn(bonDeSortieBrouillon);
        when(bonDeSortieMapper.toResponseDto(any(BonDeSortie.class))).thenReturn(null); // AJOUTe

        // When
        bonDeSortieService.valider(1L);

        // Then
        assertEquals(15, lot.getQuantiteRestante());
        assertEquals(LotStatus.DISPONIBLE, lot.getStatut());
        verify(lotDAO).save(lot);
        System.out.println("Scenario 6 reussi: Validation bon met à jour quantites lots");
    }

    @Test
    void validationBonDeSortie_DoitChangerStatutLotSiEpulse() {
        // Given
        BonDeSortie bonDeSortieBrouillon = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-001")
                .statut(BonDeSortieStatus.BROUILLON)
                .build();

        Produit produit = Produit.builder().id(1L).nom("Produit A").build();

        BonDeSortieLigne ligne = BonDeSortieLigne.builder()
                .produit(produit)
                .quantiteDemandee(30)
                .build();

        bonDeSortieBrouillon.setBonDeSortieLignes(List.of(ligne));

        Lot lot = Lot.builder()
                .id(1L)
                .produit(produit)
                .quantiteRestante(30)
                .quantiteInitiale(30)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .build();

        when(bonDeSortieDAO.findById(1L)).thenReturn(Optional.of(bonDeSortieBrouillon));
        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot));
        when(bonDeSortieDAO.save(any(BonDeSortie.class))).thenReturn(bonDeSortieBrouillon);
        when(bonDeSortieMapper.toResponseDto(any(BonDeSortie.class))).thenReturn(null); // AJOUTe

        // When
        bonDeSortieService.valider(1L);

        // Then
        assertEquals(0, lot.getQuantiteRestante());
        assertEquals(LotStatus.EPUISE, lot.getStatut());
        verify(lotDAO).save(lot);
        System.out.println("Scenario 7 reussi: Validation bon change statut lot si epuise");
    }

    @Test
    void validationBonDeSortie_StatutNonBrouillon_DoitLeverException() {
        // Given
        BonDeSortie bonDeSortieValide = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-001")
                .statut(BonDeSortieStatus.VALIDE)
                .build();

        when(bonDeSortieDAO.findById(1L)).thenReturn(Optional.of(bonDeSortieValide));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bonDeSortieService.valider(1L));

        assertTrue(exception.getMessage().contains("Seuls les bons de sortie brouillons peuvent etre valides"));
        System.out.println("Scenario 8 reussi: Validation echoue si statut non brouillon");
    }

    @Test
    void validationBonDeSortie_MultipleLotsFIFO() {
        // Given
        BonDeSortie bonDeSortieBrouillon = BonDeSortie.builder()
                .id(1L)
                .numeroBon("BS-001")
                .statut(BonDeSortieStatus.BROUILLON)
                .build();

        Produit produit = Produit.builder().id(1L).nom("Produit A").build();

        BonDeSortieLigne ligne = BonDeSortieLigne.builder()
                .produit(produit)
                .quantiteDemandee(60)
                .build();

        bonDeSortieBrouillon.setBonDeSortieLignes(List.of(ligne));

        // Lots ordonnes par date d'entree (FIFO)
        Lot lot1 = Lot.builder() // Plus ancien
                .id(1L)
                .produit(produit)
                .quantiteRestante(20)
                .quantiteInitiale(20)
                .prixAchatUnitaire(BigDecimal.valueOf(8.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(10))
                .build();

        Lot lot2 = Lot.builder() // Intermediaire
                .id(2L)
                .produit(produit)
                .quantiteRestante(30)
                .quantiteInitiale(30)
                .prixAchatUnitaire(BigDecimal.valueOf(9.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(5))
                .build();

        Lot lot3 = Lot.builder() // Plus recent
                .id(3L)
                .produit(produit)
                .quantiteRestante(25)
                .quantiteInitiale(25)
                .prixAchatUnitaire(BigDecimal.valueOf(10.0))
                .statut(LotStatus.DISPONIBLE)
                .dateEntree(LocalDateTime.now().minusDays(2))
                .build();

        when(bonDeSortieDAO.findById(1L)).thenReturn(Optional.of(bonDeSortieBrouillon));
        when(lotDAO.findByProduitIdAndStatutOrderByDateEntreeAsc(1L, LotStatus.DISPONIBLE))
                .thenReturn(List.of(lot1, lot2, lot3));
        when(bonDeSortieDAO.save(any(BonDeSortie.class))).thenReturn(bonDeSortieBrouillon);
        when(bonDeSortieMapper.toResponseDto(any(BonDeSortie.class))).thenReturn(null); // AJOUTe

        // When
        bonDeSortieService.valider(1L);

        // Then - Verification ordre FIFO (60 demandes: 20 du lot1 + 30 du lot2 + 10 du lot3)
        assertEquals(0, lot1.getQuantiteRestante()); // Lot1 totalement utilise
        assertEquals(0, lot2.getQuantiteRestante()); // Lot2 totalement utilise
        assertEquals(15, lot3.getQuantiteRestante()); // Lot3 partiellement utilise (25 - 10 = 15)
        assertEquals(LotStatus.EPUISE, lot1.getStatut());
        assertEquals(LotStatus.EPUISE, lot2.getStatut());
        assertEquals(LotStatus.DISPONIBLE, lot3.getStatut());
        verify(mouvementStockDAO, times(3)).save(any(MouvementStock.class));
        System.out.println("Scenario 9 reussi: Validation bon avec multiple lots FIFO");
    }
}