# Full-Stack Application

A containerized full-stack application built using modern web technologies with Docker-based deployment and CI/CD automation.

---

## Technologies Used

- Frontend: React
- Backend: Node.js + Express
- Database: MongoDB
- Message Queue: RabbitMQ
- Database UI: Mongo Express
- Containerization: Docker + Docker Compose
- CI/CD: GitHub Actions

---

## Description

This project is a full-stack application powered by Docker that includes a frontend client, backend API, database, and queue system.

It demonstrates:

- Full-stack architecture
- Docker multi-container setup
- REST API integration
- MongoDB database connectivity
- RabbitMQ messaging system
- Mongo Express admin panel
- Automated CI/CD pipeline using GitHub Actions

---

## Project Structure

```bash
fullstack-docker/
│── backend/
│── frontend/
│── docker-compose.yml
│── ci-pipeline.yml
│── README.md
```

---

## Getting Started

### Clone Repository

```bash
git clone https://github.com/aaarif044/technical-tasks.git
cd technical-tasks
```

### Run Project

```bash
docker compose up --build
```

### Run in Background

```bash
docker compose up -d --build
```

---

## Useful Commands

### Check Running Containers

```bash
docker compose ps
```

### View Logs

```bash
docker compose logs -f
```

### View Backend Logs

```bash
docker compose logs -f backend
```

### View Frontend Logs

```bash
docker compose logs -f frontend
```

### Stop Containers

```bash
docker compose down
```

### Stop and Remove Volumes

```bash
docker compose down -v
```

### Restart Backend

```bash
docker compose restart backend
```

### Access Backend Container Shell

```bash
docker compose exec backend sh
```

---

## Application URLs

- Frontend: [http://localhost:3000](http://localhost:3000)
- Backend API: [http://localhost:3010](http://localhost:3010)
- Mongo Express: [http://localhost:8081](http://localhost:8081)
- RabbitMQ Dashboard: [http://localhost:15672](http://localhost:15672)

---

## CI/CD

GitHub Actions workflow is configured inside:

```bash
ci-pipeline.yml
```

It can be used for:

- Build validation
- Automated testing
- Docker image checks
- Deployment pipeline

---

## Author

Mohammed Arif Ansari
