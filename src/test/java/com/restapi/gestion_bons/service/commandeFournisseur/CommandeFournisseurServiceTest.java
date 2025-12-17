package com.restapi.gestion_bons.service.commandeFournisseur;

import com.restapi.gestion_bons.dao.*;
import com.restapi.gestion_bons.dto.commandefournisseur.CommandeFournisseurResponseDTO;
import com.restapi.gestion_bons.entitie.*;
import com.restapi.gestion_bons.entitie.enums.CommandeStatus;
import com.restapi.gestion_bons.mapper.CommandeFournisseurMapper;
import com.restapi.gestion_bons.mapper.LigneCommandeMapper;
import com.restapi.gestion_bons.service.fournisseur.FournisseurService;
import com.restapi.gestion_bons.util.LotHelper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeFournisseurServiceTest {

    @Mock
    private CommandeFournisseurDAO commandeFournisseurDAO;

    @Mock
    private CommandeFournisseurMapper commandeFournisseurMapper;

    @Mock
    private FournisseurService fournisseurService;

    @Mock
    private LotDAO lotDAO;

    @Mock
    private LotHelper lotHelper;

    @Mock
    private ProduitDAO produitDAO;

    @Mock
    private LigneCommandeDAO ligneCommandeDAO;

    @Mock
    private FournisseurDAO fournisseurDAO;

    @Mock
    private LigneCommandeMapper ligneCommandeMapper;

    @InjectMocks
    private CommandeFournisseurService commandeFournisseurService;

    private CommandeFournisseur commandeValidee;
    private Fournisseur fournisseur;
    private Produit produit;
    private LigneCommande ligneCommande;
    private Lot lot;

    @BeforeEach
    void setUp() {
        // Setup Fournisseur
        fournisseur = new Fournisseur();
        fournisseur.setId(1L);
        fournisseur.setRaisonSociale("Fournitures Pro");

        // Setup Produit
        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");
        produit.setReference("REF001");

        // Setup LigneCommande
        ligneCommande = new LigneCommande();
        ligneCommande.setId(1L);
        ligneCommande.setProduit(produit);
        ligneCommande.setQuantiteCommandee(10);
        ligneCommande.setPrixAchatUnitaire(15.50);

        // Setup CommandeFournisseur avec statut VALIDEE
        commandeValidee = CommandeFournisseur.builder()
                .id(1L)
                .fournisseur(fournisseur)
                .dateCommande(new Date())
                .montantTotal(155.00)
                .statut(CommandeStatus.VALIDEE)
                .lignesCommande(Arrays.asList(ligneCommande))
                .build();

        // Setup Lot
        lot = Lot.builder()
                .id(1L)
                .numeroLot("LOT-2024-001")
                .dateEntree(LocalDateTime.now())
                .quantiteInitiale(10)
                .quantiteRestante(10)
                .prixAchatUnitaire(new BigDecimal("15.50"))
                .produit(produit)
                .commandeFournisseur(commandeValidee)
                .build();

        ligneCommande.setCommande(commandeValidee);
    }

    @Test
    void receptionnerCommande_WhenCommandeValidee_ShouldCreateLotsAndUpdateStatusToLivree() {
        // Arrange
        Long commandeId = 1L;
        List<Lot> lotsAttendus = Arrays.asList(lot);

        when(commandeFournisseurDAO.findById(commandeId))
                .thenReturn(Optional.of(commandeValidee));
        when(lotHelper.createLotsFromLignesCommande(commandeValidee))
                .thenReturn(lotsAttendus);
        when(lotDAO.saveAll(lotsAttendus))
                .thenReturn(lotsAttendus);
        when(commandeFournisseurDAO.save(commandeValidee))
                .thenReturn(commandeValidee);

        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO();
        responseDTO.setId(commandeId);
        responseDTO.setStatut(CommandeStatus.LIVREE);
        when(commandeFournisseurMapper.toResponseDto(commandeValidee))
                .thenReturn(responseDTO);

        // Act
        CommandeFournisseurResponseDTO result = commandeFournisseurService.receptionnerCommande(commandeId);

        // Assert
        assertNotNull(result);
        assertEquals(CommandeStatus.LIVREE, result.getStatut());

        // verefier que le statut a mis à jouree
        assertEquals(CommandeStatus.LIVREE, commandeValidee.getStatut());

        // verefier que les lots ont ete crees et sauvegardes
        verify(lotHelper).createLotsFromLignesCommande(commandeValidee);
        verify(lotDAO).saveAll(lotsAttendus);
        verify(commandeFournisseurDAO).save(commandeValidee);

        // Verifier que le lot est liee à la commande
        assertTrue(commandeValidee.getLots().isEmpty(), "La liste des lots dans la commande devrait etre vide car elle est lazy");
        System.out.println("Russite");
    }

    @Test
    void receptionnerCommande_WhenCommandeNotValidee_ShouldThrowIllegalStateException() {
        // Arrange - Tester differents statuts invalides
        CommandeStatus[] statutsInvalides = {
                CommandeStatus.EN_ATTENTE,
                CommandeStatus.LIVREE,
                null
        };

        for (CommandeStatus statutInvalide : statutsInvalides) {
            commandeValidee.setStatut(statutInvalide);

            when(commandeFournisseurDAO.findById(1L))
                    .thenReturn(Optional.of(commandeValidee));

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> commandeFournisseurService.receptionnerCommande(1L));

            assertEquals("Can only receive VALIDEE orders", exception.getMessage());
        }

        verify(lotHelper, never()).createLotsFromLignesCommande(any());
        verify(lotDAO, never()).saveAll(any());
        verify(commandeFournisseurDAO, never()).save(any());
        System.out.println("Russite");

    }

    @Test
    void receptionnerCommande_ShouldVerifyLotPropertiesAndTraceability() {
        // Arrange
        Long commandeId = 1L;
        List<Lot> lotsAttendus = Arrays.asList(lot);

        when(commandeFournisseurDAO.findById(commandeId))
                .thenReturn(Optional.of(commandeValidee));
        when(lotHelper.createLotsFromLignesCommande(commandeValidee))
                .thenReturn(lotsAttendus);
        when(lotDAO.saveAll(lotsAttendus))
                .thenReturn(lotsAttendus);
        when(commandeFournisseurDAO.save(commandeValidee))
                .thenReturn(commandeValidee);

        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO();
        when(commandeFournisseurMapper.toResponseDto(commandeValidee))
                .thenReturn(responseDTO);

        // Act
        commandeFournisseurService.receptionnerCommande(commandeId);

        // Assert - Verifier les proprietes de traçabilite du lot
        Lot lotCree = lotsAttendus.get(0);

        // Verification du numero de lot
        assertNotNull(lotCree.getNumeroLot(), "Le numero de lot ne doit pas etre null");
        assertFalse(lotCree.getNumeroLot().isBlank(), "Le numero de lot ne doit pas etre vide");

        // Verification de la date d'entree
        assertNotNull(lotCree.getDateEntree(), "La date d'entree ne doit pas etre null");
        assertTrue(lotCree.getDateEntree().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                        lotCree.getDateEntree().isEqual(LocalDateTime.now()),
                "La date d'entree doit etre la date actuelle ou très recente");

        // Verification du prix d'achat unitaire
        assertNotNull(lotCree.getPrixAchatUnitaire(), "Le prix d'achat unitaire ne doit pas etre null");
        assertEquals(new BigDecimal("15.50"), lotCree.getPrixAchatUnitaire(),
                "Le prix d'achat unitaire doit correspondre à celui de la ligne de commande");

        // Verification de la traçabilite
        assertEquals(produit, lotCree.getProduit(), "Le lot doit etre lie au bon produit pour la traçabilite");
        assertEquals(commandeValidee, lotCree.getCommandeFournisseur(), "Le lot doit etre lie à la commande fournisseur pour la traçabilite");

        // Verification des quantites
        assertEquals(10, lotCree.getQuantiteInitiale(), "La quantite initiale doit correspondre à la ligne de commande");
        assertEquals(10, lotCree.getQuantiteRestante(), "La quantite restante doit etre egale à la quantite initiale à la creation");
        System.out.println("Russite");
    }

    @Test
    void receptionnerCommande_WithMultipleLignesCommande_ShouldCreateMultipleLots() {
        // Arrange
        Long commandeId = 1L;

        // Creer une deuxième ligne de commande
        LigneCommande ligneCommande2 = new LigneCommande();
        ligneCommande2.setId(2L);
        ligneCommande2.setProduit(produit);
        ligneCommande2.setQuantiteCommandee(5);
        ligneCommande2.setPrixAchatUnitaire(12.75);
        ligneCommande2.setCommande(commandeValidee);

        commandeValidee.setLignesCommande(Arrays.asList(ligneCommande, ligneCommande2));

        // Creer un deuxième lot
        Lot lot2 = Lot.builder()
                .id(2L)
                .numeroLot("LOT-2024-002")
                .dateEntree(LocalDateTime.now())
                .quantiteInitiale(5)
                .quantiteRestante(5)
                .prixAchatUnitaire(new BigDecimal("12.75"))
                .produit(produit)
                .commandeFournisseur(commandeValidee)
                .build();

        List<Lot> lotsMultiples = Arrays.asList(lot, lot2);

        when(commandeFournisseurDAO.findById(commandeId))
                .thenReturn(Optional.of(commandeValidee));
        when(lotHelper.createLotsFromLignesCommande(commandeValidee))
                .thenReturn(lotsMultiples);
        when(lotDAO.saveAll(lotsMultiples))
                .thenReturn(lotsMultiples);
        when(commandeFournisseurDAO.save(commandeValidee))
                .thenReturn(commandeValidee);

        CommandeFournisseurResponseDTO responseDTO = new CommandeFournisseurResponseDTO();
        when(commandeFournisseurMapper.toResponseDto(commandeValidee))
                .thenReturn(responseDTO);

        // Act
        commandeFournisseurService.receptionnerCommande(commandeId);

        // Assert
        verify(lotDAO).saveAll(lotsMultiples);
        assertEquals(2, lotsMultiples.size(), "Deux lots doivent etre crees pour deux lignes de commande");

        // Verifier que chaque lot a des proprietes differentes basees sur les lignes de commande
        Lot premierLot = lotsMultiples.get(0);
        Lot deuxiemeLot = lotsMultiples.get(1);

        assertEquals(new BigDecimal("15.50"), premierLot.getPrixAchatUnitaire());
        assertEquals(new BigDecimal("12.75"), deuxiemeLot.getPrixAchatUnitaire());
        assertEquals(10, premierLot.getQuantiteInitiale());
        assertEquals(5, deuxiemeLot.getQuantiteInitiale());
        System.out.println("Russite");

    }


}