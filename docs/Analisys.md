# Requirements and Analysis Document

## 1. Problem Statement

Creatina-Boys Sports Club manages athlete memberships, enrollment by discipline
(football and basketball) and monthly fees manually. This causes wrong age
category assignment, inconsistent fee collection and no consolidated reports.

## 2. Business Rules (Fee Matrix)

| Category | Age range      | Monthly fee (COP) |
| :------- | :------------- | :---------------- |
| Children | 5 to 12 years  | $50.000           |
| Youth    | 13 to 17 years | $70.000           |
| Adults   | 18+ years      | $100.000          |

Athletes younger than 5 years are rejected with `InvalidAgeException`.

## 3. Functional Requirements (FR)

- **FR1 - Athlete management:** create, query, update and delete athletes (id, full name, age, phone, jersey number).
- **FR2 - Discipline assignment:** assign an athlete to football or basketball, capturing discipline-specific data (playing position for football, height for basketball).
- **FR3 - Automatic category and fee:** determine the category from the age and assign the monthly fee.
- **FR4 - Fee payment control:** record monthly payments and show a payment status summary. *(Pending: not implemented yet, see project status.)*

## 4. Non-Functional Requirements (NFR)

- **NFR1 - Platform:** Java (JDK 17) built with Apache Maven.
- **NFR2 - User interface:** Java Swing (`JFrame`, `JTable`, `JOptionPane`) in `com.creatinaboys.ui`.
- **NFR3 - Architecture:** 4-layer architecture (`ui`, `service`, `repository`, `model`) plus a cross-cutting `exceptions` package.
- **NFR4 - Error handling:** custom checked exceptions (`AthleteNotFoundException`, `InvalidAgeException`).

## 5. Design Principles

- **GRASP:** Controller and Creator (`AthleteService`), Information Expert (`BillingService`), Low Coupling, High Cohesion.
- **SOLID:** SRP (`AthleteRepository`), OCP (`Athlete` hierarchy), LSP (`Footballer`/`Basketballer`), ISP (small `IBillingService` and `IAthleteRepository`), DIP (service depends on interfaces).