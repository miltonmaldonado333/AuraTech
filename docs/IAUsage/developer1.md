# ARTIFICIAL INTELLIGENCE USAGE REPORT – DEVELOPER 1

**Project:** Aura-Tech – Creatina-Boys Sports Club Management System

**University:** Universidad Popular del Cesar

**Course:** Computer Programming III

**Team Member:** Jesus Manuel Martinez Baute

**Role:** Developer 1

**Development Branch:** `feature/domain-persistence`

**AI Tools Used:** Gemini, Claude, and ChatGPT

## 1. Purpose of Using Artificial Intelligence

During the development of the Aura-Tech project, artificial intelligence tools were used to support programming, code review, error correction, and technical documentation.

AI helped analyze different implementation approaches, identify potential issues in the source code, and improve the application of Object-Oriented Programming (OOP), GRASP patterns, and SOLID principles.

AI-generated suggestions were reviewed and adapted to the project's requirements. Artificial intelligence was used as a supporting tool rather than a replacement for the developer's analysis and decision-making.

## 2. AI Usage Activity Log

### Activity 1. Domain Model Design

**AI Tool:** Gemini

**Prompt:** I requested assistance in structuring the abstract `Athlete` class and its subclasses, `Footballer` and `Basketballer`, using inheritance, encapsulation, abstraction, and polymorphism.

**Application:** The entities were organized within the `com.creatinaboys.model` package, defining shared attributes and discipline-specific behaviors.

### Activity 2. Repository Implementation and Data Persistence

**AI Tools:** Gemini and Claude

**Prompt:** I requested guidance on implementing repositories to store, retrieve, update, and delete athlete information.

**Application:** The `IAthleteRepository`, `AthleteRepository`, and `FileAthleteRepository` components were developed and reviewed to separate data access from business logic.

Utilities such as `FileStorage`, `TextCodec`, and parsers were also incorporated to manage records stored in files.

### Activity 3. Custom Exception Handling

**AI Tools:** Gemini and Claude

**Prompt:** I requested assistance in designing appropriate error handling using custom exceptions.

**Application:** Classes such as `AthleteNotFoundException`, `InvalidAgeException`, `ValidationException`, `DuplicateDataException`, and `PersistenceException` were reviewed.

This improved error identification and handling across the different application layers.

### Activity 4. Application of GRASP and SOLID Principles

**AI Tool:** Claude

**Prompt:** I requested a review of the domain model and repository classes to identify improvements related to single responsibility, low coupling, high cohesion, and extensibility.

**Application:** Responsibilities were reorganized, interfaces were used, and separation between entities, services, and persistence components was maintained.

### Activity 5. Data Storage Error Correction

**AI Tool:** Claude

**Prompt:** I requested an analysis of issues involving duplicate records, special characters, corrupted files, and incomplete validations.

**Application:** Duplicate identifier handling, UTF-8 encoding, invalid record processing, and secure file storage using temporary files and `.bak` backups were reviewed.

### Activity 6. Input Data Validation

**AI Tool:** Claude

**Prompt:** I requested a review of the validation rules for identification numbers, names, phone numbers, ages, jersey numbers, and heights.

**Application:** The `DataValidator` utility was improved, and validation checks in `AthleteService` were reinforced to prevent invalid information from being registered.

### Activity 7. Athlete Identification by Discipline

**AI Tool:** Claude

**Prompt:** I requested guidance on allowing a person to register in multiple sports disciplines without creating duplicate registrations within the same discipline.

**Application:** The `AthleteKey`, `AthleteUniquenessPolicy`, and `DuplicateDataException` components were incorporated.

Athlete identification using identification number and discipline was reviewed, ensuring independent memberships and payment balances for each registration.

### Activity 8. Payment Migration and Financial Validation

**AI Tool:** Claude

**Prompt:** I requested assistance reviewing athlete payments to prevent inconsistencies when managing multiple sports disciplines.

**Application:** `PaymentDisciplineMigration` was incorporated to adapt previous payment records.

Validation rules for positive amounts, outstanding balances, and partial payments were also reviewed.

### Activity 9. Search Functionality Improvements

**AI Tool:** Claude

