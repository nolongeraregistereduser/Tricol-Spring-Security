# Project Setup Guide

## Prerequisites
- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Initial Setup

### 1. Clone the Repository
```bash
git clone https://github.com/ettaoussiilyas/Gestion-des-Bons-de-Sortie.git
cd Gestion-des-Bons-de-Sortie
```

### 2. Configure Application Properties

Copy the example file and update with your credentials:

```bash
cp src/main/resources/application.properties src/main/resources/application.properties
```

Edit `application.properties` and replace:
- `<DB_USERNAME>` with your MySQL username (default: `root`)
- `<DB_PASSWORD>` with your MySQL password

**Optional:** Copy seed profile example:
```bash
cp src/main/resources/application-seed.properties.example src/main/resources/application-seed.properties
```

### 3. Run Liquibase Migrations

Migrations run automatically on application startup. To run manually:

```bash
mvn liquibase:update
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Application will be available at: `http://localhost:8080/api`

### Seed Database (Optional)

To populate the database with test data:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seed
```

Or uncomment `spring.profiles.active=seed` in `application.properties` and run normally.

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Available Endpoints

#### Produits
- `GET /produits` - List all products
- `GET /produits/{id}` - Get product by ID
- `GET /produits/{id}/stock` - Get stock for product
- `POST /produits` - Create product
- `PUT /produits/{id}` - Update product
- `DELETE /produits/{id}` - Delete product

#### Stock Management
- `GET /stock` - Global stock view
- `GET /stock/produit/{id}` - Stock detail by product (FIFO)
- `GET /stock/mouvements` - Movement history
- `GET /stock/alertes` - Stock alerts
- `GET /stock/valorisation` - Stock valuation (FIFO)

#### Bons de Sortie
- `GET /bons-sortie` - List all exit vouchers
- `POST /bons-sortie` - Create exit voucher (draft)
- `PUT /bons-sortie/{id}/valider` - Validate voucher (FIFO)
- `PUT /bons-sortie/{id}/annuler` - Cancel voucher

#### Fournisseurs
- `GET /fournisseurs` - List all suppliers
- `POST /fournisseurs` - Create supplier
- `PUT /fournisseurs/{id}` - Update supplier

#### Ateliers
- `GET /ateliers` - List all workshops
- `POST /ateliers` - Create workshop

## HTTP Test Files

Use the `.http` files in the project root to test endpoints:
- `ProduitModel.http` - Product endpoints
- `StockModel.http` - Stock management endpoints
- `BonDeSortieModel.http` - Exit voucher endpoints
- `AtelierModel.http` - Workshop endpoints
- `FournisseurModel.http` - Supplier endpoints

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running: `sudo systemctl status mysql`
- Check credentials in `application.properties`
- Ensure database exists: `CREATE DATABASE IF NOT EXISTS gestion_bons;`

### Liquibase Errors
- Clear changelog lock: 
  ```sql
  DELETE FROM DATABASECHANGELOGLOCK WHERE ID=1;
  ```

### Seeder Already Run
The seeder checks for existing data and skips if present. To re-seed:
```sql
DROP DATABASE gestion_bons;
CREATE DATABASE gestion_bons;
```
Then run the application with seed profile.