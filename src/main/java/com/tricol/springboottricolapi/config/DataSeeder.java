package com.tricol.springboottricolapi.config;

import com.tricol.springboottricolapi.entity.*;
import com.tricol.springboottricolapi.entity.enums.MovementType;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.repository.*;
import com.tricol.springboottricolapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StockBatchRepository stockBatchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    @Override
    public void run(String... args) {
        permissionService.initializePermissions();
        
        if (userRepository.count() == 0) {
            seedUsers();
        }
        
        if (productRepository.count() > 0) return;

        Supplier supplier1 = Supplier.builder()
                .raisonSociale("Fournisseur A")
                .address("123 Rue Test")
                .city("Casablanca")
                .ice("001234567890")
                .email("contact@fournisseurA.ma")
                .phone("0612345678")
                .build();
        supplierRepository.save(supplier1);

        Product product1 = Product.builder()
                .reference("PROD001")
                .name("Produit Test 1")
                .category("Electronique")
                .unitPrice(BigDecimal.valueOf(100))
                .currentStock(BigDecimal.valueOf(500))
                .reorderPoint(BigDecimal.valueOf(50))
                .unitOfMeasure("PIECE")
                .build();

        Product product2 = Product.builder()
                .reference("PROD002")
                .name("Produit Test 2")
                .category("Textile")
                .unitPrice(BigDecimal.valueOf(50))
                .currentStock(BigDecimal.valueOf(300))
                .reorderPoint(BigDecimal.valueOf(30))
                .unitOfMeasure("PIECE")
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        StockBatch batch1 = StockBatch.builder()
                .product(product1)
                .batchNumber("LOT-001")
                .entryDate(LocalDateTime.now().minusDays(10))
                .initialQuantity(BigDecimal.valueOf(300))
                .remainingQuantity(BigDecimal.valueOf(250))
                .unitPurchasePrice(BigDecimal.valueOf(80))
                .build();

        StockBatch batch2 = StockBatch.builder()
                .product(product1)
                .batchNumber("LOT-002")
                .entryDate(LocalDateTime.now().minusDays(5))
                .initialQuantity(BigDecimal.valueOf(200))
                .remainingQuantity(BigDecimal.valueOf(200))
                .unitPurchasePrice(BigDecimal.valueOf(85))
                .build();

        StockBatch batch3 = StockBatch.builder()
                .product(product2)
                .batchNumber("LOT-003")
                .entryDate(LocalDateTime.now().minusDays(7))
                .initialQuantity(BigDecimal.valueOf(300))
                .remainingQuantity(BigDecimal.valueOf(250))
                .unitPurchasePrice(BigDecimal.valueOf(40))
                .build();

        stockBatchRepository.save(batch1);
        stockBatchRepository.save(batch2);
        stockBatchRepository.save(batch3);

        StockMovement movement1 = StockMovement.builder()
                .product(product1)
                .batch(batch1)
                .movementType(MovementType.ENTREE)
                .quantity(BigDecimal.valueOf(300))
                .movementDate(LocalDateTime.now().minusDays(10))
                .source("SUPPLIER_ORDER")
                .comments("Entrée initiale")
                .build();

        StockMovement movement2 = StockMovement.builder()
                .product(product1)
                .batch(batch1)
                .movementType(MovementType.SORTIE)
                .quantity(BigDecimal.valueOf(50))
                .movementDate(LocalDateTime.now().minusDays(3))
                .source("DELIVERY_NOTE")
                .comments("Sortie production")
                .build();

        StockMovement movement3 = StockMovement.builder()
                .product(product2)
                .batch(batch3)
                .movementType(MovementType.ENTREE)
                .quantity(BigDecimal.valueOf(300))
                .movementDate(LocalDateTime.now().minusDays(7))
                .source("SUPPLIER_ORDER")
                .build();

        StockMovement movement4 = StockMovement.builder()
                .product(product2)
                .batch(batch3)
                .movementType(MovementType.SORTIE)
                .quantity(BigDecimal.valueOf(50))
                .movementDate(LocalDateTime.now().minusDays(2))
                .source("DELIVERY_NOTE")
                .build();

        stockMovementRepository.save(movement1);
        stockMovementRepository.save(movement2);
        stockMovementRepository.save(movement3);
        stockMovementRepository.save(movement4);

        System.out.println("✅ Data seeded successfully!");
    }

    private void seedUsers() {
        UserApp admin = UserApp.builder()
                .username("admin")
                .email("admin@tricol.ma")
                .password(passwordEncoder.encode("admin123"))
                .enabled(true)
                .roles(Set.of(RoleApp.ADMIN))
                .build();

        UserApp responsableAchats = UserApp.builder()
                .username("achats")
                .email("achats@tricol.ma")
                .password(passwordEncoder.encode("achats123"))
                .enabled(true)
                .roles(Set.of(RoleApp.RESPONSABLE_ACHATS))
                .build();

        UserApp magasinier = UserApp.builder()
                .username("magasinier")
                .email("magasinier@tricol.ma")
                .password(passwordEncoder.encode("magasinier123"))
                .enabled(true)
                .roles(Set.of(RoleApp.MAGASINIER))
                .build();

        UserApp chefAtelier = UserApp.builder()
                .username("chef")
                .email("chef@tricol.ma")
                .password(passwordEncoder.encode("chef123"))
                .enabled(true)
                .roles(Set.of(RoleApp.CHEF_ATELIER))
                .build();

        userRepository.save(admin);
        userRepository.save(responsableAchats);
        userRepository.save(magasinier);
        userRepository.save(chefAtelier);

        System.out.println("✅ Users seeded successfully!");
    }
}
