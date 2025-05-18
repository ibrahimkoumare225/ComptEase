# ComptEase - Application de gestion financière

ComptEase est une application JavaFX conçue pour aider les petites entreprises et auto-entrepreneurs à gérer leurs finances, clients, factures, obligations fiscales, rapports financiers, et paramètres. Développée par une équipe passionnée, elle combine simplicité, proximité et confiance pour répondre aux besoins réels du terrain. L'application utilise Hibernate pour la persistance des données, SLF4J pour la journalisation, iText pour l’exportation PDF, et FontAwesome pour les icônes.

## Contributeurs

- **Ibrahim Koumare**
- **Ballo Hafiza**
- **Ndeye Sophie**
- **Ludvine Adele**
- **Njonta Kevin**
- **Amadou Sakhir**

Merci à cette équipe pour son travail collaboratif et sa complémentarité entre développement, design, comptabilité, et stratégie.

## Fonctionnalités principales

### Authentification et configuration initiale
- **Connexion/Inscription** (`auth.fxml`) :
  - Page de connexion avec pseudo et mot de passe (`AuthController`).
  - Page d’inscription avec pseudo, email, mot de passe.
  - Bascule entre formulaires via un bouton.
  - Hachage des mots de passe via BCrypt (`UserServiceImpl`).

  ![Page de connexion](screenshots/login-screen.png)

- **Configuration de l’entreprise** (`infos.fxml`, `infos2.fxml`) :
  - **Étape 1** : Saisie des informations de l’entreprise : nom, forme juridique, régime fiscal, profession, SIRET, RIB, capital social, numéro de TVA, adresse, téléphone, email (`InfosController`).
  - **Étape 2** : Saisie des détails de l’activité : nature des ventes, date de création, date de clôture de l’exercice (`Infos2Controller`).

### Tableau de bord (`dashboard.fxml`)
- **Statistiques financières** :
  - Cartes affichant le chiffre d’affaires, les factures payées, les factures en attente, et les dépenses.
- **Graphique** :
  - Histogramme (`BarChart`) montrant les factures entrantes (vert) et sortantes (rouge) par mois.
  - Filtrage par année/mois via `DatePicker`.
- **Classement des clients** :
  - Liste des 3 principaux clients par solde, filtrée par période.
- **Fiscalité** :
  - Liste des 3 principales obligations fiscales avec total, filtrée par période.
- **Profits** :
  - Résumé : chiffre d’affaires, dépenses, impôts, et montant restant, filtré par période.

  ![Tableau de bord](screenshots/dashboard-screen.png)

### Gestion des clients (`clientPage.fxml`, `modifClientForm.fxml`)
- **Liste des clients** :
  - Tableau avec identifiant, nom, prénom, contact, solde, note, et détail.
  - Recherche par mot-clé avec option d’annulation.
- **Ajout de client** (`addClientForm.fxml`) :
  - Formulaire pour nom, prénom, adresse, contact, SIRET, RIB, note.
  - Boutons pour enregistrer ou annuler.
- **Modification/Suppression** (`modifClientForm.fxml`) :
  - Formulaire pour modifier les informations d’un client (nom, prénom, adresse, contact, SIRET, RIB, note).
  - Option de suppression d’un client.
- **Détails du client** :
  - Tableau des ventes/factures associées (article, date, quantité, prix unitaire, prix total, statut).
  - Recherche dans les détails.
  - Options pour voir une facture ou modifier une note (envoi de rappel non implémenté).

  ![Gestion des clients](screenshots/clients-screen.png)

### Gestion des factures (`facture.fxml`, `invoiceDetailPopup.fxml`)
- **Création/Modification de facture ou devis** :
  - Formulaire pour sélectionner un client, ajouter une description, et ajouter des articles (description, prix, quantité).
  - Tableau des articles avec total calculé.
  - Sélection du type (facture/devis) et statut (payée/impayée).
  - Boutons pour enregistrer ou modifier une facture.

  ![Création de facture](screenshots/invoice-creation-screen.png)

- **Liste des factures** :
  - Tableau avec ID, client, date, statut, total, et actions (ex. modifier, supprimer).
- **Détails de la facture** (`invoiceDetailPopup.fxml`) :
  - Pop-up affichant les informations de l’entreprise, du client, et de la facture (numéro, date, statut, type, description, articles, total).
  - Bouton pour exporter en PDF.
- **Exportation** :
  - Exportation des détails d’une facture en PDF via `InvoiceDetailPopupController`.
- **Mise à jour automatique** :
  - Mise à jour du solde des clients après modification des factures.

