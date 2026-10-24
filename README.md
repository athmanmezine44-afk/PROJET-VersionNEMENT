# TravauxPratique - Versionnement des BDD avec FlyWay et Liquibase \&\& Déploiement sur AWS



================================================================================

&#x20;                   JOURNAL DE BORD ET SUIVI DU PROJET

================================================================================

Projet       : Application de Gestion des Étudiants (Master M2I - Génie Logiciel)

Architecture : Full-Stack (Spring Boot 3 + React 19 / Vite + Flyway + H2/PostgreSQL)

Date         : Octobre 2026

Auteur       : Équipe Génie Logiciel / Antigravity AI

================================================================================



\--------------------------------------------------------------------------------

TABLE DES MATIÈRES

\--------------------------------------------------------------------------------

1\. Contexte et Objectifs Globaux

2\. Ce qui a été fait (LE QUOI)

3\. Justifications Techniques et Architecturales (LE POURQUOI)

4\. Détails d'Implémentation par Fichier (LE COMMENT)

5\. Guide d'Exécution et Commandes Utiles (LES COMMANDES)

6\. Tableau de Suivi de Progrès (CHECKLIST \& PROCHAINES ÉTAPES)



================================================================================

1\. CONTEXTE ET OBJECTIFS GLOBAUX

================================================================================

L'application se compose de :

&#x20; - Un Backend REST Spring Boot 3 (Java 17/21) écoutant sur le port 8081.

&#x20; - Un Frontend SPA React 19 / Vite écoutant sur le port 5173.



L'objectif de cette phase préparatoire était de corriger les anomalies existantes

et de doter le projet d'une persistance relationnelle robuste gérée par un outil de

migration de schéma (Flyway). Cela permet de préparer sereinement :

&#x20; - Le TP 1 : Déploiement Cloud sur Azure (App Service + PostgreSQL).

&#x20; - Le TP 2 : Gestion du cycle de vie et versionnement de base de données (Flyway).



================================================================================

2\. CE QUI A ÉTÉ FAIT (LE QUOI)

================================================================================



\[FRONTEND]

&#x20; 1. Correction du port API : le message d'erreur d'App.jsx mentionnait le port 8080

&#x20;    alors que le serveur tourne sur le port 8081.

&#x20; 2. Intégration du flux de modification :

&#x20;    - La prop `onEdit={setEditingStudent}` a été passée à <StudentTable />.

&#x20;    - Le composant <EditStudentModal /> est désormais rendu conditionnellement

&#x20;      lorsqu'un étudiant est sélectionné pour édition.

&#x20; 3. Styles CSS :

&#x20;    - Ajout de la classe `.btn-warning` (bouton ambre/orange d'édition).

&#x20;    - Ajout des styles pour la modale (`.modal-overlay`, `.modal-box`,

&#x20;      `.modal-header`, `.modal-close`) avec animations d'apparition.



\[BACKEND]

&#x20; 1. Persistance relationnelle :

&#x20;    - Remplacement du stockage volatil (`ArrayList`) par Spring Data JPA.

&#x20;    - Création de l'entité JPA `Student` mappée sur la table SQL `student`.

&#x20;    - Création du repository `StudentRepository` étendant `JpaRepository`.

&#x20;    - Adaptation du service `StudentService` avec gestion des transactions (`@Transactional`).

&#x20; 2. Gestion des migrations Flyway :

&#x20;    - Ajout de `flyway-core` dans `pom.xml`.

&#x20;    - Création de la migration initiale `V1\_\_init\_schema.sql` créant la table

&#x20;      `student` et insérant 5 étudiants initiaux.

&#x20;    - Configuration de `application.properties` avec `ddl-auto=validate` (pour que

&#x20;      seul Flyway gère les changements de schéma).

&#x20; 3. Base de données \& Drivers :

&#x20;    - Configuration de la base H2 in-memory pour le dev et les tests.

&#x20;    - Activation de la console web H2 (`/h2-console`).

&#x20;    - Ajout du driver PostgreSQL dans `pom.xml` (prêt pour Azure).

&#x20; 4. Tests automatisés :

&#x20;    - Création de tests d'intégration complets avec MockMvc dans

&#x20;      `StudentsApiApplicationTests.java` (5 tests validés avec succès).



================================================================================

3\. JUSTIFICATIONS TECHNIQUES ET ARCHITECTURALES (LE POURQUOI)

================================================================================



\* Pourquoi remplacer l'ArrayList par JPA ?

&#x20; - L'ArrayList en mémoire perdait toutes les données à chaque arrêt du serveur.

&#x20; - Elle n'était pas thread-safe (risque de corruption lors d'accès concurrents).

&#x20; - Elle ne correspond pas aux architectures cloud professionnelles scalables.



\* Pourquoi Flyway plutôt que la génération automatique Hibernate (ddl-auto=update) ?

&#x20; - `ddl-auto=update` est proscrit en production : il peut altérer ou supprimer des

&#x20;   colonnes de façon imprévisible, ne conserve aucun historique et n'est pas reproductible.

&#x20; - Flyway applique le principe d'Infrastructure as Code (IaC) à la base de données :

&#x20;   chaque changement est versionné (V1, V2, ...), audité dans la table

&#x20;   `flyway\_schema\_history`, reproductible entre dev, test et production Azure.



\* Pourquoi ddl-auto=validate ?

&#x20; - C'est la bonne pratique de référence dans l'industrie : Flyway applique les DDL,

