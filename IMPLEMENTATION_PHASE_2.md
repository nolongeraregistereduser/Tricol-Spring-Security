# 🚀 PHASE 2: DATABASE MIGRATIONS

## MINI-PHASE 2.1: Create Role Table Migration (10 minutes)

**What:** Create Liquibase migration for role_app table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v010-create-role-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created in correct location

---

## MINI-PHASE 2.2: Create Permission Table Migration (10 minutes)

**What:** Create Liquibase migration for permission table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v011-create-permission-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.3: Create Role-Permission Junction Table (10 minutes)

**What:** Create many-to-many relationship table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v012-create-role-permission-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.4: Create User Table Migration (10 minutes)

**What:** Create user_app table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v013-create-user-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.5: Create User Permission Table Migration (10 minutes)

**What:** Create user_permission table for custom permissions

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v014-create-user-permission-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.6: Create Refresh Token Table Migration (10 minutes)

**What:** Create refresh_token table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v015-create-refresh-token-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.7: Create Audit Log Table Migration (10 minutes)

**What:** Create audit_log table

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v016-create-audit-log-table.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 2.8: Update Master Changelog (10 minutes)

**What:** Register all new migrations in master file

**Action:**
1. Open file: `src/main/resources/db/changelog/db.changelog-master.yaml`
2. Add these lines at the end (after existing includes):

```yaml
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

3. Save file
4. ✅ Verify: File updated

---

## MINI-PHASE 2.9: Test Migrations (10 minutes)

**What:** Run application to apply migrations

**Action:**
1. Make sure MySQL is running
2. Run: `mvn spring-boot:run`
3. Check console output for Liquibase logs
4. Should see: "Successfully applied 7 changesets"
5. ✅ Verify in database:

```sql
USE gestion_bons;
SHOW TABLES;
```

Should see new tables:
- role_app
- permission
- role_permission
- user_app
- user_permission
- refresh_token
- audit_log

6. Stop application (Ctrl+C)

---

## 🎉 PHASE 2 CHECKPOINT

**What you've completed:**
- ✅ Created 7 migration files
- ✅ Updated master changelog
- ✅ Applied migrations to database
- ✅ Verified tables created

**Next:** Seed initial data

---
