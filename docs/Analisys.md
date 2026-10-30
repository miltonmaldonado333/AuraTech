# Requirements and Analysis Document

## 1. Problem Statement

Creatina-Boys Sports Club manages athlete memberships, enrollment by discipline
(football and basketball) and monthly fees manually. This causes wrong age
category assignment, inconsistent fee collection and no consolidated reports.

The system (AuraTech) is a Java Swing desktop application that registers athletes, assigns their
category and monthly fee automatically, controls the monthly payments and produces the
club's reports. Access is protected with a user account.

## 2. Business Rules

### 2.1 Fee matrix

| Category | Age range      | Monthly fee (COP) |
| :------- | :------------- | :---------------- |
| Children | 5 to 12 years  | $50.000           |
| Youth    | 13 to 17 years | $70.000           |
| Adults   | 18+ years      | $100.000          |

Athletes younger than 5 years are rejected with `InvalidAgeException`.

### 2.2 Membership and payments

- A new athlete is registered as **active** with the membership paid for one month (`paidUntil` = registration date + 1 month).
- The monthly fee can be paid in **several partial payments**. A payment must be greater than zero, cannot exceed the pending balance and is rejected if the month is already paid.
- The payment status of the month is **Paid** (total paid covers the fee), **Partial** (something paid, but not the whole fee) or **Pending** (nothing paid) (`PaymentStatusEnum`).
- When a payment completes the monthly fee, the membership is extended one month and the athlete becomes active again.
- When the athlete list is opened, the activation policy (`MembershipExpirationPolicy`, 5 grace days) deactivates the athletes whose membership has expired.
- An athlete deactivated manually stays inactive until he/she is activated again or renews the membership by paying.

### 2.3 Input rules (`DataValidator`)

- Identification: digits only, 6 to 12 characters.
- Full name: letters (accents allowed), spaces, apostrophes and hyphens, 3 to 60 characters.
- Phone: required for athletes, optional for coaches; when present, 7 to 10 digits.
- Age: 5 to 120. Jersey number: 0 to 99. Height: 0.5 to 2.5 meters.
- An identification can only belong to one athlete; registering a duplicate is rejected.

### 2.4 Access rules (`Authservice`)

- Username: 3 to 20 characters (letters, digits, dot, hyphen and underscore). Password: at least 8 characters with at least one letter and one number, typed twice when creating the account.
- A username can only exist once.
- After 5 failed login attempts the login is locked for 15 seconds.
- A default administrator account is created the first time the application runs.

## 3. Functional Requirements (FR)

- **FR1 - Athlete management:** register athletes (id, full name, age, phone, jersey number); list them in a paginated table; activate/deactivate and delete an athlete. *(Editing the personal data of an existing athlete is supported by the repository, but has no service method or screen yet.)*
- **FR2 - Discipline assignment:** assign an athlete to football or basketball, capturing discipline-specific data (playing position for football, height for basketball). The *Disciplines* screen shows the athletes of each discipline by category and lets the user add a new discipline to the catalog. *(New disciplines are kept in memory only: the athlete form still offers football and basketball, and `FileDisciplineRepository` is not injected in `Main` yet.)*
- **FR3 - Automatic category and fee:** determine the category from the age and assign the monthly fee. The *Categories* screen shows, for each category, its age range, fee, athletes, active athletes and expected income.
- **FR3.1 - Accent- and case-insensitive search:** the search boxes of the athlete list and the coach list must find records by id, full name, discipline and category regardless of upper/lower case or accents (e.g. `ninos`, `NIÑOS` and `Niños` return the same results; `futbol` finds `Fútbol`). The coach list also searches by phone. An empty query shows all records.
- **FR4 - Fee payment control:** record full or partial monthly payments, show the status of the month per athlete (Paid, Partial, Pending) and the payment history. Payments are stored in `data/payments.txt`.
- **FR5 - Coaches:** register, edit, activate/deactivate and delete coaches, and assign each one to a discipline and a category (*Coaches* screen, with search and pagination).
- **FR6 - Health records:** keep each athlete's fitness status, restrictions and injury history. *(Model, service and repository are implemented; there is no screen yet and `HealthService` is not wired in `Main`.)*
- **FR7 - Authentication:** log in with a user account, create new accounts and log out (the login window opens again).
- **FR8 - Dashboard:** the home screen summarizes athletes, income collected and expected, athletes by discipline and category, recent payments and athletes with pending payments, with shortcuts to the main screens.
- **FR9 - Reports:** projected, collected and pending income, compliance percentage, income by discipline and by category, the pending-payment portfolio and the collection month by month.

## 4. Non-Functional Requirements (NFR)

- **NFR1 - Platform:** Java (JDK 17) built with Apache Maven.
- **NFR2 - User interface:** Java Swing in `com.creatinaboys.ui`: frames (`LoginFrame`, `PrincipalFrames`), one panel per screen (`ui.panels`) and reusable custom components (`ui.components`, `ui.theme`).
- **NFR3 - Architecture:** 4-layer architecture (`ui`, `service`, `repository`, `model`) plus the cross-cutting packages `exceptions` and `validation`.
- **NFR4 - Error handling:** custom checked exceptions (`AthleteNotFoundException`, `CoachNotFoundException`, `InvalidAgeException`, `ValidationException`) and the unchecked `PersistenceException` for storage errors.
- **NFR5 - Persistence:** UTF-8 text files in `data/` (`athletes.txt`, `coaches.txt`, `payments.txt`, `users.txt`), written through a temporary file with a `.bak` copy; invalid lines are skipped without blocking the rest of the file.
- **NFR6 - Search normalization:** text comparisons for searching are done through `SearchText` (`com.creatinaboys.ui.components`), which trims the text, removes accents (Unicode NFD decomposition without combining marks) and lowercases it with `Locale.ROOT`. The stored data in `data/` is not modified; only the comparison is normalized.
- **NFR7 - Security:** passwords are never stored in plain text: they are saved with a random salt and hashed with PBKDF2-HMAC-SHA256.

## 5. Design Principles

- **GRASP:** Controller and Creator (`AthleteService`), Information Expert (`BillingService`, `SaleService`), Low Coupling, High Cohesion.
- **SOLID:** SRP (one repository per entity, `TextCodec` and `FileStorage` for encoding and file access, `SearchText` only normalizes text), OCP (`Athlete` hierarchy, parser registry and `IActivationPolicy`, which allows new activation rules without changing `AthleteService`), LSP (`Footballer`/`Basketballer`), ISP (small interfaces such as `IBillingService` and `IAthleteRepository`), DIP (services and screens depend on interfaces; `Main` creates and injects the implementations).

## 6. Pending Work

- Edit screen for an athlete's personal data (FR1).
- Persist the discipline catalog by injecting `FileDisciplineRepository` in `Main`, and let new disciplines be used when registering athletes (FR2).
- Screen and wiring for health records (FR6).