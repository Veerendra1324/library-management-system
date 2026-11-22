# Library Management System

A command-line Library Management System built with **Java 17, JDBC, SQLite and Maven**. It demonstrates object-oriented programming, database operations, input validation, and transaction handling.

## Features

- Add, view, search and remove books
- Register and list library members
- Issue available books to members
- Return issued books and automatically update availability
- View issue/return history
- Persistent storage using SQLite (database is created automatically)

## Tech stack

- Java 17
- JDBC
- SQLite
- Maven

## Prerequisites

- JDK 17 or later
- Maven 3.8+

Check your setup:

```bash
java -version
mvn -version
```

## Run the project

1. Extract the ZIP file.
2. Open a terminal in the project folder (the folder containing `pom.xml`).
3. Run:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.veerendra.library.Main"
```

If your Maven installation does not have the `exec` goal available, use:

```bash
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt
java -cp "target/classes;$(cat classpath.txt)" com.veerendra.library.Main
```

On Windows PowerShell, the classpath command can be run as:

```powershell
mvn dependency:build-classpath "-Dmdep.outputFile=classpath.txt"
$cp = Get-Content classpath.txt
java -cp "target/classes;$cp" com.veerendra.library.Main
```

The SQLite database file `library.db` is created in the project directory on first run.

## Suggested demo flow

1. Add a book (for example, Clean Code).
2. Register a member.
3. Search for the book by title.
4. Issue the book using its book ID and member ID.
5. View issue history.
6. Return the book and verify that it becomes available again.

## Project structure

```text
src/main/java/com/veerendra/library/
├── Main.java
├── Database.java
├── Book.java
├── Member.java
├── Loan.java
├── LibraryService.java
└── InputHelper.java
```

## Database design

- `books`: title, author, ISBN, and available-copy count
- `members`: member name and email
- `loans`: book/member references, issue date, due date, and return date

## Resume description

**Library Management System | Java, JDBC, SQLite, Maven**

- Developed a console-based library management application using Java and JDBC with SQLite for persistent data storage.
- Implemented book and member management, book search, issue/return workflows, and loan history.
- Applied object-oriented design, input validation, SQL queries, and database transaction handling to maintain data consistency.

Only include features you have personally run, tested, and can explain in an interview. Extend the project with unit tests, a GUI or Spring Boot REST APIs as your next improvement.

## GitHub upload

Create a new empty repository on GitHub, for example `library-management-system`. Then run these commands from the project folder:

```bash
git init
git add .
git commit -m "Add Java Library Management System"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/library-management-system.git
git push -u origin main
```

Replace `YOUR-USERNAME` with your GitHub username. Do not upload `library.db` or your IDE files.
