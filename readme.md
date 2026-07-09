# 📚 Documentation du Système PowerHouse PCMS

## 1. Présentation Générale

**PowerHouse PCMS** est une application de gestion de Club de fitness développée en Java utilisant l'architecture MVC avec l'interface graphique Swing. Le système gère les activités, les inscriptions des membres, et propose un tableau de bord administrateur pour superviser l'ensemble du club.

### Caractéristiques Principales
- ✅ Authentification sécurisée avec hachage SHA-256
- ✅ Gestion des activités avec capacité maximale
- ✅ Système d'inscription avec statuts (EN_ATTENTE, ACCEPTEE, REFUSEE)
- ✅ Tableau de bord pour les membres et administrateurs
- ✅ Génération de cartes de membre en PDF
- ✅ Statistiques et rapports d'activités
- ✅ Gestion complète des membres

---

## 2. Architecture et Principes de Conception

### 2.1 Patterns Utilisés

**Modèle MVC (Model-View-Controller)**
```
Models/ → Objet métier (Activity, Member, Enrollment, User, Admin)
Controllers/ → Logique métier (AdminController, MemberController, AuthController)
View/ → Interfaces Swing (LoginView, AdminDashboard, MemberDashboard)
DAO/ → Accès aux données (ActivityDAO, MemberDAO, EnrollmentDAO, StatisticsDAO)
```

**Pattern DAO** : Abstraction de la couche données avec classe `databaseConnection` centralisée

### 2.2 Structure des Dossiers

```
PCMS project/
├── Main.java                    # Point d'entrée de l'application
├── config/
│   └── databaseConnection.java  # Gestion de la connexion SQLite
├── models/                      # Objets métier
├── controllers/                 # Logique applicative
├── dao/                         # Accès aux données
├── view/                        # Interfaces graphiques Swing
├── exceptions/                  # Exceptions personnalisées
├── utils/                       # Utilitaires (PDF, Hash, GUI)
├── icons/                       # Images et icônes
├── card_ph/                     # Stockage des cartes PDF
└── database/                    # Base de données SQLite
```

---

## 3. Modèles de Données

### 3.1 Classe `user` (parent)
Classe abstraite contenant les attributs communs à tous les utilisateurs.

**Attributs:**
- `id`: Identifiant unique (clé primaire)
- `nom`: Nom de l'utilisateur
- `prenom`: Prénom
- `role`: ADMIN ou MEMBER
- `email`: Adresse e-mail avec validation regex
- `login`: Identifiant unique (minimum 6 caractères)
- `password`: Mot de passe hashé en SHA-256

**Validations:**
- Email doit respecter le format `^[A-Za-z0-9+_.-]+@(.+)$`
- Login minimum 6 caractères

### 3.2 Classe `member extends user`
Représente un membre du club avec informations supplémentaires.

**Attributs spécifiques:**
- `DateNaissance`: Date de naissance au format String
- `adresse`: Adresse physique
- `Telephone`: Numéro de téléphone
- `Poids`: Poids en kilogrammes (doit être positif)
- `PremierLogin`: Booléen indiquant la première connexion (changement mot de passe obligatoire)

### 3.3 Classe `admin extends user`
Hérite de `user`, simple extension avec rôle ADMIN.

### 3.4 Classe `activity`
Représente une activité du club.

**Attributs:**
- `id`: Identifiant unique
- `nom`: Nom de l'activité (non vide)
- `description`: Description détaillée
- `CapaciteMax`: Nombre maximum de participants (> 0)
- `Horaire`: Horaire de l'activité (format String)

**Validations:**
- Nom ne peut pas être vide
- Capacité doit être un nombre positif

### 3.5 Classe `enrollment`
Représente l'inscription d'un membre à une activité.

**Attributs:**
- `id`: Identifiant unique
- `userId`: Référence vers l'utilisateur
- `activityId`: Référence vers l'activité
- `status`: État de l'inscription
  - `EN_ATTENTE`: En attente d'approbation (par défaut)
  - `ACCEPTEE`: Approuvée par l'administrateur
  - `REFUSEE`: Refusée par l'administrateur

