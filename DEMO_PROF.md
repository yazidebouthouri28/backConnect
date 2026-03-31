# DEMO DEVOPS - ConnectCamp - KillerCoda
## Farah | Esprit PI 4SE3 2025-2026

---

## ETAPE 0 — Setup KillerCoda (nouvelle session)

```bash
# Clone le projet (branche avec k8s/)
git clone --branch integrationfarah https://github.com/yazidebouthouri28/Esprit-PI-4SE3-2025-2026-ConnectCamp-Backend.git
cd Esprit-PI-4SE3-2025-2026-ConnectCamp-Backend/Esprit-PI-4SE3-2025-2026-ConnectCamp-Backend

# Déploie tout
kubectl apply -f k8s/
kubectl apply -f k8s/monitoring/
kubectl apply -f k8s/17-hpa.yaml
```

Attends ~3 minutes puis continue.

---

## ETAPE 1 — Cluster KubeADM

```bash
# Montre les noeuds du cluster
kubectl get nodes
```
DIRE: "Cluster kubeadm avec control-plane + worker node01.
      En prod on ajoute des workers via: kubeadm join <master-ip> --token ..."

---

## ETAPE 2 — Tous les pods

```bash
kubectl get pods -n connectcamp
```
DIRE: "Namespace connectcamp contient: backend Spring Boot, frontend Angular,
      MySQL, Prometheus, Grafana. Chaque composant est un Deployment Kubernetes."

---

## ETAPE 3 — Services (exposition réseau)

```bash
kubectl get svc -n connectcamp
```
DIRE: "NodePort pour backend (30089), frontend (30080), Grafana (30030), Prometheus (30090).
      MySQL en ClusterIP = accessible uniquement à l'intérieur du cluster (sécurité)."

---

## ETAPE 4 — ConfigMaps et Secrets

```bash
kubectl get configmap -n connectcamp
kubectl get secret -n connectcamp
```
DIRE: "ConfigMap = variables non-sensibles (URL DB, port, mail host).
      Secret = données sensibles encodées base64: JWT_SECRET, MAIL_PASSWORD."

---

## ETAPE 5 — Stockage persistant (PVC)

```bash
kubectl get pvc -n connectcamp
```
DIRE: "PersistentVolumeClaim garantit que les données MySQL et les fichiers
      uploadés survivent au redémarrage des pods."

---

## ETAPE 6 — Auto-scaling (HPA)

```bash
kubectl get hpa -n connectcamp
```
DIRE: "HorizontalPodAutoscaler: si CPU > 70% ou mémoire > 80%,
      Kubernetes scale automatiquement jusqu'à 3 replicas du backend."

---

## ETAPE 7 — Ressources consommées

```bash
kubectl top pods -n connectcamp
```
DIRE: "On voit la consommation CPU et mémoire en temps réel de chaque pod."

---

## ETAPE 8 — Détails du backend

```bash
kubectl describe deployment backend -n connectcamp | head -40
```
DIRE: "On voit l'image Docker, les probes de santé liveness/readiness,
      les limites de ressources et les variables d'environnement injectées."

---

## ETAPE 9 — Prometheus (port 9090)

```bash
# Port-forward si pas encore fait
kubectl port-forward -n connectcamp svc/prometheus-service 9090:9090 --address=0.0.0.0 &
```

Dans KillerCoda → Custom Ports → 9090
- Aller sur: Status → Targets
- Montrer que backend-service est UP (scrape /actuator/prometheus)

DIRE: "Prometheus collecte les métriques Spring Boot toutes les 15 secondes:
      CPU, mémoire JVM, nombre de requêtes HTTP, temps de réponse."

---

## ETAPE 10 — Grafana (port 30030)

Dans KillerCoda → Custom Ports → 30030
Login: admin / admin123

- Aller sur: Dashboards → Spring Boot (12900)
- Montrer: JVM heap memory, HTTP requests, uptime

