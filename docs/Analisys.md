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

Additional input rules (`DataValidator`):

- Identification: digits only, 6 to 12 characters.
- Full name: letters (accents allowed), spaces, apostrophes and hyphens, 3 to 60 characters.
- Phone: required for athletes, 7 to 10 digits.
- Age: 5 to 120. Jersey number: 0 to 99. Height: 0.5 to 2.5 meters.
- An identification can only belong to one athlete; registering a duplicate is rejected.

## 3. Functional Requirements (FR)

- **FR1 - Athlete management:** create, query, update and delete athletes (id, full name, age, phone, jersey number). *(Create and query are available in the UI; update and delete are supported by the service and repository but have no screen yet.)*
- **FR2 - Discipline assignment:** assign an athlete to football or basketball, capturing discipline-specific data (playing position for football, height for basketball).
- **FR3 - Automatic category and fee:** determine the category from the age and assign the monthly fee.
- **FR4 - Fee payment control:** record monthly payments and show a payment status summary. *(Pending: the model does not store a payment status yet.)*
- **FR5 - Coaches:** register coaches and assign them to a discipline and category. *(Service and repository implemented; no screen yet.)*
- **FR6 - Health records:** keep each athlete's fitness status, restrictions and injury history. *(Service and repository implemented; no screen yet.)*

## 4. Non-Functional Requirements (NFR)

- **NFR1 - Platform:** Java (JDK 17) built with Apache Maven.
- **NFR2 - User interface:** Java Swing (`JFrame`, `JTable`, `JOptionPane`) in `com.creatinaboys.ui`.
- **NFR3 - Architecture:** 4-layer architecture (`ui`, `service`, `repository`, `model`) plus the cross-cutting packages `exceptions` and `validation`.
- **NFR4 - Error handling:** custom checked exceptions (`AthleteNotFoundException`, `CoachNotFoundException`, `InvalidAgeException`, `ValidationException`) and the unchecked `PersistenceException` for storage errors.
- **NFR5 - Persistence:** UTF-8 text files in `data/`, written through a temporary file with a `.bak` copy; invalid lines are skipped without blocking the rest of the file.

## 5. Design Principles

- **GRASP:** Controller and Creator (`AthleteService`), Information Expert (`BillingService`), Low Coupling, High Cohesion.
- **SOLID:** SRP (one repository per entity, `TextCodec` and `FileStorage` for encoding and file access), OCP (`Athlete` hierarchy and parser registry), LSP (`Footballer`/`Basketballer`), ISP (small interfaces such as `IBillingService` and `IAthleteRepository`), DIP (services depend on interfaces).