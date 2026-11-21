-- =====================================================================
-- College Library Management System  |  Database script (MySQL 8)
-- Import this file into a NEW / EMPTY database named library_db. Do not re-run over existing data.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

-- 1. users : one row for every person who can log in (admin, librarian, student)
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,               -- SHA-256 hex
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    role          ENUM('ADMIN','LIBRARIAN','STUDENT') NOT NULL,
    status        ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. students : extra details of users whose role = STUDENT
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL UNIQUE,
    roll_no    VARCHAR(30) NOT NULL UNIQUE,
    course     VARCHAR(50) NOT NULL,
    semester   TINYINT NOT NULL CHECK (semester BETWEEN 1 AND 12),
    phone      VARCHAR(15),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. librarians : extra details of users whose role = LIBRARIAN
CREATE TABLE librarians (
    librarian_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL UNIQUE,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    phone         VARCHAR(15),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. categories
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(60) NOT NULL UNIQUE
);

-- 5. books
CREATE TABLE books (
    book_id          INT AUTO_INCREMENT PRIMARY KEY,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    title            VARCHAR(200) NOT NULL,
    author           VARCHAR(120) NOT NULL,
    publisher        VARCHAR(120),
    category_id      INT NOT NULL,
    publication_year SMALLINT,
    total_copies     INT NOT NULL CHECK (total_copies >= 0),
    available_copies INT NOT NULL CHECK (available_copies >= 0),
    cover_url        VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 6. book_issues : one row per issue transaction
CREATE TABLE book_issues (
    issue_id    INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT NOT NULL,
    student_id  INT NOT NULL,
    issued_by   INT NOT NULL,                          -- users.user_id of staff
    issue_date  DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE NULL,
    status      ENUM('ISSUED','RETURNED') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (book_id)    REFERENCES books(book_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (issued_by)  REFERENCES users(user_id)
);

-- 7. book_returns : one row per returned issue
CREATE TABLE book_returns (
    return_id    INT AUTO_INCREMENT PRIMARY KEY,
    issue_id     INT NOT NULL UNIQUE,
    return_date  DATE NOT NULL,
    received_by  INT NOT NULL,
    overdue_days INT NOT NULL DEFAULT 0,
    FOREIGN KEY (issue_id)    REFERENCES book_issues(issue_id),
    FOREIGN KEY (received_by) REFERENCES users(user_id)
);

-- 8. fines : created only when a book is returned late
CREATE TABLE fines (
    fine_id   INT AUTO_INCREMENT PRIMARY KEY,
    issue_id  INT NOT NULL UNIQUE,
    amount    DECIMAL(8,2) NOT NULL,
    status    ENUM('UNPAID','PAID') NOT NULL DEFAULT 'UNPAID',
    paid_date DATE NULL,
    FOREIGN KEY (issue_id) REFERENCES book_issues(issue_id)
);

-- 9. settings : key/value pairs editable by admin (fine rate etc.)
CREATE TABLE settings (
    setting_key   VARCHAR(40) PRIMARY KEY,
    setting_value VARCHAR(100) NOT NULL
);

-- ---------------------------- SAMPLE DATA ----------------------------
-- Passwords:  admin / admin123 , librarian / lib123 , student / stud123
INSERT INTO users (username,password_hash,full_name,email,role) VALUES
('admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','System Administrator','admin@college.edu','ADMIN'),
('librarian','6518454a49ab2912238b510b2221f0fc1ce404986d3fb94bb34311ff6069d467','Meena Sharma','meena@college.edu','LIBRARIAN'),
('student','19b9dd3e24fad97f47400340f81e118ca3f88be2ee3503b34b9bde0ad5ad7ebd','Rohan Verma','rohan@student.edu','STUDENT'),
('priya','19b9dd3e24fad97f47400340f81e118ca3f88be2ee3503b34b9bde0ad5ad7ebd','Priya Singh','priya@student.edu','STUDENT');

INSERT INTO librarians (user_id,employee_code,phone) VALUES (2,'LIB001','9876500001');
INSERT INTO students (user_id,roll_no,course,semester,phone) VALUES
(3,'BCA2023001','BCA',5,'9876500002'),(4,'BCA2023002','BCA',5,'9876500003');

INSERT INTO categories (name) VALUES ('Programming'),('Database'),('Networking'),('Mathematics'),('Fiction'),('Management');

INSERT INTO books (isbn,title,author,publisher,category_id,publication_year,total_copies,available_copies) VALUES
('9780134685991','Effective Java','Joshua Bloch','Addison-Wesley',1,2018,5,5),
('9780596009205','Head First Java','Kathy Sierra','OReilly',1,2005,6,6),
('9780321356680','Java: The Complete Reference','Herbert Schildt','McGraw Hill',1,2020,4,4),
('9780078022159','Database System Concepts','Abraham Silberschatz','McGraw Hill',2,2019,4,4),
('9780133970777','Database Systems','Ramez Elmasri','Pearson',2,2016,3,3),
('9780132126953','Computer Networks','Andrew Tanenbaum','Pearson',3,2010,3,3),
('9780070634145','Higher Engineering Mathematics','B. S. Grewal','Khanna',4,2017,5,5),
('9780143031369','The Guide','R. K. Narayan','Penguin',5,2006,2,2),
('9780062316097','Sapiens','Yuval Noah Harari','Harper',5,2015,3,3),
('9780066620992','Good to Great','Jim Collins','Harper Business',6,2001,2,2);

INSERT INTO settings VALUES ('fine_per_day','5'),('loan_days','14'),('max_books','3'),('library_name','College Central Library');

-- Sample transaction history so dashboard charts and reports have data
-- (students: 1 = Rohan, 2 = Priya; staff user_id 2 = librarian)
INSERT INTO book_issues (book_id,student_id,issued_by,issue_date,due_date,return_date,status) VALUES
(1,1,2,DATE_SUB(CURDATE(),INTERVAL 30 DAY),DATE_SUB(CURDATE(),INTERVAL 16 DAY),NULL,'ISSUED'),        -- overdue
(4,2,2,DATE_SUB(CURDATE(),INTERVAL 5 DAY), DATE_ADD(CURDATE(),INTERVAL 9 DAY), NULL,'ISSUED'),        -- on time
(2,1,2,DATE_SUB(CURDATE(),INTERVAL 40 DAY),DATE_SUB(CURDATE(),INTERVAL 26 DAY),DATE_SUB(CURDATE(),INTERVAL 20 DAY),'RETURNED'), -- returned late
(3,2,2,DATE_SUB(CURDATE(),INTERVAL 20 DAY),DATE_SUB(CURDATE(),INTERVAL 6 DAY), DATE_SUB(CURDATE(),INTERVAL 10 DAY),'RETURNED'); -- returned on time
INSERT INTO book_returns (issue_id,return_date,received_by,overdue_days) VALUES
(3,DATE_SUB(CURDATE(),INTERVAL 20 DAY),2,6),(4,DATE_SUB(CURDATE(),INTERVAL 10 DAY),2,0);
INSERT INTO fines (issue_id,amount,status,paid_date) VALUES (3,30.00,'PAID',DATE_SUB(CURDATE(),INTERVAL 19 DAY));
UPDATE books SET available_copies = available_copies - 1 WHERE book_id IN (1,4);
