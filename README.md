# Gestion des incidents de sécurité

Application full-stack de gestion des incidents de sécurité informatique, réalisée dans le cadre du devoir Spring Security, JWT et React.

## Projets

- `backend/` — API Spring Boot 3, Java 17, Spring Web, Spring Security, Spring Data JPA, Validation, H2 et JJWT. Port par défaut : `8080`.
- `frontend/` — React, Vite, Axios et React Router. Port de développement : `5173`.

## Modèle de données

Le backend comprend les entités `User`, `Incident` et `IncidentHistory`, ainsi que les dépôts JPA et les énumérations des rôles, catégories, priorités et statuts. Chaque incident est lié à son créateur et, éventuellement, au technicien auquel il est affecté. L’historique conserve les changements de statut et les notes de traitement. Le diagramme ERD et les choix de relations sont disponibles dans [docs/erd.md](docs/erd.md).

## Authentification et sécurité

Les requêtes et réponses utilisent JSON. L’inscription attribue automatiquement le rôle `EMPLOYEE` ; le client ne peut pas choisir son rôle. Les mots de passe sont hachés avec BCrypt. Les sessions Spring Security sont sans état (stateless). Le CORS autorise le frontend à `http://localhost:5173`.

| Méthode | Point de terminaison | Résultat attendu |
|---|---|---|
| `POST` | `http://localhost:8080/auth/register` | `201 Created` |
| `POST` | `http://localhost:8080/auth/login` | `200 OK` |

### Inscription — `/auth/register`

```json
{
  "fullName": "Amina Exemple",
  "email": "amina@example.com",
  "password": "ChangeMe123!"
}
```

### Connexion — `/auth/login`

```json
{
  "email": "amina@example.com",
  "password": "ChangeMe123!"
}
```

Une authentification réussie renvoie `token`, `tokenType`, `email`, `fullName`, `role`, `issuedAt` et `expiresAt`. Le JWT est valide une heure par défaut. Pour appeler une route protégée, transmettre l’en-tête :

```http
Authorization: Bearer <token>
```

Le secret JWT doit être configuré dans la variable d’environnement `JWT_SECRET` avec au moins 32 octets UTF-8 avant tout déploiement. La valeur de secours incluse dans la configuration est réservée au développement local. Pour le moment, `/auth/**` et `/h2-console/**` sont publics ; les routes `/api/**` requièrent un JWT valide.

## Captures d’écran Postman

### Inscription réussie — `201 Created`

![Capture Postman de l’inscription réussie](images/postman-inscription-201.png)

### Connexion réussie — `200 OK`

![Capture Postman de la connexion réussie](images/postman-connexion-200.png)

## Lancement du projet

### Backend

Prérequis : Java 17 ou supérieur et Maven.

```powershell
cd backend
mvn spring-boot:run
```

L’API est disponible sur `http://localhost:8080`.

### Frontend

Prérequis : Node.js et npm.

```powershell
cd frontend
npm install
npm run dev
```

Vite affiche l’adresse locale du frontend, normalement `http://localhost:5173`.

## État actuel

L’initialisation des deux projets, le modèle de données, le diagramme ERD et l’authentification JWT sont en place. Les API métier, les règles d’autorisation par rôle et les écrans fonctionnels du frontend restent à développer.
