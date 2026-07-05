\# MonitorHub: Full-Stack Live Service Monitoring Console



MonitorHub is a multi-tier service tracking application containerized for seamless cloud deployment. It pings target endpoints, displays real-time uptime metrics via a high-contrast animated dashboard, and triggers automated SMS notifications upon service disruptions.



\## 🏗️ Technical Architecture Stack

\- \*\*Frontend Client UI:\*\* React.js, Material UI (MUI), Framer Motion, Axios

\- \*\*Web Server Layer:\*\* Nginx (configured with custom `try\_files` route fallbacks)

\- \*\*Backend Core API:\*\* Java 21, Spring Boot, Spring Data JPA, Hibernate ORM

\- \*\*Database Engine:\*\* PostgreSQL 15

\- \*\*Infrastructure Orchestration:\*\* Docker, Docker Compose



\## 🚀 One-Command Deployment



This entire full-stack ecosystem is completely automated. To download image profiles, compile the Spring Boot JAR, build the static React assets, and launch the private virtual container network:



1\. Clone this repository.

2\. Launch \*\*Docker Desktop\*\* on your computer.

3\. Open a terminal prompt in the project root directory and execute:



```bash

docker compose up --build