**Prompt:** I requested improvements to athlete and coach searches so that results would not be affected by uppercase letters or accent marks.

**Application:** The `SearchText` utility was incorporated, allowing searches by identification number, name, discipline, and category.

### Activity 10. Component and Functionality Review

**AI Tool:** Claude

**Prompt:** I requested a comparison between the project's requirements and its implemented features to identify possible inconsistencies.

**Application:** Athlete registration validations, coach category displays, and consistency between services and graphical interface components were reviewed.

### Activity 11. UML Diagram Development

**AI Tools:** Gemini, Claude, and ChatGPT

**Prompt:** I requested assistance generating and correcting UML diagrams using Mermaid syntax based on the project's existing classes and relationships.

**Application:** The following files were developed and reviewed:

- `docs/class-diagram.md`
- `docs/hierarchy-diagram.md`
- `docs/Layers_diagram.md`

These diagrams represent classes, attributes, methods, inheritance relationships, interfaces, and the system architecture.

### Activity 12. UML Diagram Updates Based on the Final Source Code

**AI Tool:** ChatGPT

**Prompt:** I provided the complete Aura-Tech project as a ZIP file and requested updates to the original Mermaid diagrams while preserving their structure and incorporating newly implemented components.

**Application:** The diagrams were updated to include classes such as `GeneralAthlete`, `CategoryService`, `DashboardService`, and `HealthPanel`, as well as PostgreSQL repositories.

Relationships between the presentation, service, repository, and model layers were reviewed, including database integration.

### Activity 13. Architecture and PostgreSQL Persistence Review

**AI Tool:** ChatGPT

**Prompt:** I requested an analysis of the updated source code to identify changes in the application's architecture and data persistence mechanisms.

**Application:** New persistence components were identified, including:

- `PostgresAthleteRepository`
- `PostgresCategoryRepository`
- `PostgresDisciplineRepository`

This review helped document the integration of PostgreSQL alongside repositories that continue to use local file storage.

### Activity 14. Project Documentation Update

**AI Tools:** Claude and ChatGPT

**Prompt:** I requested a review and update of the project documentation using the latest Aura-Tech source code as the reference.

**Application:** Sections covering functional and non-functional requirements, business rules, domain models, class identification, attributes, methods, UML relationships, and software architecture were updated.

Newly implemented features, including athlete editing, category and discipline management, health module integration, and PostgreSQL persistence, were also documented.

### Activity 15. Source Code and Documentation Consistency Review

**AI Tool:** ChatGPT

**Prompt:** I requested verification that the technical documentation accurately represented the classes, interfaces, repositories, services, and features implemented in the updated project.

**Application:** Class names, interfaces, repository implementations, and service dependencies were reviewed to maintain consistency between the source code and the documentation.

## 3. Contributions of Artificial Intelligence

Artificial intelligence provided support in several areas of the project:

- **Programming:** Assistance with abstract classes, inheritance, interfaces, and data persistence.
- **Error Detection:** Identification of validation problems, duplicate records, and storage inconsistencies.
- **Software Architecture:** Review of responsibilities and relationships between application layers.
- **Documentation:** Updates to technical information based on changes in the source code.
- **UML Diagrams:** Creation and modification of class, hierarchy, and layered architecture diagrams using Mermaid.
- **Code Quality:** Support for applying GRASP patterns and SOLID design principles.

## 4. Developer Responsibilities

As Developer 1, my main responsibilities included the domain model, repositories, custom exceptions, data validation, and UML diagrams.

Artificial intelligence tools were used to obtain suggestions, analyze possible errors, and improve technical documentation.

The proposed solutions were reviewed before being incorporated into the project, ensuring compatibility between modules and compliance with the established requirements.

## 5. Conclusion

Artificial intelligence was a valuable supporting tool throughout the development of Aura-Tech, particularly in reviewing the domain model, data persistence, validations, and technical documentation.

Its use helped identify errors, explore alternative solutions, and update UML diagrams according to the evolution of the software.

As a developer, this process also strengthened my knowledge of Java, Object-Oriented Programming, layered architecture, data persistence, and software engineering best practices.

**AI was used as a technical and academic support tool, while reviewing, adapting, and integrating the proposed solutions remained part of my responsibilities as a developer.**