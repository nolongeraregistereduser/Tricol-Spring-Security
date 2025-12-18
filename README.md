# 🏭 Tricol Supply Chain API

API REST Spring Boot pour la gestion complète de la chaîne d'approvisionnement et de la gestion des stocks de l'entreprise Tricol. Ce système assure un suivi rigoureux des matières premières avec la méthode FIFO (First In, First Out).

## 🎯 Objectifs du projet

- Gérer les fournisseurs et leurs informations de contact
- Gérer les produits et les alertes de stock
- Créer et suivre les commandes fournisseurs
- Impléter la gestion de stock avec valorisation FIFO
- Générer des bons de sortie vers les ateliers de production
- Tracer chaque mouvement de stock pour l'audit et la conformité

## 🧩 Architecture technique

- **Langage** : Java 17
- **Framework** : Spring Boot 3.5.7
- **Build** : Maven
- **Base de données** : MySQL (avec Liquibase pour les migrations)
- **ORM** : JPA / Hibernate
- **Mapping DTO** : MapStruct 1.6.3
- **Annotations** : Lombok
- **Validation** : Spring Validation

## 📊 Domaines métier

### 1. Gestion des Fournisseurs
- **CRUD complet** (Créer, Consulter, Modifier, Supprimer)
- **Informations** : Raison sociale, adresse, contact, email, téléphone, ville, ICE
- **Recherche et filtrage** des fournisseurs

### 2. Gestion des Produits
- **CRUD complet** des produits
- **Consultation du stock** disponible par produit
- **Système d'alertes** sur seuils minimums
- **Informations** : Référence produit, nom, description, prix unitaire, catégorie, stock actuel, point de commande, unité de mesure

### 3. Gestion des Commandes Fournisseurs
- **Créer, modifier, consulter, supprimer** des commandes
- **Filtrage** par fournisseur, statut, période
- **Association** à un fournisseur et une liste de produits
- **Calcul automatique** du montant total
- **Statuts** : EN_ATTENTE, VALIDÉE, LIVRÉE, ANNULÉE
- **Réception de commande** déclenchant la création de lots de stock

### 4. Gestion de Stock (FIFO)

#### Mouvements d'entrée
- Enregistrement automatique lors de la réception des commandes
- Création de lots avec :
  - Numéro de lot unique
  - Date d'entrée
  - Quantité
  - Prix d'achat unitaire
  - Référence à la commande fournisseur

#### Mouvements de sortie
- **Consommation FIFO** (les plus anciens lots en premier)
- **Gestion multi-lots** (une sortie peut concerner plusieurs lots)
- **Mise à jour automatique** des quantités restantes

#### Traçabilité
- Historique complet des mouvements
- Lien entre bons de sortie et mouvements de stock
- Valorisation du stock selon les prix d'achat FIFO

### 5. Gestion des Bons de Sortie
Les bons de sortie formalisent le prélèvement de matières du stock central vers les ateliers de production.

#### Fonctionnalités
- **Création** de bon de sortie (statut BROUILLON)
- **Ajout multi-produits** avec quantités
- **Modification** avant validation
- **Validation** déclenchant automatiquement les sorties FIFO
- **Annulation** possible en statut BROUILLON
- **Consultation** des bons et des mouvements générés

#### Informations du bon
- Numéro unique du bon
- Date de sortie
- Atelier destinataire
- Liste des produits avec quantités
- Motif de sortie (PRODUCTION, MAINTENANCE, AUTRE)
- Statut (BROUILLON, VALIDÉ, ANNULÉ)

## 🔄 Algorithme FIFO

### Règles métier

1. **Réception de commande** : Lors de la validation, création automatique des lots de stock
2. **Sortie de stock** : L'algorithme FIFO :
   - Identifie les lots les plus anciens en premier
   - Consomme les quantités dans l'ordre chronologique
   - Gère les sorties multi-lots
   - Met à jour les quantités restantes
3. **Valorisation** : Le calcul du stock utilise les prix d'achat selon l'ordre FIFO
4. **Traçabilité** : Chaque mouvement est enregistré avec référence aux lots

### Exemple concret

**Scénario** : Bon de sortie BS-2025-042 demandant 150 unités du Produit A

**État des lots avant** :
- LOT001 : 100 unités (le plus ancien)
- LOT002 : 80 unités