**Contrainte unique:** Une paire (userId, activityId) unique (un membre ne peut s'inscrire qu'une fois par activité)

---

## 4. Schéma de la Base de Données SQLite

```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role TEXT NOT NULL,
    login TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    nom TEXT,
    prenom TEXT,
    date_naissance TEXT,
    adresse TEXT,
    telephone TEXT,
    email TEXT,
    poids REAL,
    first_login INTEGER DEFAULT 1
);

CREATE TABLE activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    description TEXT,
    capacite_max INTEGER NOT NULL,
    horaires TEXT
);

CREATE TABLE enrollments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    activity_id INTEGER NOT NULL,
    status TEXT DEFAULT 'EN_ATTENTE',
    UNIQUE(user_id, activity_id),
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(activity_id) REFERENCES activities(id) ON DELETE CASCADE
);
```

### Utilisateur Admin par Défaut
- **Login:** Admin
- **Mot de passe:** Admin123
- **Email:** Admin@gmail.com

---

## 5. Couche DAO (Data Access Objects)

### 5.1 `memberDAO`
Gère les opérations liées aux utilisateurs.

**Méthodes principales:**
- `authenticate(login, password)`: Authentifie un utilisateur et retourne un objet `user` ou `admin`
- `addMember(member)`: Ajoute un nouveau membre
- `updateMember(member)`: Met à jour les informations d'un membre
- `deleteMember(id)`: Supprime un membre
- `getAllMembers()`: Récupère tous les membres
- `updatePassword(id, newPassword)`: Change le mot de passe

### 5.2 `activityDAO`
Gère les opérations sur les activités.

**Méthodes principales:**
- `addActivity(activity)`: Crée une nouvelle activité
- `updateActivity(activity)`: Modifie une activité existante
- `deleteActivity(id)`: Supprime une activité
- `getAllActivities()`: Liste toutes les activités
- `getFullActivities()`: Liste les activités complètes (au maximum)
- `getActivitiesWithCount()`: Retourne les activités avec le nombre d'inscrits

### 5.3 `enrollmentDAO`
Gère les inscriptions aux activités.

**Méthodes principales:**
- `enroll(userId, activityId)`: Inscrit un membre à une activité
  - ⚠️ Lance `AlreadyEnrolledException` si déjà inscrit
  - ⚠️ Lance `ActivityFullException` si activité pleine
- `cancelEnrollment(userId, activityId)`: Annule l'inscription
- `deleteEnrollment(enrollmentId)`: Supprime une inscription
- `updateStatus(enrollmentId, status)`: Change le statut d'une inscription
- `getAllEnrollments()`: Récupère toutes les inscriptions
- `getEnrollmentsByUser(userId)`: Récupère les inscriptions d'un utilisateur
- `getMostActiveMembers()`: Retourne les membres les plus actifs

### 5.4 `statisticsDAO`
Fournit des statistiques sur l'utilisation du club.

**Méthodes:**
- `getMostPopularActivity()`: Retourne l'activité la plus populaire

---

## 6. Couche Contrôleur

### 6.1 `authController`
Gère l'authentification des utilisateurs.

```java
public user login(String username, String password)
    throws AuthenticationException, DatabaseException
```
- Valide que le nom d'utilisateur et le mot de passe ne sont pas vides
- Appelle `memberDAO.authenticate()`
- Lance `AuthenticationException` si les identifiants sont incorrects

### 6.2 `memberController`
Gère les opérations des membres.

**Méthodes:**
- `enrollInActivity(member, activityId)`: Inscrit le membre à une activité
- `cancelEnrollment(member, activityId)`: Annule l'inscription
- `getMyEnrollments(member)`: Récupère les inscriptions du membre
- `changePassword(member, newPassword)`: Change le mot de passe (minimum 8 caractères)
- `downloadMemberCard(member)`: Génère la carte de membre en PDF

### 6.3 `adminController`
Gère l'administration du club.

