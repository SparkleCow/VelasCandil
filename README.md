# Velas Candil - Sales and Management System

This project is the final capstone project for the ADSO (Software Analysis and Development) program at SENA, developed for a handmade candle business in Bogotá, Colombia.

The system aims to digitize sales processes, product management, customer administration and order processing through a modern full-stack architecture based on Angular and Spring Boot.

The platform centralizes business operations, improves traceability and provides a scalable foundation for future growth and digital transformation.

<img width="1902" height="936" alt="image" src="https://github.com/user-attachments/assets/44c4be21-3dfc-4fee-a388-0d91d1df82d2" />

---

## Technologies Used

### Frontend
- Angular
- TypeScript
- HTML / SCSS
- Angular Material
- Toastr

### Backend
- Java
- Spring - Spring Boot
- Spring Security (authentication and authorization)
- JWT (JSON Web Tokens)
- JPA / Hibernate

### Database
- PostgreSQL
- S3 (AWS)

### Infrastructure & DevOps
- Docker
- Docker Compose
- CI/CD

### Additional Services
- MailDev (email testing and development environment)
- AWS S3 (image storage)

---

## System Modules

- User authentication and registration
- User management (roles: admin, customer, etc.)
- Candle product catalog
- Shopping cart
- Checkout process
- Order management
- Email notifications
- Product image upload

<img width="1902" height="936" alt="image" src="https://github.com/user-attachments/assets/3db89739-b3fd-4db5-b693-070a3b927b9e" />

---

## Architecture

The system is divided into two main layers:

- **Frontend (Angular)**: user interface
- **Backend (Spring Boot)**: business logic and REST API

Both services communicate via HTTP/JSON.

<img width="1376" height="853" alt="image" src="https://github.com/user-attachments/assets/5243b6e7-ca0a-4f84-a7ac-4a198f218752" /> <br/>

The following diagram illustrates how the local components interact during development.

<img width="1720" height="788" alt="image" src="https://github.com/user-attachments/assets/10d0769d-e67b-45eb-8d4c-84501de3a495" />

The root of the application is the AppComponent, which defines the global layout structure including the navigation bar, routing outlet and footer components.

The frontend follows a modular architecture where each feature is isolated into its own module, improving maintainability and scalability.
<img width="1714" height="787" alt="image" src="https://github.com/user-attachments/assets/a4a03cf9-9b4f-4a49-b60c-f205e5d3a844" />

## Global Services & State

The core/services directory contains singleton services that manage business logic and communication with the backend API.

These services centralize API consumption, state synchronization and reusable business operations across the application.

<img width="1132" height="837" alt="image" src="https://github.com/user-attachments/assets/62b8e303-019c-43b3-8e48-63d44df42059" />

## Profile Data Flow

The following diagrams illustrate how profile information travels through components, services and backend endpoints.

The flow demonstrates the separation of responsibilities between UI components, Angular services and Spring Boot controllers.

<img width="1132" height="837" alt="image" src="https://github.com/user-attachments/assets/43d722e5-44d8-4cb3-a500-9176b1c15aa6" />

<img width="1117" height="484" alt="image" src="https://github.com/user-attachments/assets/0df08b7f-1b58-4ebb-b57c-fa1482ab0427" />

---

# Data Flow: Authentication Filter

The JwtFilter intercepts incoming requests to validate the token before they reach the controllers.

Security Filter Chain Execution Flow

<img width="1580" height="852" alt="image" src="https://github.com/user-attachments/assets/3e2f89ec-2d4b-440c-b3d9-b69545e225c1" />

# Required Environment Variables

To run the API successfully, several sensitive or environment-specific variables must be provided. These are typically managed via a .env file or IDE run configurations.

Variable	Description	Source in Code

- SECRET_KEY	HS256 key for signing JWT tokens.	

- AWS_KEY	Access Key for AWS S3 integration.	

- AWS_SECRET	Secret Key for AWS S3 integration.	

- BUCKET_NAME	Name of the S3 bucket for product images.	

- MP_ACCESS_TOKEN	Mercado Pago Production/Sandbox token.	

- MP_WEBHOOK_URL	Publicly accessible URL for MP callbacks.	

# Backend CI Data Flow

The project includes automated GitHub Actions workflows for validating both frontend and backend codebases.

These pipelines help ensure build stability and reduce integration issues.

Diagram: Backend CI Implementation

<img width="500" height="839" alt="image" src="https://github.com/user-attachments/assets/72518959-a930-4413-a735-57d3c8a24ab9" />

Frontend CI Data Flow

The following diagram maps the CI steps to the frontend build ecosystem.

Diagram: Frontend CI Implementation

<img width="1725" height="699" alt="image" src="https://github.com/user-attachments/assets/283cdef0-8813-4760-9416-0a49c4fd1b97" />

# Local Development

Backend
cd candil-api
./mvnw spring-boot:run
Frontend
cd candil-ui
npm install
ng serve or npm start (script)

The frontend will be available at:

http://localhost:4200

The backend will be available at:

http://localhost:8080

## 🐳 Docker Execution

The project can be run using containers:

The entire environment can be executed through Docker Compose.

Start all services:

docker compose up -d

Stop all services:

docker compose down

View container logs:

docker compose logs -f
