# College Library Management System (BCA V Semester Minor Project)

Java 11 + JSP + Servlets + JDBC + MySQL 8 + Bootstrap 5, run on Apache Tomcat 9.

## 1. Set up the database
1. Start MySQL.
2. Run `database/library_db.sql` (MySQL Workbench: File > Open SQL Script > Run, or `mysql -u root -p < database/library_db.sql`).
3. Open `src/main/java/com/library/util/DBConnection.java` and set your MySQL `USER` and `PASSWORD`.

## 2. Run the project
**NetBeans:** File > Open Project > select this folder (it is a Maven project). Add Apache Tomcat **9.x** under Tools > Servers. Right-click project > Run.
**IntelliJ IDEA (Ultimate):** Open the folder as a Maven project > Run > Edit Configurations > Tomcat Server (Local) > Tomcat 9 > Deployment > add `LibraryManagementSystem:war exploded`.
**Without an IDE:** `mvn clean package`, then copy `target/LibraryManagementSystem.war` into Tomcat's `webapps` folder.

Open: http://localhost:8080/LibraryManagementSystem/  (use Tomcat **9**, not 10: this code uses the `javax.servlet` packages.)
The pages load Bootstrap, icons and Chart.js from a CDN, so the computer needs internet access.

## 3. Sample logins
| Role | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Librarian | librarian | lib123 |
| Student | student | stud123 |

## 4. Folder structure
```
database/library_db.sql                 tables + sample data
src/main/java/com/library/
  util/      DBConnection, PasswordUtil (SHA-256), Flash, Param, Validator, ChartUtil
  model/     User, Student, Librarian, Book, Category, IssueRecord
  dao/       UserDAO, StudentDAO, BookDAO, IssueDAO, StatsDAO, ReportDAO, SettingsDAO   (all SQL lives here)
  servlet/   Login, Logout, Register, Dashboard, Book, Catalog, Student, Librarian, Issue, Return, Fine, Report, Settings
  filter/    AuthFilter (login check + role-based access)
src/main/webapp/
  css/style.css, js/main.js
  WEB-INF/views/   all JSP pages (not reachable by URL; only through servlets)
  WEB-INF/views/common/   taglibs.jsp, header.jsp (sidebar), footer.jsp
  WEB-INF/tags/    statusBadge.tag, cover.tag
```

## 5. How the pieces connect (MVC)
Browser -> **AuthFilter** -> **Servlet** (controller) -> **DAO** (SQL via JDBC, returns **model** objects) -> servlet stores them in the request -> **JSP** (view) shows them.

## 6. Business rules
- Fine = overdue days x fine per day (default Rs 5, changeable in Settings). Created when a late book is returned.
- A student can hold at most 3 books (Settings), cannot take the same book twice, and cannot borrow while a fine is unpaid.
- Issue and return run inside database transactions, and the available-copies count can never go below zero.
- Books or students with issue history cannot be deleted (foreign keys protect the records).

## 7. Quick test checklist
1. Log in as `librarian`. Dashboard cards and charts show numbers from MySQL.
2. Books: add a book, edit its copies, search by author and by ISBN, filter by category.
3. Students: register one, open History.
4. Issue Book: pick a student and book. Available copies drop by 1 on the Books page.
5. Return Book: accept the overdue sample book (Rohan, Effective Java). A fine of Rs 80 appears under Fines (16 days x Rs 5).
6. Fines: mark it paid. Reports: print and download CSV.
7. Log in as `student`: personal dashboard, catalog, due dates. Try opening /librarians: you are sent back with a message.
8. Log in as `admin`: Librarians and Settings pages.
