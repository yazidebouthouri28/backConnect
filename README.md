# BackConnect - Architecture de Base

Ce projet sert de base architecturale pour le système CampConnect. Il inclut une gestion robuste des utilisateurs, de la sécurité JWT et une configuration hybride MySQL + MongoDB.

## 🚀 Fonctionnalités Incluses (Base)
- **Authentification & Sécurité** : Spring Security + JWT (Stateless).
- **Gestion des Utilisateurs** : Profil complet, Rôles (CLIENT, ORGANISATEUR, AGENT_SECURITE, etc.).
- **Base Hybride** :
    - **MySQL (JPA)** : Pour les données relationnelles et transactionnelles.
    - **MongoDB** : Pour les données flexibles (avis, images, métadonnées).
- **Précision Financière** : Migration complète vers `BigDecimal` pour tous les calculs monétaires.
- **Documentation API** : Swagger UI intégré.

## 🛠️ Pré-requis
1. **Java 17**.
2. **MySQL Server** (Créez une base nommée `campconnect`).
3. **MongoDB Server** (Port par défaut 27017).

## 📁 Structure du Projet & Cohérence
Pour ajouter un nouveau module de manière cohérente avec cette architecture, suivez ces patterns :

1. **Entity** : Dans `com.camping.projet.entity`. Utilisez `BigDecimal` pour les prix.
2. **DTO** : Dans `com.camping.projet.dto` (Request/Response).
3. **Repository** : Dans `com.camping.projet.repository`.
    - Les repos MongoDB **doivent** être placés dans le sous-package `.mongo` pour éviter les conflits de scan.
4. **Service** : 
    - Interface dans `.service`.
    - Implementation dans `.service.impl`.
5. **Controller** : Dans `com.camping.projet.controller`. Utilisez `@Valid` pour le contrôle de saisie.

## 🔑 Configuration Sécurité
Les endpoints sont sécurisés par défaut. Pour tester :
1. Créez un compte via `/api/auth/signup`.
2. Connectez-vous via `/api/auth/signin` pour obtenir un Token JWT.
3. Ajoutez le token dans l'en-tête `Authorization: Bearer <votre_token>` pour vos requêtes suivantes.

## 📝 Test
Accédez à Swagger : `http://localhost:8080/swagger-ui/index.html`