### Obligations fiscales (`obligationFiscale.fxml`, `modal_obligation.fxml`)
- **Ajout/Modification** (`modal_obligation.fxml`) :
  - Formulaire pour saisir le montant, l’échéance, et le type d’impôt (CFP, IR, CS).
  - Bouton pour valider.
- **Liste des obligations** (`obligationFiscale.fxml`) :
  - Tableau affichant ID, montant, échéance, et type d’impôt.
  - Boutons pour ajouter, modifier, et supprimer une obligation.
- **Validation** :
  - Validation du montant (format numérique uniquement).

### Rapports financiers (`rapportfinancier.fxml`)
- **Statistiques** :
  - Revenus, dépenses, bénéfices, factures totales/payées/impayées, taux de paiement, montant moyen des factures.
- **Graphique** :
  - Histogramme (`BarChart`) des montants par mois.
- **Objectif** :
  - Progression vers un objectif de revenus (fixe à 50 000 €).
- **Exportation** :
  - Exportation du rapport en PDF.

### Paramètres (`parametre.fxml`)
- **Informations de l’entreprise** :
  - Formulaire pour modifier nom, créateur, forme juridique, régime fiscal, activité, profession, SIRET, RIB, capital social, numéro de TVA, adresse, téléphone, email, dates de création et de clôture.
- **Comptes utilisateurs** :
  - Tableau des utilisateurs (pseudo, email, actions).
  - Formulaire pour modifier pseudo, email, et mot de passe.
- **Nous contacter** :
  - Formulaire pour envoyer un message (non implémenté côté serveur).
- **À propos** :
  - Page statique décrivant l’équipe, les valeurs (simplicité, proximité, confiance), et une signature.

  ![Page des paramètres](screenshots/settings-screen.png)

### Autres fonctionnalités
- Gestion des devis (`Devis`) liés à des factures et clients.
- Notifications associées aux clients, factures, et obligations fiscales.
- Gestion des stocks (`Stock`), bien que peu utilisée.
- Menu latéral pour naviguer entre les sections (Dashboard, Clients, Factures, etc.).

## Structure du projet

### Architecture
- **Point d’entrée** (`App.java`) :
  - Initialisation d’Hibernate et lancement de la page de connexion.
- **Modèles** : Entités JPA (`Client`, `Company`, `Article`, `User`, `Devis`, `Invoice`, `Notification`, `ObligationFiscale`, `RapportFinancier`, `Transaction`).
- **DAO** : Couche d’accès aux données avec Hibernate (`ClientDao`, `InvoiceDao`, `CompanyDao`, etc.).
- **Services** : Logique métier (`ClientServiceImpl`, `FactureServiceImpl`, `UserServiceImpl`, etc.).
- **Controllers** : Contrôleurs JavaFX (`AuthController`, `ClientController`, `DashboardController`, `InvoiceController`, `InfosController`, `Infos2Controller`, `InvoiceDetailPopupController`, `ObligationFiscaleController`, `ModifClientFormController`, `ParametreController`, `RapportFinancierController`).
- **FXML** : Interfaces utilisateur (`auth.fxml`, `clientPage.fxml`, `facture.fxml`, `dashboard.fxml`, `infos.fxml`, `infos2.fxml`, `invoiceDetailPopup.fxml`, `modal_obligation.fxml`, `modifClientForm.fxml`, `obligationFiscale.fxml`, `parametre.fxml`, `rapportfinancier.fxml`).
- **Utilitaires** : Gestion des sessions Hibernate (`HibernateUtil`, `SessionManager`), connexion JDBC (`DB`), et tests (`HibernateTest`).

### Base de données (Hibernate)
- **Configuration** (`hibernate.cfg.xml`) :
  - Base de données MySQL (`datab`).
  - Utilisateur `root` (mot de passe non spécifié).
  - Moteur MyISAM (non recommandé, préférer InnoDB).
  - Entités mappées : `Client`, `Company`, `Article`, `User`, `Devis`, `Invoice`, `Notification`, `ObligationFiscale`, `RapportFinancier`, `Transaction`.
  - Propriétés : mise à jour automatique du schéma (`hbm2ddl.auto=update`), affichage des requêtes SQL.

### Dépendances principales
- JavaFX 19.0.2 (ou 23.0.1 selon certains FXML)
- Hibernate (persistance)
- SLF4J (journalisation)
- iText (export PDF)
- BCrypt (hachage des mots de passe)
- FontAwesome (icônes)
- Maven (gestion des dépendances)

## Prérequis