DIRE: "Grafana visualise les métriques Prometheus. Le datasource Prometheus
      est provisionné automatiquement via ConfigMap — zéro configuration manuelle."

---

## ETAPE 11 — Events du cluster

```bash
kubectl get events -n connectcamp --sort-by='.lastTimestamp' | tail -15
```
DIRE: "On voit l'historique complet: scheduling, pulling image, liveness probe, etc."

---

## ETAPE 12 — Ingress (si demandé)

```bash
kubectl get ingress -n connectcamp
kubectl describe ingress -n connectcamp
```
DIRE: "L'Ingress route le trafic HTTP: /api/* vers le backend, /* vers le frontend.
      C'est un reverse proxy Kubernetes."

---

## COMMANDES BONUS si prof pose questions

```bash
# Logs du backend en live
kubectl logs -n connectcamp deployment/backend -f --tail=20

# Voir l'image Docker utilisée
kubectl get deployment backend -n connectcamp -o jsonpath='{.spec.template.spec.containers[0].image}'

# Voir les variables d'environnement du backend
kubectl exec -n connectcamp deployment/backend -- env | grep -E "SPRING|JWT|MAIL|DB"

# Simuler du trafic pour Grafana
for i in $(seq 1 30); do curl -s http://localhost:8089/actuator/health > /dev/null; done

# Voir le YAML complet d'un deployment
kubectl get deployment backend -n connectcamp -o yaml
```

---

## QUESTIONS PROF — REPONSES

**Q: Pourquoi KubeADM et pas Minikube ?**
R: "Minikube = dev local, un seul nœud. KubeADM = vrai cluster production
   avec séparation control-plane/workers, RBAC, networking réel."

**Q: C'est quoi un Deployment ?**
R: "Déclare l'état désiré: image, replicas, probes. Kubernetes maintient
   cet état — si un pod crash, il est recréé automatiquement."

**Q: Différence ConfigMap vs Secret ?**
R: "ConfigMap = configs non-sensibles. Secret = données sensibles en base64,
   montées de façon sécurisée, jamais affichées dans les logs."

**Q: Comment fonctionne le HPA ?**
R: "Metrics Server collecte CPU/mémoire. HPA compare aux seuils — si CPU > 70%
   il crée des replicas jusqu'au max (3). Si charge baisse, il scale down."

**Q: Pourquoi Prometheus + Grafana ?**
R: "Prometheus = collecte et stocke les métriques time-series.
   Grafana = visualisation. Spring Boot expose /actuator/prometheus
   que Prometheus scrape toutes les 15 secondes."

**Q: C'est quoi un namespace ?**
R: "Isolation logique dans le cluster. connectcamp isole toutes mes
   ressources — évite les conflits avec d'autres projets."

**Q: C'est quoi un PVC ?**
R: "PersistentVolumeClaim = demande de stockage persistant.
   MySQL garde ses données même si le pod redémarre."

**Q: C'est quoi une liveness probe ?**
R: "Kubernetes vérifie /actuator/health/liveness — si ça échoue,
   il redémarre le pod. La readiness probe vérifie si le pod
   est prêt à recevoir du trafic."

**Q: Comment le backend arrive dans Kubernetes ?**
R: "Jenkins build le projet Maven, crée l'image Docker,
   la push sur Docker Hub (farah893/connectcamp-backend:latest),
   puis kubectl apply -f k8s/ déploie la nouvelle image."

---

## ORDRE RECOMMANDE POUR LA DEMO (10 minutes)

1. kubectl get nodes          (cluster kubeadm)
2. kubectl get pods           (tous les composants)
3. kubectl get svc            (services + ports)
4. kubectl get pvc            (stockage persistant)
5. kubectl get hpa            (auto-scaling)
6. kubectl top pods           (ressources live)
7. Prometheus → Targets       (scraping backend)
8. Grafana → Dashboard        (métriques visuelles)
