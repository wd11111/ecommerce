# Kubernetes Deployment Guide

This document describes the production-grade Kubernetes deployment for the E-commerce microservices system.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                        Kubernetes Cluster                            │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐                                                    │
│  │   Ingress   │ ← External Traffic (HTTPS)                         │
│  │   (NGINX)   │   - TLS termination, Path-based routing            │
│  └──────┬──────┘                                                    │
│         │                                                            │
│  ┌──────┴──────────────────────────────────────┐                    │
│  │                                              │                    │
│  ▼                  ▼                  ▼                            │
│ ┌────────────┐ ┌────────────┐ ┌────────────┐                        │
│ │ Identity   │ │  Product   │ │   Cart     │                        │
│ │  Service   │ │  Service   │ │  Service   │                        │
│ │   :8081    │ │   :8082    │ │   :8083    │                        │
│ └─────┬──────┘ └─────┬──────┘ └─────┬──────┘                        │
│       │              │              │                                │
│       ▼              ▼              ▼                                │
│ ┌────────────────────────────────────────────┐                      │
│ │            PostgreSQL Instance             │                      │
│ │  ┌──────────┐ ┌──────────┐ ┌──────────┐   │                      │
│ │  │identity_ │ │product_  │ │ cart_db  │   │                      │
│ │  │   db     │ │   db     │ │          │   │                      │
│ │  └──────────┘ └──────────┘ └──────────┘   │                      │
│ └────────────────────────────────────────────┘                      │
│                                                                      │
│  ┌─────────────────────────────────────────────┐                    │
│  │          Monitoring Namespace                │                    │
│  │  ┌────────────┐    ┌────────────┐           │                    │
│  │  │ Prometheus │    │  Grafana   │           │                    │
│  │  │   :9090    │    │   :3000    │           │                    │
│  │  └────────────┘    └────────────┘           │                    │
│  └─────────────────────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────────┘
```

## Design Decisions

### Why No API Gateway?

We chose to use **Kubernetes Ingress directly** instead of Spring Cloud Gateway because:

| Feature | Spring Cloud Gateway | K8s Ingress (NGINX) |
|---------|---------------------|---------------------|
| Path-based routing | ✅ | ✅ |
| TLS termination | ✅ | ✅ |
| Rate limiting | ✅ | ✅ (annotations) |
| Load balancing | ✅ | ✅ (via Service) |
| Circuit breakers | ✅ | ❌ |
| Extra pod required | ❌ Yes | ✅ No (already exists) |

Since our routing is simple path-based, Ingress is sufficient and reduces:
- **Complexity**: One less service to maintain
- **Resource usage**: No extra pods consuming CPU/RAM
- **Latency**: One less network hop

### Why No Eureka (Discovery Service)?

Kubernetes provides **native service discovery** via CoreDNS:
- DNS names: `<service>.<namespace>.svc.cluster.local`
- Example: `identity-service.ecommerce-prod.svc.cluster.local:8081`

### Database-per-Service Pattern

Each microservice has its own isolated database within a shared PostgreSQL instance:

| Service | Database | Connection URL |
|---------|----------|----------------|
| identity-service | `identity_db` | `postgres:5432/identity_db` |
| product-service | `product_db` | `postgres:5432/product_db` |
| cart-service | `cart_db` | `postgres:5432/cart_db` |

> 💡 **Note**: Services share a PostgreSQL instance but have complete data isolation. For production at scale, consider separate PostgreSQL instances per service.

## Prerequisites

- Kubernetes cluster (v1.25+)
- `kubectl` configured
- NGINX Ingress Controller installed
- (Optional) cert-manager for TLS

## Namespaces

| Namespace | Purpose |
|-----------|---------|
| `ecommerce-prod` | Production workloads |
| `ecommerce-dev` | Development/staging |
| `monitoring` | Prometheus, Grafana |

## Quick Start

### 1. Create Namespaces

```bash
kubectl apply -f k8s/namespaces.yaml
```

### 2. Deploy Infrastructure

```bash
# Secrets (DB credentials)
kubectl apply -f k8s/infra/secrets.yaml

