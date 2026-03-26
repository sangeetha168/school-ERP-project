# school-ERP-project
🎓 School ERP – Student Management System
📌 Project Overview

School ERP is a full-stack Java web application developed using Java, Servlets, JSP, Hibernate ORM, and MySQL following the MVC architecture.

The system is designed to manage student academic data, courses, attendance, and user authentication with role-based access control. It demonstrates strong backend development skills, database design, and secure web application implementation.

This project showcases real-world enterprise backend concepts such as:

MVC layered architecture
Role-based authentication & authorization
CRUD operations using Hibernate
Secure password encryption
Relational database schema design
Session management
🚀 Tech Stack
🔹 Backend
Java (JDK 21)
Servlets
JSP
Hibernate ORM
JDBC
Spring Security (if used in newer version)
🔹 Database
MySQL
🔹 Frontend
HTML5
CSS3
Bootstrap
🔹 Build Tool
Maven
🔹 Server
Apache Tomcat
🏗️ Architecture

The project follows the MVC (Model-View-Controller) pattern:

1️⃣ Model Layer
Entity classes mapped using Hibernate annotations
Represents database tables such as:
Student
Course
Attendance
User
ExamScore
2️⃣ Controller Layer
Servlets handle HTTP requests
Processes user input
Calls service layer
Redirects to JSP views
3️⃣ View Layer
JSP pages for UI rendering
Uses Expression Language (EL)
Bootstrap for responsive UI
🔐 Authentication & Authorization
Secure login system
Role-based access:
ADMIN
STUDENT
TEACHER
Password encryption using BCrypt
Session management for logged-in users
📊 Features
👨‍🎓 Student Module
Student Registration
View Student Details
Update Student Information
Delete Student
View Attendance
View Exam Scores
📚 Course Module
Add Courses
Assign Courses to Students
View Course List
📅 Attendance Module
Mark Attendance
View Attendance by Student
Filter by Class & Section
📝 Exam Module
Add Exam Scores
View Score Reports
Calculate Performance
👨‍💼 Admin Module
Manage Users
Assign Roles
Monitor System Data
🗄️ Database Design

The database is designed using normalized relational schema.

Tables:
users
students
courses
attendance
exam_scores
Relationships:
One-to-Many (Student → Attendance)
One-to-Many (Student → Exam Scores)
Many-to-Many (Student ↔ Course)

Hibernate is used for:

Entity mapping
Relationship management
Transaction handling