- **JDK 19 ou supérieur** : Assurez-vous d’avoir Java 19+ installé (les FXML mentionnent JavaFX 23.0.1, vérifiez la compatibilité).
- **Maven** : Pour gérer les dépendances et compiler le projet.
- **Base de données** : Configurez une base de données MySQL et mettez à jour `hibernate.cfg.xml`.
  - Exemple de configuration minimale pour MySQL (recommandé avec InnoDB) :
    ```xml
    <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/comptease?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC</property>
    <property name="hibernate.connection.username">root</property>
    <property name="hibernate.connection.password">votre-mot-de-passe</property>
    <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
    <property name="hibernate.hbm2ddl.auto">update</property>
    <property name="hibernate.show_sql">true</property>
    <property name="hibernate.format_sql">true</property>
    ```
- **IDE recommandé** : IntelliJ IDEA pour un meilleur support JavaFX.

## Installation

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/ibrahimkoumare225/ComptEase.git
   ```
2. Configurez la base de données dans `hibernate.cfg.xml` (URL, utilisateur, mot de passe).
3. Importez le projet dans votre IDE (IntelliJ IDEA recommandé).
4. Compilez et exécutez avec Maven :
   ```bash
   mvn clean install
   mvn exec:java
   ```
5. L’application démarre sur la page de connexion (`auth.fxml`).

## Utilisation

1. **Connexion/Inscription** :
   - Créez un compte (pseudo, email, mot de passe) ou connectez-vous avec vos identifiants.
   - Configurez les informations de votre entreprise en deux étapes (informations générales et détails de l’activité).
2. **Tableau de bord** :
   - Consultez les statistiques financières, le graphique des factures, le classement des clients, la fiscalité, et les profits.
   - Filtrez les données par période.
3. **Clients** :
   - Ajoutez, modifiez ou supprimez des clients via les formulaires.
   - Consultez les détails des ventes/factures associées à un client.
4. **Factures** :
   - Créez ou modifiez une facture/devis avec des articles.
   - Consultez la liste des factures et exportez les détails en PDF.
5. **Obligations fiscales** :
   - Ajoutez, modifiez ou supprimez des obligations fiscales.
6. **Rapports financiers** :
   - Consultez les statistiques et exportez un rapport en PDF.
7. **Paramètres** :
   - Modifiez les informations de l’entreprise ou des utilisateurs.
   - Envoyez un message via le formulaire de contact.

## Dépannage

- **Validation des formulaires** : Les formulaires manquent de validation stricte (email, SIRET, RIB, dates, champs numériques, mots de passe).
- **Envoi de rappels** : Fonctionnalité incomplète (bouton `envoyerRappel` masqué et non implémenté).
- **Mot de passe oublié** : Lien présent dans `auth.fxml` mais non fonctionnel.
- **Graphiques** : Les couleurs codées en dur (vert/rouge) peuvent poser des problèmes d’accessibilité.
- **Transactions** : Le contrôleur `TransactionController` est vide (fonctionnalité non implémentée).
- **Objectif de revenus** : Codé en dur à 50 000 € dans `RapportFinancierController`.
- **Export PDF** : Pas de gestion d’erreur si le fichier est déjà ouvert.
- **Image manquante** : `user.jpg` référencée dans `auth.fxml` mais non fournie.
- **Incohérence des tailles de fenêtre** : Les pages de configuration (`infos.fxml`, `infos2.fxml`) sont plus petites (600x400) que les pages principales (1300x720).
- **Base de données** : Utilisation de MyISAM (préférer InnoDB pour les transactions et clés étrangères). Utilisateur `root` sans mot de passe (risque de sécurité).
- **Redondance** : Deux versions de `obligationFiscale.fxml`, une seule devrait être conservée.

## Améliorations suggérées

- Ajouter des validations strictes pour les formulaires (email, SIRET, RIB, dates, mots de passe, etc.).
- Implémenter les fonctionnalités inachevées : envoi de rappels, réinitialisation de mot de passe, gestion des transactions, envoi de messages (formulaire "Nous contacter").
- Rendre l’objectif de revenus configurable dans `RapportFinancierController`.
- Améliorer l’accessibilité des graphiques (couleurs adaptées aux daltoniens, ajout de légendes).
- Ajouter des tests unitaires pour les services et DAO.
- Implémenter une gestion des erreurs plus conviviale (ex. export PDF, échec d’Hibernate).
- Uniformiser les tailles des fenêtres pour une meilleure expérience utilisateur.
- Utiliser InnoDB au lieu de MyISAM dans `hibernate.cfg.xml` et sécuriser l’accès à la base de données.
- Supprimer ou fusionner les fichiers redondants (ex. `obligationFiscale.fxml`).

## Contribuer

Les contributions sont les bienvenues ! Veuillez soumettre une pull request ou ouvrir une issue pour discuter des améliorations. N’hésitez pas à contacter l’équipe si vous avez des questions ou des suggestions.

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.