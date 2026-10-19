# Inventory Management System

A full-stack inventory management application with JWT authentication, real-time dashboard analytics, and complete product lifecycle management.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 21, Spring Boot 3.3.5, Spring Security, Spring Data JPA |
| **Frontend** | React 19, Vite 8, Tailwind CSS 4, React Router 7 |
| **Database** | MySQL 8 (production) / H2 (development) |
| **Authentication** | JWT (JJWT 0.12.6), BCrypt password hashing |

## System Architecture

```
┌─────────────────────┐         ┌──────────────────────────────┐
│   React Frontend    │  HTTP   │     Spring Boot Backend       │
│                     │◄───────►│                               │
│  - Auth Pages       │  REST   │  Controller ─► Service ─► Repository
│  - Dashboard        │  + JWT  │       │                    │
│  - Product CRUD     │         │  Security Filter Chain     JPA
│  - Image Upload     │         │  (JWT Authentication)      │
└─────────────────────┘         └────────────────────────┬─────┘
                                                         │
                                                    ┌────▼────┐
                                                    │ MySQL / │
                                                    │   H2    │
                                                    └─────────┘
```

## Features

### Authentication & Security
- User registration with input validation (username, email, password, full name)
- Login with JWT token generation (HMAC-SHA, 24-hour expiration)
- Stateless session management — no server-side sessions
- BCrypt password hashing
- Axios interceptors auto-attach Bearer token and handle 401 redirects
- Protected routes on frontend; all API endpoints (except auth) require authentication

### Product Management
- Full CRUD operations with server-side validation
- Image upload with drag-and-drop (supports JPG, PNG, GIF, WebP; max 5MB)
- UUID-based file naming to prevent conflicts
- Automatic stock status calculation based on quantity thresholds:
  - `IN_STOCK` (quantity > 10)
  - `LOW_STOCK` (quantity 1–10)
  - `OUT_OF_STOCK` (quantity = 0)
- SKU uniqueness enforcement

### Search, Filter & Sort
- Full-text search across product name, SKU, and description (case-insensitive)
- Filter by category and stock status
- Sort by date, name, price, or quantity (ascending/descending)
- Server-side pagination

### Dashboard Analytics
- Total products count
- Stock status breakdown (in stock / low stock / out of stock)
- Total inventory value (calculated as SUM of price x quantity)
- Low stock alerts table with quick edit action

### UI/UX
- Responsive sidebar layout (collapses to hamburger menu on mobile)
- Color-coded status badges
- Toast notifications for user feedback
- Confirmation dialogs for destructive actions
- 404 page handling

## Database Schema

```
┌──────────────┐        ┌──────────────────┐
│    users     │        │     products     │
├──────────────┤        ├──────────────────┤
│ id (PK)      │◄──┐    │ id (PK)          │
│ username     │   │    │ name             │
│ email        │   │    │ description      │
│ password     │   └────│ user_id (FK)     │
│ full_name    │        │ category         │
│ role         │        │ sku (UNIQUE)     │
│ created_at   │        │ quantity         │
│ updated_at   │        │ price            │
└──────────────┘        │ image_url        │
                        │ status           │
                        │ created_at       │
                        │ updated_at       │
                        └──────────────────┘
```

**Relationship:** Each product belongs to one user (the creator). All users share the same inventory — products are visible to all authenticated users.

## API Endpoints

### Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive JWT token |

### Protected (requires Bearer token)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List products (search, filter, sort, pagination) |
| GET | `/api/products/{id}` | Get a single product |
| POST | `/api/products` | Create a product (multipart: JSON + image) |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |
| GET | `/api/products/categories` | Get all distinct categories |
| GET | `/api/dashboard/stats` | Get dashboard statistics |

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- Maven 3.9+

### Option A: Run with H2 (recommended for reviewers — no database setup needed)

```bash
# Start backend with embedded H2 database
cd backend
mvn clean spring-boot:run "-Dspring-boot.run.profiles=dev"

# Start frontend (in a separate terminal)
cd frontend
npm install
npm run dev
```

The dev profile auto-seeds the database with a demo account and 10 sample products across different categories and stock statuses, so you can explore the full application immediately.

**Demo login:** `demo` / `password123`

### Option B: Run with MySQL (via Docker)

```bash
# Start MySQL
docker run -d --name inventory-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=inventory_db \
  mysql:8

# Start backend
cd backend
mvn clean spring-boot:run

# Start frontend (in a separate terminal)
cd frontend
npm install
npm run dev
```

### Access the application

- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080
- **H2 Console (dev profile only):** http://localhost:8080/h2-console

## Testing

The backend includes integration tests using `@SpringBootTest` with an H2 in-memory database:

```bash
cd backend
mvn test
```

**Test coverage:**
- **AuthController** (5 tests) — registration, duplicate detection, login, invalid credentials, input validation
- **ProductController** (6 tests) — CRUD operations, stock status logic, authentication enforcement, pagination, dashboard stats

## Project Structure

```
├── backend/
│   └── src/main/java/com/inventory/
│       ├── config/           # Security, CORS, JWT filter, web config
│       ├── controller/       # REST controllers (Auth, Product, Dashboard)
│       ├── dto/
│       │   ├── request/      # LoginRequest, RegisterRequest, ProductRequest
│       │   └── response/     # AuthResponse, ProductResponse, DashboardResponse, PagedResponse
│       ├── entity/           # JPA entities (User, Product) and enums (Role, StockStatus)
│       ├── exception/        # Global exception handler, custom exceptions
│       ├── repository/       # Spring Data JPA repositories
│       ├── service/          # Business logic (Auth, Product, Dashboard, FileStorage)
│       └── util/             # JWT utility class
│
├── frontend/
│   └── src/
│       ├── api/              # Axios instance and API modules
│       ├── components/
│       │   ├── common/       # ProtectedRoute, Pagination, ConfirmDialog, ImageUpload
│       │   ├── layout/       # AppLayout, Sidebar, Header
│       │   ├── products/     # ProductFilters
│       │   └── dashboard/    # StatCard, LowStockTable
│       ├── context/          # AuthContext (JWT + user state)
│       ├── hooks/            # useAuth custom hook
│       ├── pages/            # Login, Register, Dashboard, ProductList, ProductForm, NotFound
│       └── utils/            # Currency and date formatters
```

## Error Handling

The backend returns structured JSON error responses for all error types:

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input",
  "path": "/api/products",
  "fieldErrors": {
    "name": "must not be blank",
    "price": "must be greater than 0.01"
  }
}
```

Handled cases: validation errors (400), authentication failures (401), duplicate resources (409), not found (404), file upload limits, and unexpected server errors (500).
