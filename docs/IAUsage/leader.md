# AI Tool Usage Log - Technical Leader

**Member:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  

---

## Logged Interactions

## Phase 1 - Architecture and Foundations (October 14, 2026 - branch `feature/gui-services-main`)

### 1. Project Initialization & Architecture Setup
* **Prompt/Query:** Generation of 3-tier architecture package layouts (`com.creatinaboys.ui`, `com.creatinaboys.service`, `com.creatinaboys.repository`, `com.creatinaboys.model`), Maven `pom.xml` dependencies, and GitFlow branch setup for Java Swing desktop applications.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Adapted generated folder hierarchy and Maven build configurations to meet Universidad Popular del Cesar (UPC) guidelines.

### 2. User Interface Design (Java Swing)
* **Prompt/Query:** Designing responsive `JFrame` and `JTable` components in Java Swing with custom event listeners and layout managers in `com.creatinaboys.ui`.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Reviewed and incorporated generated Swing form structures into `PrincipalFrame` and the registration frames.

### 3. Technical Documentation
* **Prompt/Query:** Drafting English technical specifications for `README.md`, `team.md`, and project analysis documents.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Validated and added directly into project documentation.

### 4. README Translation
* **Prompt/Query:** Translating `README.md` between English and Spanish and fixing the wording problems left by the automatic translation.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Kept the Spanish version as the project README and corrected the technical terms by hand.

### 5. Mermaid Diagram Syntax Fixes
* **Prompt/Query:** Fixing the Mermaid syntax of the class diagram and drafting the first hierarchy and layers diagrams.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Updated `docs/class-diagram.md` and added the first versions of the hierarchy and layers diagrams, later refined by Developer 1.

### 6. Fee Calculation by Age Category
* **Prompt/Query:** Skeleton of `IBillingService` and `BillingService` to determine the category and monthly fee from the athlete's age, and of the `IAthleteRepository` interface used by `AthleteService`.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Reviewed the generated methods and adjusted the age ranges and fees to the club's rules before committing.

---

## Phase 2 - Services, Interface and Access (October 28-30, 2026 - branch `feature/phase-2`)

### 7. Payment and Sales Service
* **Prompt/Query:** Design of `ISaleService` and `SaleService` to register full or partial monthly payments, compute pending balances, estimated monthly revenue and revenue by category.
* **AI Tool Used:** Claude.
* **Output Utilization:** Reviewed the generated methods and connected the service to `IAthleteRepository`, `IBillingService` and `IPaymentRepository` through the constructor.

### 8. Health, Coach and Discipline Services
* **Prompt/Query:** First version of `HealthService`, `CoachService` and `DisciplineService` on top of the repository interfaces created by Developer 1.
* **AI Tool Used:** Claude.
* **Output Utilization:** Adapted the code to the project's exceptions; Developer 1 later fixed the service interfaces and the exception handling.

### 9. UI Package Reorganization and Refactoring
* **Prompt/Query:** Splitting `com.creatinaboys.ui` into `panels`, `components` and `Theme`, creating the new frames and refactoring the Swing panels to reuse components.
* **AI Tool Used:** Claude.
* **Output Utilization:** Applied the new package layout, moved the classes and ran the application to check that every screen still opened.

### 10. Coaches Panel
* **Prompt/Query:** Building `CoachPanel` with a table, pagination, status badges and a form dialog connected to `ICoachService`.
* **AI Tool Used:** Claude.
* **Output Utilization:** Reviewed the generated panel and adjusted columns, labels and button actions; Developer 1 later added the category that each coach trains.

### 11. Login with Protected Passwords
* **Prompt/Query:** Design of `UserAccount`, `Fileuserrepository`, `Authservice` and `LoginFrame` with salted PBKDF2 hashing and a temporary lock after repeated failed attempts.
* **AI Tool Used:** Claude.
* **Output Utilization:** Kept PBKDF2-HMAC-SHA256 with a random salt, set 5 attempts and a 15-second lock, and tested a wrong password, a correct one and the lock countdown manually.

### 12. Home Dashboard
* **Prompt/Query:** Layout of `HomePanel` with club indicators, donut and bar charts drawn with Java 2D, recent payments and athletes with pending payments.
* **AI Tool Used:** Claude.
* **Output Utilization:** Wired the panel to `PrincipalFrames` and refreshed its data each time the *Inicio* screen is opened.

### 13. User Registration
* **Prompt/Query:** Adding account creation to `IAuthService`, `Authservice` and `LoginFrame` (username and password rules, password typed twice, duplicated usernames).
* **AI Tool Used:** Claude.
* **Output Utilization:** Reviewed the rules, kept the validation messages in Spanish and tested the creation of valid and invalid accounts.

### 14. Validations in the UI and Composite Key
* **Prompt/Query:** Moving the input validations to the screens and identifying a registration by document plus discipline (`AthleteKey`, `DuplicateDataException`) so one person can belong to several disciplines.
* **AI Tool Used:** Claude.
* **Output Utilization:** Applied the changes in the repositories, services and panels and checked duplicated documents and payments by discipline manually.

### 15. Quality Documentation and README Update
* **Prompt/Query:** Drafting `docs/traceability.md` (GRASP and SOLID), `docs/CleanCode.md`, `docs/commits.md` and updating `README.md` and this log with the Phase 1 and Phase 2 work.
* **AI Tool Used:** Claude.
* **Output Utilization:** Checked every table against the source code and the git history before committing.