**Gestion des activités:**
- `createActivity(nom, desc, cap, horaires)`: Crée une activité
- `updateActivity(id, nom, desc, cap, horaires)`: Modifie une activité
- `deleteActivity(id)`: Supprime une activité
- `listAllActivities()`: Liste toutes les activités
- `listActivitiesWithCount()`: Liste avec comptage
- `listFullActivities()`: Liste des activités complètes

**Gestion des membres:**
- `registerMember(member)`: Enregistre un nouveau membre
- `updateMember(member)`: Modifie un membre
- `deleteMember(id)`: Supprime un membre
- `listAllMembers()`: Liste tous les membres

**Gestion des inscriptions:**
- `listAllEnrollments()`: Liste toutes les inscriptions
- `validateEnrollment(enrollmentId)`: Approuve une inscription
- `refuseEnrollment(enrollmentId)`: Refuse une inscription
- `deleteEnrollment(enrollmentId)`: Supprime une inscription

**Statistiques:**
- `getPopularityReport()`: Rapport de popularité
- `getMostActiveMembers()`: Membres les plus actifs

---

## 7. Gestion des Exceptions

### Hiérarchie des Exceptions

```
powerHouseException (classe parent)
├── AuthenticationException
├── DatabaseException
├── ValidationException
├── SecurityException
├── ActivityFullException
├── AlreadyEnrolledException
└── ExportException
```

### Description des Exceptions

| Exception | Contexte | Message |
|-----------|----------|---------|
| `powerHouseException` | Parent générique | Base de toutes les exceptions métier |
| `DatabaseException` | Erreurs BDD | Impossible d'initialiser les tables, d'authentifier, etc. |
| `AuthenticationException` | Authentification échouée | Identifiants incorrects, champs vides |
| `ValidationException` | Données invalides | Nom vide, capacité négative, email invalide, login < 6 caractères |
| `SecurityException` | Sécurité/Mot de passe | Algorithme non trouvé, mot de passe < 8 caractères |
| `ActivityFullException` | Activité pleine | L'activité a atteint sa capacité maximale |
| `AlreadyEnrolledException` | Double inscription | Le membre est déjà inscrit à cette activité |
| `ExportException` | Export PDF | Impossible de générer la carte de membre |

---

## 8. Interface Utilisateur (Swing)

### 8.1 `loginView`
**Écran de connexion** accessible au démarrage.

- Champs: Nom d'utilisateur, Mot de passe
- Boutons: Connexion, S'enregistrer
- Redirection vers le tableau de bord approprié selon le rôle

### 8.2 `memberDashboard`
**Tableau de bord des membres**

Onglets:
1. **Activités disponibles** - Liste des activités avec capacités, permet l'inscription
2. **Mes inscriptions** - Liste des inscriptions du membre avec statuts
3. **Mon profil** - Informations personnelles et téléchargement de carte

### 8.3 `adminDashboard`
**Tableau de bord administrateur**

Onglets:
1. **Gestion des activités** - Créer, modifier, supprimer des activités
2. **Gestion des membres** - Créer, modifier, supprimer des membres
3. **Gestion des inscriptions** - Approuver, refuser ou supprimer les inscriptions
4. **Statistiques** - Activité la plus populaire, membres les plus actifs

### 8.4 `ChangePasswordView`
Écran de changement de mot de passe pour la première connexion.

---

## 9. Utilitaires

### 9.1 `passwordHasher`
Hache les mots de passe en **SHA-256** codés en Base64.

```java
public static String hashPassword(String password) throws SecurityException
```

### 9.2 `guiHelper`
Classe utilitaire pour le styling des composants Swing.

**Couleurs définies:**
- `COLOUR_BACKGROUND`: Gris clair (245, 247, 250)
- `COLOUR_ACCENT`: Bleu (30, 90, 200)
- `COLOUR_DANGER`: Rouge (190, 35, 35)
- `COLOUR_SUCCESS`: Vert (30, 140, 70)
- `COLOUR_DARK`: Gris foncé (40, 40, 55)
- `COLOUR_MUTED`: Gris moyen (100, 100, 120)

