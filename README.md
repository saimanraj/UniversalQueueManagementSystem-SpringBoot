# 🎟️ UQS — Online Queue Management System

A **production-ready**, **beginner-friendly** queue management system built with **Java 17 + Spring Boot + MySQL**.  
Runs directly in **VS Code** — no external application server needed.

```
http://localhost:8080
```

---

## 📋 Table of Contents

1. [Features](#features)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Prerequisites](#prerequisites)
5. [MySQL Setup](#mysql-setup)
6. [VS Code Setup](#vs-code-setup)
7. [Running the Application](#running-the-application)
8. [Default Credentials](#default-credentials)
9. [User Roles & Workflows](#user-roles--workflows)
10. [API Endpoints](#api-endpoints)
11. [Security](#security)
12. [Troubleshooting](#troubleshooting)

---

## ✨ Features

### 👤 Customer
- Register & Login
- Browse approved vendors
- Join queue → receive token number
- Track queue status in real time
- See people ahead + estimated wait (ETA)
- Cancel token

### 🏪 Vendor
- Register shop (pending admin approval)
- Open / Pause / Resume / Close queue
- View all customers in queue
- Call next token
- Mark token as served
- Configure average service time

### 🛡️ Admin
- Login to dashboard
- Approve / Reject vendors
- View all users
- Monitor all active queues
- Analytics cards

---

## 🛠️ Technology Stack

| Layer        | Technology                           |
|-------------|--------------------------------------|
| Language     | Java 17                              |
| Framework    | Spring Boot 3.2                      |
| Security     | Spring Security 6 + BCrypt           |
| ORM          | Spring Data JPA + Hibernate          |
| Database     | MySQL 8                              |
| Templates    | Thymeleaf 3                          |
| UI           | Bootstrap 5.3 + Font Awesome 6       |
| Build Tool   | Maven 3.9+                           |
| Server       | Embedded Tomcat (no install needed!) |

---

## 📁 Project Structure

```
OnlineQueueSystem/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/uqs/
        │   ├── OnlineQueueSystemApplication.java   ← Main class
        │   ├── config/
        │   │   ├── SecurityConfig.java             ← Spring Security
        │   │   └── GlobalExceptionHandler.java     ← Error handling
        │   ├── controller/
        │   │   ├── AuthController.java             ← Login / Register
        │   │   ├── CustomerController.java         ← Customer pages
        │   │   ├── VendorController.java           ← Vendor pages
        │   │   ├── AdminController.java            ← Admin pages
        │   │   └── ApiController.java              ← REST API (AJAX)
        │   ├── dto/
        │   │   ├── RegisterDto.java
        │   │   ├── QueueStatusDto.java
        │   │   └── DashboardDto.java
        │   ├── entity/
        │   │   ├── User.java
        │   │   ├── Vendor.java
        │   │   ├── Queue.java
        │   │   └── Token.java
        │   ├── repository/
        │   │   ├── UserRepository.java
        │   │   ├── VendorRepository.java
        │   │   ├── QueueRepository.java
        │   │   └── TokenRepository.java
        │   ├── service/
        │   │   ├── CustomUserDetailsService.java
        │   │   ├── UserService.java
        │   │   ├── VendorService.java
        │   │   └── QueueService.java
        │   └── util/
        │       └── AuthUtil.java
        └── resources/
            ├── application.properties
            ├── schema.sql
            ├── static/
            │   ├── css/style.css
            │   └── js/app.js
            └── templates/
                ├── error.html
                ├── auth/
                │   ├── login.html
                │   └── register.html
                ├── customer/
                │   ├── dashboard.html
                │   ├── vendors.html
                │   ├── join.html
                │   └── track.html
                ├── vendor/
                │   └── dashboard.html
                └── admin/
                    ├── dashboard.html
                    ├── vendors.html
                    ├── users.html
                    └── queues.html
```

---

## ✅ Prerequisites

Make sure these are installed before running:

| Tool      | Version | Download                              |
|----------|---------|---------------------------------------|
| Java JDK | 17+     | https://adoptium.net                  |
| Maven    | 3.8+    | https://maven.apache.org/download.cgi |
| MySQL    | 8.0+    | https://dev.mysql.com/downloads/      |
| VS Code  | Latest  | https://code.visualstudio.com         |

### Verify installations

```bash
java -version      # Should show: 17 or higher
mvn -version       # Should show: Apache Maven 3.x.x
mysql --version    # Should show: 8.x.x
```

---

## 🐬 MySQL Setup

### Step 1 — Start MySQL

**Windows:**
```
Open Services → Start "MySQL80"
  OR
Run: net start mysql80
```

**Mac:**
```bash
brew services start mysql
```

**Linux:**
```bash
sudo systemctl start mysql
```

### Step 2 — Create the database

Open MySQL terminal:
```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE IF NOT EXISTS uqs_db;
-- The schema.sql file will create all tables automatically on startup
EXIT;
```

### Step 3 — Update application.properties

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/uqs_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

> 💡 Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL root password.  
> If MySQL has no password, leave it blank: `spring.datasource.password=`

---

## 💻 VS Code Setup

### Step 1 — Install VS Code Extensions

Open VS Code → Extensions (Ctrl+Shift+X) → Install:

| Extension | Publisher |
|-----------|-----------|
| **Extension Pack for Java** | Microsoft |
| **Spring Boot Extension Pack** | VMware |
| **Lombok Annotations Support** | Gabriel Basilio Brito |

### Step 2 — Open the Project

```
File → Open Folder → Select the OnlineQueueSystem folder
```

### Step 3 — VS Code should auto-detect it as a Maven/Spring Boot project

You'll see Java projects in the Explorer panel.

---

## 🚀 Running the Application

### Method 1 — Terminal (Recommended)

Open VS Code terminal (`Ctrl+\``) and run:

```bash
cd OnlineQueueSystem
mvn spring-boot:run
```

### Method 2 — VS Code Spring Boot Dashboard

1. Press `Ctrl+Shift+P` → "Spring Boot Dashboard"
2. Click the ▶ play button next to `OnlineQueueSystemApplication`

### Method 3 — Maven directly

```bash
mvn clean install
java -jar target/OnlineQueueSystem-1.0.0.jar
```

### ✅ Success Output

When the app starts, you'll see:
```
Started OnlineQueueSystemApplication in X.XXX seconds
```

**Open your browser:**  
👉 [http://localhost:8080](http://localhost:8080)

---

## 🔑 Default Credentials

| Role   | Email             | Password  |
|--------|-------------------|-----------|
| Admin  | admin@uqs.com     | admin123  |

> Customers and Vendors are created via the Register page.

---

## 👥 User Roles & Workflows

### Customer Flow
```
Register → Login → Browse Vendors → Join Queue
→ Get Token Number → Track Queue → See ETA → Done!
```

### Vendor Flow
```
Register (as Vendor) → Wait for Admin Approval
→ Login → Open Queue → Call Next Token → Mark Served
```

### Admin Flow
```
Login → Dashboard → Approve Vendors → Monitor Queues
```

---

## 🌐 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/` | Redirect to login |
| GET | `/login` | Login page |
| POST | `/login` | Authenticate |
| GET | `/register` | Register page |
| POST | `/register` | Create account |
| GET | `/customer/dashboard` | Customer home |
| GET | `/customer/vendors` | Browse vendors |
| GET | `/customer/join/{id}` | Join queue page |
| POST | `/customer/join/{id}` | Join queue |
| GET | `/customer/track/{id}` | Track token |
| POST | `/customer/cancel/{id}` | Cancel token |
| GET | `/vendor/dashboard` | Vendor home |
| POST | `/vendor/queue/open` | Open queue |
| POST | `/vendor/queue/pause` | Pause queue |
| POST | `/vendor/queue/resume` | Resume queue |
| POST | `/vendor/queue/close` | Close queue |
| POST | `/vendor/queue/next` | Call next |
| POST | `/vendor/token/{id}/served` | Mark served |
| GET | `/admin/dashboard` | Admin home |
| GET | `/admin/vendors` | Vendor management |
| POST | `/admin/vendors/{id}/approve` | Approve vendor |
| POST | `/admin/vendors/{id}/reject` | Reject vendor |
| GET | `/admin/users` | User list |
| GET | `/admin/queues` | Queue monitor |
| GET | `/api/queue/status/{vendorId}` | AJAX: queue status |
| GET | `/api/vendor/stats/{vendorId}` | AJAX: vendor stats |

---

## 🔐 Security

| Feature | Implementation |
|---------|---------------|
| Password hashing | BCrypt (strength 10) |
| Authentication | Spring Security form login |
| Authorization | Role-based (`CUSTOMER`, `VENDOR`, `ADMIN`) |
| CSRF protection | Enabled on all forms |
| Session management | 30-minute timeout, single session |
| SQL injection | Prevented by JPA parameterized queries |
| XSS protection | Thymeleaf auto-escapes all output |
| Secure logout | Session invalidation + cookie deletion |

---

## 🐞 Troubleshooting

### ❌ "Communications link failure" (MySQL)
- Make sure MySQL is running
- Check password in `application.properties`
- Try: `spring.datasource.url=...&allowPublicKeyRetrieval=true`

### ❌ "Port 8080 already in use"
Change the port in `application.properties`:
```properties
server.port=8081
```

### ❌ "Lombok not recognized" (compilation errors)
- Install the Lombok VS Code extension
- Run: `mvn clean compile`

### ❌ "Table doesn't exist"
- Make sure `spring.sql.init.mode=always` is set
- Or manually run `schema.sql` in MySQL Workbench

### ❌ White label error page
- Check the browser console and VS Code terminal for stack traces
- Make sure you're logged in with the correct role

### ❌ Vendor can't operate queue (no controls visible)
- The vendor must be **Approved** by Admin first
- Login as admin → Vendors → Approve

---

## 📊 Database Schema

```sql
users       → id, name, email, phone, password, role, created_at
vendors     → id, user_id, shop_name, category, approved, avg_service_time, ...
queues      → id, vendor_id, current_token, is_active, is_paused, opened_at
tokens      → id, user_id, vendor_id, token_no, status, created_at, served_at
```

**Token Status Flow:**
```
WAITING → SERVING → SERVED
   ↓
CANCELLED
```

---

## 🎨 UI Screenshots

| Page | Description |
|------|-------------|
| Login | Clean auth page with role registration links |
| Customer Dashboard | Token history with status badges |
| Vendor Browser | Card grid with live ETA info |
| Token Tracker | Big token display, countdown, auto-refresh |
| Vendor Dashboard | Queue controls + customer table |
| Admin Dashboard | Stats cards + pending approvals |

---

## 📝 License

Built for educational and production use. Free to use and modify.

---

**Made with ❤️ using Java 17 + Spring Boot + MySQL**
