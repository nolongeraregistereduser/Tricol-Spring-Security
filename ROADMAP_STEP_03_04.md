# 📋 STEPS 3-4: DATABASE MIGRATIONS & SEEDING

---

## ✅ STEP 3: CREATE LIQUIBASE MIGRATIONS

### 🎯 Goal
Create database migration files to create tables for security entities.

### 📚 What You Need to Know

**Liquibase:**
A database migration tool that tracks and applies database changes.

**Why Liquibase?**
- Version control for database
- Automatic schema updates
- Rollback capability
- Works across different databases

**Changelog Files:**
YAML files that describe database changes.

**Your Project Structure:**
```
resources/
  db/
    changelog/
      changes/
        v001-create-produit-table.yaml  ← Existing
        v010-create-role-table.yaml     ← New
        v011-create-permission-table.yaml
        ...
      db.changelog-master.yaml          ← Update this
```

### ✍️ Implementation

#### 3.1 Create Role Table Migration

**Location:** `src/main/resources/db/changelog/changes/v010-create-role-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v010-create-role-table
      author: tricol-security
      changes:
        - createTable:
            tableName: role_app
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: description
                  type: VARCHAR(255)
```

**Explanation:**
- `changeSet`: A single database change
- `id`: Unique identifier for this change
- `author`: Who created this change
- `createTable`: Creates a new table
- `autoIncrement`: Auto-generate IDs
- `constraints`: Rules (primary key, unique, not null)

#### 3.2 Create Permission Table Migration

**Location:** `src/main/resources/db/changelog/changes/v011-create-permission-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v011-create-permission-table
      author: tricol-security
      changes:
        - createTable:
            tableName: permission
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: description
                  type: VARCHAR(255)
              - column:
                  name: resource
                  type: VARCHAR(50)
              - column:
                  name: action
                  type: VARCHAR(20)
```

#### 3.3 Create Role-Permission Junction Table

**Location:** `src/main/resources/db/changelog/changes/v012-create-role-permission-table.yaml`

**Concept:** Many-to-many relationship requires a junction table

```yaml
databaseChangeLog:
  - changeSet:
      id: v012-create-role-permission-table
      author: tricol-security
      changes:
        - createTable:
            tableName: role_permission
            columns:
              - column:
                  name: role_id
                  type: BIGINT
                  constraints:
                    nullable: false
              - column:
                  name: permission_id
                  type: BIGINT
                  constraints:
                    nullable: false
        
        - addPrimaryKey:
            tableName: role_permission
            columnNames: role_id, permission_id
            constraintName: pk_role_permission
        
        - addForeignKeyConstraint:
            baseTableName: role_permission
            baseColumnNames: role_id
            referencedTableName: role_app
            referencedColumnNames: id
            constraintName: fk_role_permission_role
            onDelete: CASCADE
        
        - addForeignKeyConstraint:
            baseTableName: role_permission
            baseColumnNames: permission_id
            referencedTableName: permission
            referencedColumnNames: id
            constraintName: fk_role_permission_permission
            onDelete: CASCADE
```

**Explanation:**
- Junction table connects roles and permissions
- Composite primary key (role_id + permission_id)
- Foreign keys ensure referential integrity
- `onDelete: CASCADE`: Delete junction records when role/permission is deleted

#### 3.4 Create User Table Migration

**Location:** `src/main/resources/db/changelog/changes/v013-create-user-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v013-create-user-table
      author: tricol-security
      changes:
        - createTable:
            tableName: user_app
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: username
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: email
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: password
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: enabled
                  type: BOOLEAN
                  defaultValueBoolean: true
                  constraints:
                    nullable: false
              - column:
                  name: account_non_locked
                  type: BOOLEAN
                  defaultValueBoolean: true
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
              - column:
                  name: role_id
                  type: BIGINT
        
        - addForeignKeyConstraint:
            baseTableName: user_app
            baseColumnNames: role_id
            referencedTableName: role_app
            referencedColumnNames: id
            constraintName: fk_user_role
            onDelete: SET NULL
```

**Explanation:**
- `password`: VARCHAR(255) to store BCrypt hash
- `enabled`: Can user log in?
- `account_non_locked`: Is account locked?
- `role_id`: Foreign key to role_app (can be NULL for new users)
- `onDelete: SET NULL`: If role is deleted, set user's role_id to NULL

#### 3.5 Create User Permission Table Migration