**Validation du bon** :
- Prélèvement de LOT001 : 100 unités (lot épuisé)
- Prélèvement de LOT002 : 50 unités (30 restantes)

**Mouvements générés** :
- Mouvement 1 : Sortie 100 unités depuis LOT001
- Mouvement 2 : Sortie 50 unités depuis LOT002

## 📡 API REST

### Fournisseurs
```
GET    /api/v1/fournisseurs
GET    /api/v1/fournisseurs/{id}
POST   /api/v1/fournisseurs
PUT    /api/v1/fournisseurs/{id}
DELETE /api/v1/fournisseurs/{id}
```

### Produits
```
GET    /api/v1/produits
GET    /api/v1/produits/{id}
POST   /api/v1/produits
PUT    /api/v1/produits/{id}
DELETE /api/v1/produits/{id}
GET    /api/v1/produits/{id}/stock
```

### Commandes Fournisseurs
```
GET    /api/v1/commandes
GET    /api/v1/commandes/{id}
POST   /api/v1/commandes
PUT    /api/v1/commandes/{id}
DELETE /api/v1/commandes/{id}
GET    /api/v1/commandes/fournisseur/{id}
PUT    /api/v1/commandes/{id}/reception
```

### Gestion de Stock
```
GET    /api/v1/stock
GET    /api/v1/stock/produit/{id}
GET    /api/v1/stock/mouvements
GET    /api/v1/stock/mouvements/produit/{id}
GET    /api/v1/stock/alertes
GET    /api/v1/stock/valorisation
```

### Bons de Sortie
```
GET    /api/v1/bons-sortie
GET    /api/v1/bons-sortie/{id}
POST   /api/v1/bons-sortie
PUT    /api/v1/bons-sortie/{id}
PUT    /api/v1/bons-sortie/{id}/valider
PUT    /api/v1/bons-sortie/{id}/annuler
GET    /api/v1/bons-sortie/atelier/{atelier}
```

## 🚀 Lancer l'application

### Prérequis
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Installation

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/B4drEddine0/Springboot-tricol-api.git
   cd Springboot-tricol-api
   ```

2. Configurer la base de données :
   - Créer une base de données MySQL
   - Mettre à jour `application.properties` (ou `application.yml`) :
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/tricol
     spring.datasource.username=root
     spring.datasource.password=votre_mot_de_passe
     ```

3. Compiler et lancer :
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. L'API sera accessible à `http://localhost:8080`

## 📋 Cas d'usage typiques

### Cas 1 : Sortie pour Production Normale
1. Responsable d'atelier crée un bon de sortie
2. Sélectionne l'atelier destinataire et le motif (PRODUCTION)
3. Ajoute les produits demandés avec quantités
4. Gestionnaire valide le bon
5. Algorithme FIFO prélève automatiquement du stock
6. Mouvements enregistrés avec traçabilité complète

### Cas 2 : Sortie d'Urgence avec Stock Insuffisant
1. Technicien crée un bon de sortie urgente
2. Système valide la disponibilité du stock
3. Si insuffisant : validation bloquée, alerte affichée
4. Options : modifier quantité, commander urgence, ou annuler
5. Prévention des sorties impossibles

### Cas 3 : Annulation de Bon
1. Bon créé par erreur en statut BROUILLON
2. Gestionnaire annule avant validation
3. Aucun mouvement de stock créé
4. Bon conservé dans l'historique pour audit
5. Aucune conséquence sur le stock réel

## ✅ État d'avancement

- ✅ Gestion des fournisseurs implémentée
- ✅ Gestion des produits avec alertes de stock
- ✅ Gestion des commandes fournisseurs
- ✅ Mouvements de stock FIFO
- ✅ Traçabilité des lots de stock
- ✅ Gestion des bons de sortie
- ✅ API REST complète
- 🔄 Tests unitaires en cours
- 🔄 Documentation Swagger/OpenAPI

## 🏢 À propos de Tricol

Tricol est une entreprise spécialisée dans la conception et la fabrication de vêtements professionnels. Ce système constitue une étape stratégique vers un système complet de gestion des approvisionnements et de la production.

---

**Auteur** : B4drEddine0  
**Dernière mise à jour** : Décembre 2025
