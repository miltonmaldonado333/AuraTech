# Creatina-Boys Sports Club Management System

A 3-tier Java desktop application built with Apache Maven and Java Swing for managing athletes, discipline categorization, age-based fee evaluation, and monthly subscription payments at **Creatina-Boys Sports Club**.

---

## Features

- **Athlete Management (CRUD):** Register, update, view, and delete athletes.
- **Discipline Assignment:** Specialized attributes for Footballers and Basketballers.
- **Automated Age Categorization:** Automatic classification and monthly fee calculation based on age:
  - **Children (5-12 years):** $50,000 COP
  - **Youth (13-17 years):** \,000 COP
  - **Adults (18+ years):** $100,000 COP
- **3-Tier Architecture:** Complete decoupling between UI (\com.creatinaboys.Ui\), Business Logic (\com.creatinaboys.service\), and Data Access (\com.creatinaboys.Repository\).

---

## Project Structure

\\\ ext
AuraTech/
+-- docs/
� +-- IAUsage/
� � +-- leader.md
� � +-- developer1.md
� +-- analysis.md
� +-- class-diagram.md
� +-- hierarchy-diagram.md
� +-- layers-diagram.md
+-- src/
� +-- main/
� +-- java/
� +-- com/
� +-- creatinaboys/
� +-- model/
� +-- Repository/
� +-- service/
� +-- Ui/
� +-- Main.java
+-- .gitignore
+-- pom.xml
+-- README.md
+-- team.md
\\\

---

## Prerequisites & Installation

1. **JDK 17** or higher installed.
2. **Apache Maven 3.8+** installed.
3. Clone the repository:
   \\\ash
   git clone https://github.com/miltonmaldonado333/AuraTech.git
   cd AuraTech
   \\\
4. Build and run the application:
   \\\ash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.creatinaboys.Main"
   \\\
   "@

# 6. Crear docs/analysis.md

Set-Content -Path "docs\analysis.md" -Value @"

# Project Requirements & Analysis Document

## 1. Problem Statement

Creatina-Boys Sports Club currently tracks athlete memberships, discipline enrollments (Football & Basketball), and monthly dues manually. This leads to improper age categorization, inconsistent fee billing, and lack of consolidated reporting.

## 2. Business Rules (Pricing Matrix)

| Category     | Age Range     | Monthly Fee (COP) |
| :----------- | :------------ | :---------------- |
| **Children** | 5 � 12 years  | 50,000 COP        |
| **Youth**    | 13 � 17 years | $70,000 COP       |
| **Adults**   | 18+ years     | 110,000 COP       |

## 3. Functional Requirements (FR)

- **FR1 - Athlete Enrollment:** The system must allow creating, reading, updating, and deleting athlete records (ID, Full Name, Age, Phone).
- **FR2 - Discipline Assignment:** The system must allow assigning athletes to Football or Basketball, capturing sport-specific attributes (e.g., Playing Position for Football, Height for Basketball).
- **FR3 - Automated Age Evaluation:** The system must automatically determine the athlete's age category and assign the corresponding monthly subscription fee.
- **FR4 - Dues Management:** The system must track monthly payment statuses and display summary tables.

## 4. Non-Functional Requirements (NFR)

- **NFR1 - Platform:** Developed in Java (JDK 17) using Maven build orchestration.
- **NFR2 - User Interface:** Built using Java Swing (\JFrame\, \JTable\, \JOptionPane\) inside \com.creatinaboys.Ui\.
- **NFR3 - Architecture:** Strict adherence to 3-Tier Architecture (\Ui\, \service\, \Repository\).
- **NFR4 - Exception Handling:** Custom domain exceptions for data validation and repository errors.