# PostgreSQL
kubectl apply -f k8s/infra/postgres/postgres.yaml

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgres -n ecommerce-prod --timeout=120s

# Network Policies
kubectl apply -f k8s/infra/network-policies.yaml

# PodDisruptionBudgets
kubectl apply -f k8s/infra/pdb.yaml

# Monitoring stack
kubectl apply -f k8s/infra/monitoring/monitoring.yaml
```

### 3. Build and Push Docker Images

```bash
# From project root
cd microservices

# Build each service
for service in config-server identity-service product-service cart-service; do
  cd $service
  mvn clean package -DskipTests
  docker build -t ecommerce/$service:latest .
  cd ..
done

# Push to your registry (adjust for your registry)
# docker tag ecommerce/identity-service:latest your-registry/identity-service:latest
# docker push your-registry/identity-service:latest
```

### 4. Deploy Applications

```bash
# Config Server (optional)
kubectl apply -f k8s/app/config-server/config-server.yaml

# Core services
kubectl apply -f k8s/app/identity-service/identity-service.yaml
kubectl apply -f k8s/app/product-service/product-service.yaml
kubectl apply -f k8s/app/cart-service/cart-service.yaml

# Ingress
kubectl apply -f k8s/ingress/ingress.yaml
```

### 5. Verify Deployment

```bash
# Check all pods are running
kubectl get pods -n ecommerce-prod

# Check services
kubectl get svc -n ecommerce-prod

# Check HPA status
kubectl get hpa -n ecommerce-prod

# Check Ingress
kubectl get ingress -n ecommerce-prod
```

## Routing

The Ingress routes requests directly to microservices:

| Path | Service | Port |
|------|---------|------|
| `/api/auth/**` | identity-service | 8081 |
| `/api/products/**` | product-service | 8082 |
| `/api/categories/**` | product-service | 8082 |
| `/api/cart/**` | cart-service | 8083 |

## Configuration

### Environment Variables

All services use the `kubernetes` Spring profile with environment variables:

| Variable | Description | Source |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Set to `kubernetes` | Deployment |
| `SPRING_DATASOURCE_URL` | Database URL | Deployment |
| `SPRING_DATASOURCE_USERNAME` | DB username | Secret |
| `SPRING_DATASOURCE_PASSWORD` | DB password | Secret |

### Secrets

Database credentials in `k8s/infra/secrets.yaml`:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
type: Opaque
data:
  username: <base64-encoded>
  password: <base64-encoded>
```

> ⚠️ **Production**: Use external secret management (Vault, AWS Secrets Manager)

## Security

### Network Policies

Default deny-all with explicit allow rules:
- Ingress Controller → Microservices (ports 8081-8083)
- Microservices → PostgreSQL (port 5432)
- All pods → Config Server (port 8888)

### Pod Security

All deployments enforce:
- `runAsNonRoot: true`
- `runAsUser: 1000`
- Resource limits

## Monitoring

### Prometheus Metrics

Services expose metrics at `/actuator/prometheus`. Pod annotations enable scraping:

```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/path: "/actuator/prometheus"
  prometheus.io/port: "8081"
```

### Access Monitoring

```bash
kubectl port-forward svc/prometheus -n monitoring 9090:9090
kubectl port-forward svc/grafana -n monitoring 3000:3000
```

## Scaling

HPA configured for each service:
- Min: 2 replicas
- Max: 5 replicas  
- Target: 70% CPU

```bash
kubectl get hpa -n ecommerce-prod
kubectl scale deployment identity-service -n ecommerce-prod --replicas=3
```

## Directory Structure

```
k8s/
├── namespaces.yaml
├── README.md
├── app/
│   ├── cart-service/
│   ├── config-server/
│   ├── identity-service/
│   └── product-service/
├── infra/
│   ├── monitoring/
│   ├── postgres/
│   ├── network-policies.yaml
│   ├── pdb.yaml
│   └── secrets.yaml
└── ingress/
    └── ingress.yaml
```