**Méthodes:**
- `createButton(label, backgroundColor)`: Crée un bouton stylisé
- `applyTableStyle(table)`: Applique un style aux tableaux
- `createReadOnlyTableModel(columnNames)`: Crée un modèle de tableau non-éditable

### 9.3 `PDFGenerator`
Génère des cartes de membre au format PDF (A6).

```java
public static void generateMemberCard(member member) throws ExportException
```

- Emplacement: `card_ph/carte_[NOM]_[PRENOM].pdf`
- Contient: ID, Nom, Prénom, Email, Téléphone

### 9.4 `databaseConnection`
Gère la connexion à la base de données SQLite.

```java
public static Connection getConnection() throws DatabaseException
public static void initializeDatabase() throws DatabaseException, SecurityException
```

- **Base de données:** powerhouse.db
- **URL JDBC:** `jdbc:sqlite:database/powerhouse.db`
- Crée automatiquement les tables au premier lancement
- Crée l'administrateur par défaut

---

## 10. Flux Opérationnels

### 10.1 Flux de Connexion

```
1. LoginView → Utilisateur entre ses identifiants
2. AuthController.login() → Validation des champs
3. MemberDAO.authenticate() → Recherche dans la BD
4. Comparaison des mots de passe hashés
5. Retour d'un objet user/admin ou exception
6. Redirection vers le tableau de bord approprié
```

### 10.2 Flux d'Inscription à une Activité

```
1. MemberDashboard → Membre sélectionne une activité
2. MemberController.enrollInActivity()
3. EnrollmentDAO.enroll()
   - Vérification: pas de double inscription
   - Vérification: capacité disponible
   - Création de l'inscription (statut: EN_ATTENTE)
4. AdminDashboard → Admin reçoit la demande
5. AdminController.validateEnrollment() ou refuseEnrollment()
6. Mise à jour du statut
```

### 10.3 Flux de Création d'Activité (Admin)

```
1. AdminDashboard → Admin entre les détails
2. AdminController.createActivity()
3. ActivityDAO.addActivity()
4. Insertion dans la table 'activities'
5. Rafraîchissement de l'affichage
```

---

## 11. Flux de Démarrage de l'Application

```
1. Main.java → Exécution
2. SwingUtilities.invokeLater() → Initialisation Swing
3. databaseConnection.initializeDatabase()
   - Création des tables (users, activities, enrollments)
   - Insertion de l'admin par défaut
   - Activation des clés étrangères (PRAGMA)
4. LoginView affichée
5. Utilisateur se connecte
6. Dashboard du rôle approprié chargé
```

---

## 12. Points Critiques et Considérations

### 12.1 Sécurité
- ✅ Mots de passe hashés en SHA-256 + Base64
- ✅ Clés étrangères activées dans SQLite
- ⚠️ Pas de tokens/sessions gérés
- ⚠️ Pas de rate limiting sur les tentatives de connexion

### 12.2 Gestion de la Capacité
- Les activités sont vérifiées avec la capacité maximale
- Les statuts EN_ATTENTE et ACCEPTEE comptent contre la capacité
- Les statuts REFUSEE ne comptent pas

### 12.3 Statuts des Inscriptions
- **EN_ATTENTE:** Défaut, en attente d'approbation admin
- **ACCEPTEE:** Membre confirmé pour l'activité
- **REFUSEE:** Demande rejetée, place libérée

---

## 13. Technologies et Dépendances

- **Langage:** Java (JDK 11+)
- **Interface:** Swing (AWT)
- **Base de données:** SQLite 3
- **PDF:** iTextPDF
- **JDBC Driver:** sqlite-jdbc

---

## 14. Déploiement et Maintenance

### Démarrage
```bash
javac -d . *.java
java Main
```

### Fichiers critiques
- powerhouse.db - Base de données persistante
- icons - Images et icônes de l'application
- card_ph - Stockage des cartes PDF générées

### Sauvegarde
Copier régulièrement powerhouse.db pour éviter la perte de données.