**Location:** `src/main/resources/db/changelog/changes/v014-create-user-permission-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v014-create-user-permission-table
      author: tricol-security
      changes:
        - createTable:
            tableName: user_permission
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: user_id
                  type: BIGINT
                  constraints:
                    nullable: false
              - column:
                  name: permission_id
                  type: BIGINT
                  constraints:
                    nullable: false
              - column:
                  name: granted
                  type: BOOLEAN
                  constraints:
                    nullable: false
              - column:
                  name: modified_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
              - column:
                  name: modified_by
                  type: VARCHAR(50)
        
        - addUniqueConstraint:
            tableName: user_permission
            columnNames: user_id, permission_id
            constraintName: uk_user_permission
        
        - addForeignKeyConstraint:
            baseTableName: user_permission
            baseColumnNames: user_id
            referencedTableName: user_app
            referencedColumnNames: id
            constraintName: fk_user_permission_user
            onDelete: CASCADE
        
        - addForeignKeyConstraint:
            baseTableName: user_permission
            baseColumnNames: permission_id
            referencedTableName: permission
            referencedColumnNames: id
            constraintName: fk_user_permission_permission
            onDelete: CASCADE
```

**Explanation:**
- `granted`: true = has permission, false = denied
- `modified_at`: When was this override created?
- `modified_by`: Which admin made the change?
- Unique constraint: One user can't have duplicate permission entries

#### 3.6 Create Refresh Token Table Migration

**Location:** `src/main/resources/db/changelog/changes/v015-create-refresh-token-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v015-create-refresh-token-table
      author: tricol-security
      changes:
        - createTable:
            tableName: refresh_token
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: token
                  type: VARCHAR(500)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: expiry_date
                  type: TIMESTAMP
                  constraints:
                    nullable: false
              - column:
                  name: user_id
                  type: BIGINT
                  constraints:
                    nullable: false
              - column:
                  name: revoked
                  type: BOOLEAN
                  defaultValueBoolean: false
                  constraints:
                    nullable: false
        
        - addForeignKeyConstraint:
            baseTableName: refresh_token
            baseColumnNames: user_id
            referencedTableName: user_app
            referencedColumnNames: id
            constraintName: fk_refresh_token_user
            onDelete: CASCADE
        
        - createIndex:
            tableName: refresh_token
            indexName: idx_refresh_token_token
            columns:
              - column:
                  name: token
```

**Explanation:**
- `token`: The actual refresh token string
- `expiry_date`: When does this token expire?
- `revoked`: Has this token been invalidated?
- Index on token for fast lookups

#### 3.7 Create Audit Log Table Migration

**Location:** `src/main/resources/db/changelog/changes/v016-create-audit-log-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v016-create-audit-log-table
      author: tricol-security
      changes:
        - createTable:
            tableName: audit_log
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: user_id
                  type: BIGINT
              - column:
                  name: username
                  type: VARCHAR(50)
              - column:
                  name: action
                  type: VARCHAR(50)
                  constraints:
                    nullable: false
              - column:
                  name: resource
                  type: VARCHAR(50)
              - column:
                  name: resource_id
                  type: BIGINT
              - column:
                  name: ip_address
                  type: VARCHAR(45)
              - column:
                  name: details
                  type: TEXT
              - column:
                  name: timestamp
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false
        
        - createIndex:
            tableName: audit_log
            indexName: idx_audit_user_id
            columns:
              - column:
                  name: user_id
        
        - createIndex:
            tableName: audit_log
            indexName: idx_audit_timestamp
            columns:
              - column:
                  name: timestamp
        
        - createIndex:
            tableName: audit_log
            indexName: idx_audit_action
            columns:
              - column:
                  name: action
```

**Explanation:**
- No foreign key on user_id (audit logs should persist even if user is deleted)
- Multiple indexes for fast queries
- TEXT column for details (can store JSON)

#### 3.8 Update Master Changelog

**Location:** `src/main/resources/db/changelog/db.changelog-master.yaml`

Add new changesets to the master file:

```yaml
databaseChangeLog:
  # ... existing changesets (v001-v009)
  
  - include:
      file: db/changelog/changes/v010-create-role-table.yaml
  - include:
      file: db/changelog/changes/v011-create-permission-table.yaml
  - include:
      file: db/changelog/changes/v012-create-role-permission-table.yaml
  - include:
      file: db/changelog/changes/v013-create-user-table.yaml
  - include:
      file: db/changelog/changes/v014-create-user-permission-table.yaml
  - include:
      file: db/changelog/changes/v015-create-refresh-token-table.yaml
  - include:
      file: db/changelog/changes/v016-create-audit-log-table.yaml
```

### ✅ Verification

1. **Run migrations:**
   ```bash
   mvn spring-boot:run
   ```
   Liquibase will automatically apply migrations on startup.

2. **Check database:**
   ```sql
   SHOW TABLES;
   ```
   Should see: role_app, permission, role_permission, user_app, user_permission, refresh_token, audit_log

3. **Check Liquibase tracking:**
   ```sql
   SELECT * FROM DATABASECHANGELOG;
   ```
   Should see entries for v010-v016.

---

