# Déployer sur AWS (EC2 + Docker)

Architecture : **une instance EC2** qui exécute 3 conteneurs (`docker-compose.prod.yml`) :

```
Internet ──► :80  Nginx (frontend React) ──/api──► Spring Boot (backend) ──► PostgreSQL (non exposé)
```

## 1. Créer l'instance EC2 (console AWS)

1. EC2 → **Launch instance**.
2. Image : **Ubuntu Server 24.04 LTS**.
3. Type : **t3.small** (2 Go de RAM). Un type de 1 Go (t3.micro) est trop juste pour compiler le backend.
4. **Key pair** : créez-en une (`.pem`) et gardez le fichier précieusement.
5. **Security group** (pare-feu) :
   | Port | Source | Rôle |
   |------|--------|------|
   | 22 (SSH) | **My IP** uniquement | administration |
   | 80 (HTTP) | Anywhere | le site |
   | 443 (HTTPS) | Anywhere | si vous ajoutez un domaine |
   N'ouvrez **jamais** les ports 5432 (base) ni 8080 (backend).
6. Stockage : 20 Go. Lancez l'instance.
7. (Recommandé) **Elastic IP** : associez une IP fixe, sinon l'adresse change à chaque redémarrage.

## 2. Se connecter et installer Docker

Depuis PowerShell (Windows 10/11 inclut `ssh` et `scp`) :

```powershell
ssh -i "C:\chemin\ma-cle.pem" ubuntu@IP_PUBLIQUE
```

Sur le serveur :

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu
sudo apt-get install -y unzip
exit        # puis reconnectez-vous pour activer le groupe docker
```

## 3. Envoyer le projet

Depuis votre PC :

```powershell
scp -i "C:\chemin\ma-cle.pem" tp-etudiants.zip ubuntu@IP_PUBLIQUE:~
```

Sur le serveur :

```bash
unzip tp-etudiants.zip && cd tp-etudiants
cp .env.example .env
nano .env        # changez POSTGRES_PASSWORD !
```

## 4. Lancer

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

La première construction prend quelques minutes. Ouvrez ensuite `http://IP_PUBLIQUE` dans le navigateur.

## Commandes utiles

```bash
docker compose -f docker-compose.prod.yml ps             # état des services
docker compose -f docker-compose.prod.yml logs -f backend # logs du backend
docker compose -f docker-compose.prod.yml up -d --build   # redéployer après modification
docker compose -f docker-compose.prod.yml down            # arrêter (les données restent)
docker compose -f docker-compose.prod.yml down -v         # arrêter ET effacer la base
```

## Aller plus loin

- **HTTPS + nom de domaine** : pointez un domaine vers l'Elastic IP, puis placez **Caddy** (ou Nginx + Certbot) devant le frontend : certificat Let's Encrypt automatique.
- **Sauvegardes** : `docker compose -f docker-compose.prod.yml exec postgres pg_dump -U students students > sauvegarde.sql`
- **Base gérée** : remplacez le conteneur PostgreSQL par **Amazon RDS** (PostgreSQL) et changez `DB_URL`, `DB_USER`, `DB_PASSWORD` du backend.
- **Aucune authentification** n'existe encore sur l'API : tout visiteur peut modifier les données. Ajoutez Spring Security avant d'exposer un vrai projet, ou limitez l'accès au port 80 à votre IP dans le security group.
