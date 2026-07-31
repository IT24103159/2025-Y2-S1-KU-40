# 🎓 Web-Based Help Desk System for University Students

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green.svg?style=flat&logo=thymeleaf)](https://www.thymeleaf.org/)
[![Database](https://img.shields.io/badge/Database-MS%20SQL%20Server-blue.svg?style=flat&logo=microsoftsqlserver)](https://www.microsoft.com/sql-server)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A centralized, enterprise-grade Java web application designed to streamline, track, and resolve academic, technical, administrative, and confidential counseling inquiries for university students. 

Developed as part of the **SE2030 - Software Engineering** module at the **Sri Lanka Institute of Information Technology (SLIIT)** (Year 2 Semester 1, 2025) by **Group 2025-Y2-S1-KU-40**.

---

## 📋 Table of Contents
- [Project Overview](#-project-overview)
- [Key Features & Role-Based Workflows](#-key-features--role-based-workflows)
- [Software Architecture & Design Patterns](#-software-architecture--design-patterns)
- [Technology Stack](#-technology-stack)
- [Database Schema & ER Structure](#-database-schema--er-structure)
- [Getting Started & Local Setup](#-getting-started--local-setup)
- [Project Management & Agile Sprints](#-project-management--agile-sprints)
- [Team Members & Individual Contributions](#-team-members--individual-contributions)
- [Future Enhancements](#-future-enhancements)
- [License](#-license)

---

## 🌟 Project Overview

Traditional university inquiry resolution processes often suffer from fragmented email threads, lack of transparency, manual routing delays, and security risks—especially for sensitive counseling inquiries.

The **Web-Based Help Desk System** eliminates these bottlenecks by providing:
1. **Single Point of Entry:** Unified portal for submitting IT, Academic, and Counseling issues.
2. **Transparent Lifecycle:** End-to-end status tracking from `UNASSIGNED` to `ASSIGNED` and `ANSWERED`.
3. **Role-Based Access Control (RBAC):** Customized dashboards for 6 distinct user roles.
4. **Confidential Counseling Channel:** Direct auto-routing with AES-128 payload encryption and absolute student anonymity.
5. **Self-Service Knowledge Base:** Searchable repository of solutions for common FAQs.
6. **Real-time UI Notifications:** Instant alerts triggered by ticket status updates and staff responses.

---

## 🔐 Key Features & Role-Based Workflows

The application enforces strict **Role-Based Access Control (RBAC)** across 6 distinct user roles:

| User Role | Main Responsibilities & Features |
| :--- | :--- |
| 🧑‍🎓 **Student** | Submit tickets with file attachments (`IT`, `Academic`, `Counseling`), track status in real time via "My Tickets", view public Knowledge Base, receive notifications, and provide feedback upon issue resolution. |
| 🎧 **Help Desk Officer** | Manages the **Unassigned Queue** for incoming IT and Academic tickets. Manually routes tickets to specialized staff members (IT Officers or Lecturers). |
| 💻 **IT Support Officer** | Accesses assigned technical tickets, provides responses in a threaded conversation layout, and publishes technical articles to the Knowledge Base. |
| 👨‍🏫 **Lecturer** | Accesses assigned academic tickets linked to specific course modules, engages in threaded Q&A conversations, and contributes subject-specific KB articles. |
| 🧠 **Student Counselor** | Receives confidential counseling tickets directly (bypassing the Help Desk queue), views encrypted inquiry details anonymously, and responds via AES-encrypted communication channels. |
| ⚙️ **System Administrator** | Manages user accounts (Full CRUD for all staff roles), configures system settings, and oversees platform security. |

---

## 🧩 Software Architecture & Design Patterns

The project follows a clean **Model-View-Controller (MVC)** architecture layered with classic Gang of Four (GoF) design patterns to ensure maintainability, modularity, and loose coupling.

```
                          ┌──────────────────────────┐
                          │   Client Browser (UI)    │
                          └─────────────┬────────────┘
                                        │
                                        ▼
                          ┌──────────────────────────┐
                          │   Spring MVC Controller  │
                          └─────────────┬────────────┘
                                        │
                                        ▼
                          ┌──────────────────────────┐
                          │      Service Layer       │
                          ├──────────────────────────┤
                          │  • Observer Pattern      │
                          │  • Strategy Pattern      │
                          │  • Factory Pattern       │
                          │  • AES Encryption        │
                          └─────────────┬────────────┘
                                        │
                                        ▼
                          ┌──────────────────────────┐
                          │ Spring Data JPA Repository│
                          └─────────────┬────────────┘
                                        │
                                        ▼
                          ┌──────────────────────────┐
                          │    MS SQL Server DB      │
                          └──────────────────────────┘
```

### 1. 🔔 Observer Pattern (Notification System)
* **Purpose:** Decouples event generation from user notification delivery.
* **Implementation:** When a ticket status changes or a new response is posted, `TicketServiceImpl` acts as the subject notifying the `NotificationService` (observer) to spawn real-time unread notifications for affected users.

### 2. 🧠 Strategy Pattern (Knowledge Base Article Filtering)
* **Purpose:** Encapsulates different filtering algorithms based on user authentication state and role.
* **Implementation:** 
  * `getArticlesForPublic()`: Returns all published articles for general self-service viewing.
  * `getArticlesForAuthor(User author)`: Filters and presents only author-owned articles for staff management views (`/kb/manage`).

### 3. 🏭 Factory Pattern (Knowledge Base Article Creation)
* **Purpose:** Encapsulates entity initialization and metadata assignment logic.
* **Implementation:** The service layer (`KnowledgeBaseServiceImpl.createArticle`) accepts simple DTOs from the controller, constructs complete `KnowledgeBaseArticle` domain models, injects author metadata, and handles persistence cleanly.

### 4. 🔒 AES-128 Encryption Service (Counseling Confidentiality)
* **Purpose:** Guarantees absolute student privacy and data anonymity for sensitive psychological and counseling inquiries.
* **Implementation:** `EncryptionService.java` utilizes 128-bit Advanced Encryption Standard (AES) cryptography. Inquiry details and staff responses are stored encrypted in the database, decryptable only within authorized Counselor views.

---

## 🛠 Technology Stack

### Backend
* **Language:** Java 21
* **Framework:** Spring Boot 3.5.6 (Spring Web, Spring Data JPA)
* **ORM:** Hibernate
* **Security & Utility:** BCrypt Password Hashing, Java Cryptography Extension (JCE), Lombok

### Frontend
* **Templating Engine:** Thymeleaf
* **Core Web Stack:** HTML5, CSS3, JavaScript (ES6+)

### Database & Build Tools
* **Database Management System:** Microsoft SQL Server
* **Build Automation:** Apache Maven (`pom.xml`)
* **Version Control:** Git & GitHub

---

## 🗄 Database Schema & ER Structure

The database schema (`UniHelpDeskDB`) consists of 11 relational tables structured to support RBAC and inheritance:

* **`users`** (Base entity storing credentials, role enum, and timestamps)
* **`students`** & **`support_staff`** & **`lecturers`** (Inheritance extensions for role-specific attributes)
* **`tickets`** (Core entity containing title, category, status, priority, and FK references)
* **`ticket_responses`** (Threaded conversation records linked to tickets)
* **`ticket_attachments`** (Metadata for uploaded file attachments stored in `./uploads`)
* **`knowledge_base_articles`** (FAQ articles linked to author users)
* **`notifications`** (System alerts linked to target users)
* **`faculties`** & **`modules`** (Academic organizational hierarchy)

---

## 🚀 Getting Started & Local Setup

### Prerequisites
Make sure you have the following installed on your local development machine:
* **JDK 21** or higher
* **Apache Maven 3.8+**
* **Microsoft SQL Server** (Running on default port `1433`)
* **Git**

### Installation Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-username/2025-Y2-S1-KU-40.git
   cd 2025-Y2-S1-KU-40
   ```

2. **Setup Database:**
   * Open Microsoft SQL Server Management Studio (SSMS) or your preferred SQL client.
   * Execute the database initialization script included in the root directory:
     ```sql
     -- Run UniHelpDeskDB.sql script to create database and tables
     ```

3. **Configure Database & Application Properties:**
   * Open `src/main/resources/application.properties` and verify your MS SQL Server credentials:
     ```properties
     spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=UniHelpDeskDB;encrypt=false;trustServerCertificate=true
     spring.datasource.username=sa
     spring.datasource.password=YOUR_SQL_PASSWORD
     encryption.secret.key=SLIITHelpDesk@12
     file.upload-dir=./uploads
     ```

4. **Build the Application:**
   ```bash
   ./mvnw clean install
   ```

5. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Access the Portal:**
   Open your web browser and navigate to:
   ```
   http://localhost:8080/login
   ```

---

## 📅 Project Management & Agile Sprints

The system was iteratively developed over a 12-week timeframe following the **Agile (Scrum)** methodology:

| Sprint | Timeline | Objectives & Milestone Output |
| :--- | :--- | :--- |
| **Sprint 1** | Weeks 1-3 | Foundation & Security: Spring Boot setup, SQL database initialization, JPA Entities, BCrypt security, and basic navigation layout. |
| **Sprint 2** | Weeks 4-6 | Core Ticketing Lifecycle: Student ticket submission with file attachments, Help Desk Unassigned Queue, and manual routing workflows. |
| **Sprint 3** | Weeks 7-9 | Ticket Resolution & Self-Service: Threaded staff responses, Public KB portal, Staff KB management page, Factory & Strategy design patterns. |
| **Sprint 4** | Weeks 10-12 | Advanced & Confidential Modules: Real-time Observer Pattern Notification System, AES Confidential Counseling module, system testing & bug fixes. |

---

## 👥 Team Members & Individual Contributions

**Group ID:** `2025-Y2-S1-KU-40`  
**Institution:** Sri Lanka Institute of Information Technology (SLIIT)  
**Course:** SE2030 - Software Engineering (Year 2 Semester 1 - 2025)

| Student ID | Student Name | Assigned Role & Feature Responsibilities |
| :--- | :--- | :--- |
| **IT24103159** | **Athauda K. K. A. M. S. R. V. B.** | **Student Module:** Ticket Submission form, "My Tickets" status tracking dashboard, File attachment frontend, Public KB view. |
| **IT24103652** | **Ilham M. H. M.** | **Help Desk Officer Module:** Unassigned Queue dashboard, manual routing business logic, ticket assignment workflows. |
| **IT24102770** | **Leelarathna G. N. P.** | **IT Support Officer Module:** IT Officer dashboard, assigned IT tickets view, threaded response resolution logic. |
| **IT24102944** | **Dasanayake U. R. N. P. K.** | **Lecturer Module:** Lecturer dashboard, academic ticket management, linking responses to academic course modules. |
| **IT24102876** | **Nawarathna I. G. D. S.** | **Counselor Module:** Confidential & Anonymous Counseling module, auto-assignment bypass logic, secure anonymous dashboard. |
| **IT24104181** | **Alahakoon A. M. D. S.** | **Admin & System Core:** System Architecture, Spring Security setup, User Management CRUD, Observer, Strategy & Factory pattern implementation. |

---

## 🔮 Future Enhancements

- [ ] **Email Notification Integration:** Extend the Observer Pattern to send automated email notifications via JavaMailSender.
- [ ] **Live Chat Feature:** Integrate real-time WebSocket communication for instant messaging between students and active Help Desk staff.
- [ ] **AI-Based Automated Ticket Routing:** Incorporate Machine Learning NLP algorithms to automatically classify and route incoming tickets based on description keywords.
- [ ] **Mobile Responsive React/Next.js Frontend:** Develop a modern Single Page Application (SPA) or dedicated mobile app for improved accessibility.

---

## 📄 License

This project is created for academic assessment purposes under the SE2030 Software Engineering module at SLIIT. Distributed under the [MIT License](LICENSE).
