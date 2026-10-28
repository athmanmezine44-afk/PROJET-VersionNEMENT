# TP : Application de gestion des étudiants

API REST **Spring Boot** avec base de données **PostgreSQL** + interface **React** (Vite).

```
tp-etudiants/
├── backend/             # Spring Boot 3 (Maven, Java 17+)
├── frontend/            # React + Vite
├── docker-compose.yml        # PostgreSQL pour le développement
├── docker-compose.prod.yml   # application complète pour un serveur
└── DEPLOY-AWS.md             # guide de déploiement sur AWS EC2
```

## 1. Lancer PostgreSQL

**Option A : Docker (recommandé)**, depuis la racine du projet :

```bash
docker compose up -d
```

**Option B : PostgreSQL déjà installé**, créer la base et l'utilisateur :

```sql
CREATE USER students WITH PASSWORD 'students';
CREATE DATABASE students OWNER students;
```

Pour utiliser d'autres identifiants, définir les variables d'environnement `DB_URL`, `DB_USER` et `DB_PASSWORD` (voir `application.properties`).

## 2. Lancer le backend

Prérequis : Java 17+ et Maven.

```bash
cd backend
mvn spring-boot:run
```

- API : http://localhost:8080/api/students
- Documentation interactive (Swagger) : http://localhost:8080/swagger-ui.html

Les tables sont créées automatiquement au démarrage. Au premier lancement, 6 étudiants de démonstration sont insérés (désactivable : `app.seed.enabled=false`). Pour repartir de zéro : `docker compose down -v`.

Lancer les tests : `mvn test` (ils utilisent H2 en mémoire : PostgreSQL n'est pas nécessaire).

### Endpoints

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/students` | Liste. Paramètres facultatifs : `q` (recherche nom/prénom/email), `filiere`, `sortBy` (`id`, `nom`, `prenom`, `email`, `filiere`, `age`, `moyenne`), `order` (`asc`/`desc`) |
| GET | `/api/students/{id}` | Détails d'un étudiant (404 si inconnu) |
| POST | `/api/students` | Création (201 + en-tête `Location`) |
| PUT | `/api/students/{id}` | Modification complète |
| DELETE | `/api/students/{id}` | Suppression (204) |
| GET | `/api/students/stats` | Statistiques : total, moyenne générale, meilleure moyenne, taux de réussite, détail par filière |
| GET | `/api/students/filieres` | Liste des filières existantes |

Exemple :

```bash
curl -X POST http://localhost:8080/api/students -H "Content-Type: application/json" \
  -d '{"nom":"Alami","prenom":"Hind","email":"hind.alami@etu.ma","filiere":"Data Science","age":21,"moyenne":15.25}'
```

### Validation et erreurs

Règles : nom, prénom, email (valide et **unique**), filière obligatoires ; âge entre 16 et 100 ; moyenne entre 0 et 20.
Toutes les erreurs ont le même format JSON :

```json
{
  "timestamp": "2026-10-19T20:40:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Les données envoyées sont invalides",
  "fieldErrors": { "age": "L'âge minimum est 16 ans" }
}
```

Codes utilisés : `400` données invalides, `404` étudiant introuvable, `409` email déjà utilisé.

### Architecture du backend

```
controller/   StudentController       → routes HTTP uniquement
service/      StudentService          → logique métier (recherche, unicité email, stats)
repository/   StudentRepository       → accès base (Spring Data JPA)
model/        Student                 → entité JPA (table « students »)
dto/          StudentRequest/Response/Stats → ce qui entre et sort de l'API
exception/    GlobalExceptionHandler  → erreurs JSON uniformes
config/       CorsConfig, DataSeeder  → CORS et données de démonstration
```

## 3. Lancer le frontend

Prérequis : Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

Ouvrir http://localhost:5173. L'interface permet de : voir les statistiques, **rechercher**, **filtrer par filière**, **trier**, **ajouter**, **modifier** et **supprimer** un étudiant, avec affichage des erreurs de validation sous chaque champ.

L'URL de l'API se change avec la variable `VITE_API_URL` (défaut : `http://localhost:8080`).

## CORS

Les origines autorisées se règlent dans `app.cors.allowed-origins` (`application.properties`).
