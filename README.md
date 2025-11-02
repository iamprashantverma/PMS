# 📊 Project Manager System

A **microservice-based Project Management System** designed to help teams efficiently manage projects, tasks, and collaboration in real-time. The system supports **modular project management, task tracking, Git integration, notifications**, and visualizations via **Kanban** and **Calendar** views.

---

##  Overview

This platform leverages a **modern microservices architecture** with **Dockerized backend services** and asynchronous communication via **Apache Kafka**.  

Key features include:  
- **Project creation and modular breakdown**  
- **Task, epic, bug, subtask, and story management**  
- **Git commit tracking and Calendar views**  
- **Account recovery via email (handled by User-Service)**  
- **Real-time notifications and comments**  
- **Secure JWT-based authentication with Spring Security**  
- **Spring Profiles for development and Docker environments**  

Users can **organize projects into modules, epics, stories, and tasks**, assign team members, set deadlines, and track progress efficiently.

---

## 🛠 Features

### User & Project Management
- Create projects and manage only **project-level details** (Project-Service).  
- Assign team members and watch members to tasks (managed by Task-Service).  
- **Account recovery via email** provided by User-Service.  

### Task, Epic, Subtask, Story & Bug Management
- Task-Service handles **tasks, subtasks, epics, stories, and bugs**.  
- Track task lifecycle, assign deadlines, and update statuses.  
- Move tasks across stages with **SAGA pattern for transactional consistency**.  

### Activity & Collaboration
- Activity-Tracker logs **user activities, Git commits, and project interactions**.  
- Provides **Calendar view** for task and commit tracking.  
- Real-time **comments and collaboration** on tasks.  

### Notifications & Communication
- **GraphQL subscriptions** for in-app notifications.  
- **Email notifications** using Thymeleaf templates for important updates.  

### Security & Performance
- **Spring Security** with JWT-based authentication and role-based access control.  
- Redis for **session management and caching**.  
- Asynchronous communication via **Apache Kafka**.  
- **Spring Profiles**: `dev` for local development and `docker` for containerized backend deployments.

---

## Tech Stack

| Layer                 | Technology |
|-----------------------|------------|
| Frontend              | React, Apollo Client, GraphQL, CSS, HTML, JavaScript |
| Backend (Microservices)| Java, Spring Boot, Hibernate ORM, REST APIs, GraphQL, Spring Security |
| Messaging             | Apache Kafka |
| Database              | MySQL |
| Caching & Session     | Redis |
| Security              | JWT Authentication, Spring Security |
| Notifications         | Email (Thymeleaf) |
| Build & Deployment    | Maven, Docker, Docker Compose |
| Architecture Pattern  | Microservices, SAGA Pattern |
| Environment Profiles  | Spring Profiles (`dev`, `docker`) |

---

## Microservices Overview

| Service Name           | Responsibility |
|------------------------|----------------|
| **Activity-Tracker**       | Tracks Git commits, user activities, and provides Calendar view |
| **API-Gateway**            | Central gateway routing requests to services |
| **Config-Server**          | Centralized configuration management |
| **Discovery-Server**       | Service registry for microservices |
| **Notification-Service**   | Handles email and in-app notifications |
| **Project-Service**        | Manages projects only |
| **Task-Service**           | Handles **tasks, subtasks, epics, stories, and bugs** |
| **User-Service**           | Manages authentication, user accounts, and **account recovery via email** |

---

##  Backend Deployment (Dockerized)

All **backend services** are Dockerized and orchestrated via Docker Compose. The **frontend is not yet dockerized**, so it should be run locally with Node.js during development.  

```bash
# Build and start all backend services using Docker Compose
docker-compose up -d
