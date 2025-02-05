# First Web Application (FWA) - 42 Cursus

## Project Overview
The **First Web Application (FWA)** project aims to introduce students to web development using standard Java technologies. This project will guide you in building a web application using the Java Servlet API, with a focus on authentication, user management, and JSP.

## Technologies Used
- **Java (latest LTS version)**
- **GraalVM**
- **Maven**
- **Tomcat**
- **JDBC (JdbcTemplate for database interactions)**
- **BCrypt (for password encryption)**
- **JSP & Servlets**

---

## Project Structure
```
Cinema/
│── src/
│   ├── main/java/fr/42/cinema/
│   │   ├── config/
│   │   ├── services/
│   │   ├── models/
│   │   ├── repositories/
│   │   ├── servlets/
│   │   ├── listeners/
│   │   ├── filters/
│   ├── resources/sql/
│   │   ├── schema.sql
│   │   ├── data.sql
│   ├── webapp/WEB-INF/
│   │   ├── application.properties
│   │   ├── web.xml
│   │   ├── html/
│   │   ├── jsp/
│── pom.xml
│── README.md
```

---

## Exercises
### Exercise 00: Welcome to Servlets
- Create an MVP web application for user **registration** and **authentication**.
- **SignUp Servlet**: Accepts `POST` requests to register a user with:
  - First name
  - Last name
  - Phone number
  - Password (encrypted using **BCrypt**)
- **SignIn Servlet**: Accepts `POST` requests to authenticate users.
  - If successful, creates an **HttpSession** and redirects to the profile page.
  - If unsuccessful, redirects back to the login page.

### Exercise 01: Authentication
- Implement an **authentication filter** to restrict access to the `/profile` page.
- If an unauthenticated user tries to access restricted pages, return **403 FORBIDDEN**.
- Authenticated users are redirected to their profile page.

### Exercise 02: JSP Profile Page
- The `/profile` page should be implemented as a **JSP file**.
- Display the following user details:
  - First name
  - Last name
  - Email
- Display a **history of user logins** (date/time/IP address).
- Implement an **avatar upload** feature:
  - Images should be stored on disk.
  - Ensure unique file names for uploaded images.
  - Provide links to view uploaded images.
  - Images should be accessible via `http://host:port/app-name/images/{image-unique-name}`.
