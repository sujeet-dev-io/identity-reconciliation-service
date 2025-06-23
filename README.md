# 🕵️‍♂️ Identity Reconciliation Service

This Spring Boot backend solves the problem of contact identity reconciliation across multiple records using shadow linking of phone numbers and emails.

---

## 📦 Features

* `/identify` endpoint to consolidate contact info
* Maintains `primary` and `secondary` contact relations
* Automatically updates links on overlapping data
* Stores only unique emails/phones
* Robust error handling and logging

---

## 🔧 Tech Stack

* Java 17+ / Spring Boot 3.4
* MySQL
* Lombok
* ModelMapper

---

## 📌 API Endpoint

### POST `/identify`

#### Request Body

```json
{
  "email": "test@example.com",
  "phoneNumber": "9998887771"
}
```

#### Sample Response

```json
{
  "primaryContactId": 1,
  "emails": ["test@example.com"],
  "phoneNumbers": ["9998887771"],
  "secondaryContactIds": []
}
```

---

## 📁 Project Structure

```
├── controller/           # REST Controller
├── service/              # Business logic interface
├── serviceImpl/          # Business logic implementation
├── model/                # JPA entities
├── repository/           # Spring Data JPA interfaces
├── dto/                  # Request/response DTOs
├── config/               # Configuration beans (ModelMapper)
├── exception/            # Custom exception handling
```

---

## 🐳 Local Deployment

```bash
# 1. Build Docker image
docker build -t devdot021/identity-reconciliation-service:v1.0.0 .

# 2. Run container
docker run -p 8080:8080 devdot021/identity-reconciliation-service:v1.0.0

# 3. Test API
curl -X POST http://localhost:8080/identify -H "Content-Type: application/json" -d '{"email":"test@example.com", "phoneNumber":"9998887771"}'
```

---

## ☸️ Kubernetes Deployment (Minikube)

```bash
minikube start
kubectl create namespace identity-service
kubectl apply -f k8s/ -n identity-service
minikube service identity-reconciliation-service -n identity-service
```

---

## 🔄 CI/CD Pipeline

* GitHub Actions used for CI/CD
* Docker image is built and pushed to Docker Hub on push to `main`
* Workflow location: `.github/workflows/docker-image.yml`

### 🔐 Required GitHub Secrets:

* `DOCKER_USERNAME`
* `DOCKER_PASSWORD`

---

## 📊 Logging & Monitoring

* Logs via Logback (Spring Boot default)
* View logs:

  * `docker logs <container-id>`
  * `kubectl logs <pod-name> -n identity-service`
* Advanced: Integrate Prometheus + Grafana + Spring Actuator for observability

---

> Developed by Sujeet Prajapati