&#x20;   et Hibernate se contente de vérifier au démarrage que les entités Java correspondent

&#x20;   exactement au schéma réel de la base.



\* Pourquoi la syntaxe "BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY" ?

&#x20; - C'est la syntaxe standard SQL:2008. Elle fonctionne à l'identique sur H2 (local)

&#x20;   et sur PostgreSQL (Azure), évitant d'avoir à réécrire les scripts lors du déploiement cloud.



================================================================================

4\. DÉTAILS D'IMPLÉMENTATION PAR FICHIER (LE COMMENT)

================================================================================



\[Fichiers Frontend]

\------------------

\* students-frontend/src/App.jsx :

&#x20; - Port corrigé à la ligne 25 : "port 8081".

&#x20; - Connexion de l'état `editingStudent` avec `StudentTable` (`onEdit={setEditingStudent}`).

&#x20; - Affichage de `EditStudentModal` avec handlers `handleEditSave` et fermeture.



\* students-frontend/src/App.css :

&#x20; - Ajout de `.btn-warning` (background: #f59e0b).

&#x20; - Ajout des règles de modale (`.modal-overlay` en position fixed avec backdrop-filter blur,

&#x20;   `.modal-box` avec ombrage et bordures arrondies, animations keyframes fadeIn \& slideUp).



\[Fichiers Backend]

\-----------------

\* students-api/pom.xml :

&#x20; - Dépendances ajoutées :

&#x20;   - `org.springframework.boot:spring-boot-starter-data-jpa`

&#x20;   - `org.flywaydb:flyway-core`

&#x20;   - `com.h2database:h2` (runtime)

&#x20;   - `org.postgresql:postgresql` (runtime)



\* students-api/src/main/java/com/tp/students/model/Student.java :

&#x20; - Annotations `@Entity`, `@Table(name = "student")`, `@Id`,

&#x20;   `@GeneratedValue(strategy = GenerationType.IDENTITY)`.

&#x20; - Mappage des colonnes avec `@Column(name = "...", nullable = false)`.



\* students-api/src/main/java/com/tp/students/repository/StudentRepository.java :

&#x20; - Interface étendant `JpaRepository<Student, Long>` avec annotation `@Repository`.



\* students-api/src/main/java/com/tp/students/service/StudentService.java :

&#x20; - Injection du `StudentRepository`.

&#x20; - Méthodes CRUD adaptées (`findAll`, `findById`, `save`, `deleteById`).

&#x20; - Annotation `@Transactional` au niveau classe et `@Transactional(readOnly = true)` sur la lecture.



\* students-api/src/main/resources/db/migration/V1\_\_init\_schema.sql :

&#x20; - DDL : Création de la table `student`.

&#x20; - DML : Insertion des 5 enregistrements de base (Alice, Bob, Camille, David, Emma).



\* students-api/src/main/resources/application.properties :

&#x20; - `server.port=8081`

&#x20; - `spring.datasource.url=jdbc:h2:mem:studentsdb;DB\_CLOSE\_DELAY=-1;DB\_CLOSE\_ON\_EXIT=FALSE`

&#x20; - `spring.h2.console.enabled=true` sur `/h2-console`

&#x20; - `spring.jpa.hibernate.ddl-auto=validate`

&#x20; - `spring.flyway.enabled=true` et `spring.flyway.locations=classpath:db/migration`

&#x20; - Modèle de variables d'environnement documenté pour PostgreSQL Azure.



\* students-api/src/test/java/com/tp/students/StudentsApiApplicationTests.java :

&#x20; - Tests d'intégration Spring Boot Test + MockMvc validant Flyway et les 4 verbes HTTP

&#x20;   (GET, POST, PUT, DELETE).



================================================================================

5\. GUIDE D'EXÉCUTION ET COMMANDES UTILES (LES COMMANDES)

================================================================================



\-----------------------------------------------------------------

A. BACKEND (Spring Boot)

\-----------------------------------------------------------------

Emplacement : Projet - Final/students-api



1\. Compiler et exécuter les tests :

&#x20;  mvn clean test



2\. Lancer l'application en mode développement :

&#x20;  mvn spring-boot:run



3\. Packager le binaire JAR (pour conteneur Docker ou déploiement Azure) :

&#x20;  mvn clean package -DskipTests

&#x20;  -> Le fichier JAR est généré dans : target/students-api-1.0.0.jar



4\. Accès aux interfaces backend :

&#x20;  - API REST : http://localhost:8081/api/students

&#x20;  - Console H2 : http://localhost:8081/h2-console

&#x20;    \* JDBC URL : jdbc:h2:mem:studentsdb

&#x20;    \* User Name : sa

&#x20;    \* Password : (laisser vide)

&#x20;    \* Remarque : Vous y verrez la table "STUDENT" et la table "FLYWAY\_SCHEMA\_HISTORY".



\-----------------------------------------------------------------

B. FRONTEND (React / Vite)

\-----------------------------------------------------------------

Emplacement : Projet - Final/students-frontend



1\. Installer les dépendances (si nouveau clonage) :

&#x20;  npm install



2\. Lancer le serveur de développement :

&#x20;  npm run dev

&#x20;  -> Accessible sur : http://localhost:5173



3\. Linter le code (Oxlint) :

&#x20;  npm run lint



4\. Générer le bundle de production statique (dist) :

&#x20;  npm run build



