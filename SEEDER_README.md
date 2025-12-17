# Database Seeder Guide

## Running the Seeder

To seed the database with test data, run the application with the `seed` profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seed
```

Or using the JAR:

```bash
java -jar target/gestion-bons.jar --spring.profiles.active=seed
```

## What Gets Seeded

The seeder creates:

### 1. **Produits** (5 items)
- TIS-001: Tissu Coton Blanc
- TIS-002: Tissu Polyester Bleu
- FIL-001: Fil Noir Standard
- BTN-001: Boutons Plastique 15mm
- FER-001: Fermeture Éclair 20cm

### 2. **Fournisseurs** (3 suppliers)
- Textiles Maroc SARL (Casablanca)
- Fournitures Pro (Rabat)
- Atlas Accessoires (Tanger)

### 3. **Commandes Fournisseurs** (2 orders)
- Order from Textiles Maroc (status: LIVREE)
- Order from Fournitures Pro (status: LIVREE)

### 4. **Lots** (6 stock lots with FIFO dates)
- 5 active lots (DISPONIBLE)
- 1 depleted lot (EPUISE)

### 5. **Ateliers** (4 workshops)
- Atelier Couture - Bâtiment A
- Atelier Finition - Bâtiment B
- Atelier Découpe - Bâtiment C
- Atelier Repassage - Bâtiment A

### 6. **Bons de Sortie** (2 draft vouchers)
- BS-000001: Production order (BROUILLON)
- BS-000002: Maintenance order (BROUILLON)

## Testing Workflow

After seeding, you can test:

1. **Stock queries**: Check `/api/v1/stock` endpoints
2. **FIFO validation**: Validate BS-000001 to test FIFO logic
3. **Stock alerts**: Products with low stock will appear in `/api/v1/stock/alertes`
4. **Stock valorization**: Check FIFO valuation at `/api/v1/stock/valorisation`

## Resetting the Database

1. Drop the database:
```sql
DROP DATABASE gestion_bons;
CREATE DATABASE gestion_bons;
```

2. Run the application (Liquibase will recreate schema):
```bash
mvn spring-boot:run
```

3. Run the seeder:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seed
```

## Notes

- The seeder only runs when the profile `seed` is active
- Each entity type checks if data exists before seeding
- All seeded data is transactional (rolls back on error)
- Logs show progress with emoji indicators 🌱 ✅ ⏭️
