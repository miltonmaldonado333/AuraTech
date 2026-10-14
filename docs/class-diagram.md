# UML Class Diagram

\\\mermaid
classDiagram
    class Athlete {
        <<abstract>>
        - String id
        - String fullName
        - int age
        - String phone
        - CategoryEnum category
        - double monthlyFee
        + calculateCategoryAndFee()*
    }

    class Footballer {
        - String position
        - String preferredFoot
        + calculateCategoryAndFee()
    }

    class Basketballer {
        - double heightCm
        - String primaryRole
        + calculateCategoryAndFee()
    }

    class CategoryEnum {
        <<enumeration>>
        CHILDREN
        YOUTH
        ADULT
    }

    class AthleteRepository {
        <<interface>>
        + save(Athlete athlete)
        + findById(String id) Athlete
        + findAll() List~Athlete~
        + update(Athlete athlete)
        + delete(String id)
    }

    class AthleteService {
        - AthleteRepository athleteRepository
        + registerAthlete(Athlete athlete)
        + getAllAthletes() List~Athlete~
        + getAthletesByDiscipline(String discipline) List~Athlete~
    }

    Athlete <|-- Footballer
    Athlete <|-- Basketballer
    Athlete --> CategoryEnum
    AthleteService --> AthleteRepository
\\\
"@

# 8. Crear docs/hierarchy-diagram.md
Set-Content -Path "docs\hierarchy-diagram.md" -Value @"
# Class Hierarchy Diagram

\\\mermaid
graph TD
    Athlete["[Abstract Class] Athlete"]
    Footballer["[Concrete Class] Footballer"]
    Basketballer["[Concrete Class] Basketballer"]
    CategoryEnum["[Enum] CategoryEnum"]

    Athlete -->|Inherited by| Footballer
    Athlete -->|Inherited by| Basketballer
    Athlete -->|Uses| CategoryEnum

    subgraph Footballer Details
        Footballer --- F1["+ position: String"]
        Footballer --- F2["+ preferredFoot: String"]
    end

    subgraph Basketballer Details
        Basketballer --- B1["+ heightCm: double"]
        Basketballer --- B2["+ primaryRole: String"]
    end
\\\
"@

# 9. Crear docs/layers-diagram.md
Set-Content -Path "docs\layers-diagram.md" -Value @"
# 3-Tier Software Architecture Diagram

\\\mermaid
graph TB
    subgraph Presentation Layer [Layer 1: Presentation / Ui]
        UI["Java Swing Frames\n(FrmPrincipal, FrmRegister, FrmConsult)"]
        Main["Main Entry Point\n(Main.java)"]
    end

    subgraph Service Layer [Layer 2: Service / Business Logic]
        Service["AthleteService / BillingService\n(Business Validation & Fee Rules)"]
    end

    subgraph Persistence Layer [Layer 3: Repository / Data Access]
        Repo["AthleteRepository / FileRepository\n(File I/O, Data Persistence)"]
        Model["Domain Models\n(Athlete, Footballer, Basketballer)"]
    end

    Main --> UI
    UI --> Service
    Service --> Repo
    Service --> Model
    Repo --> Model
\\\
"@

# 10. Crear docs/IAUsage/leader.md
Set-Content -Path "docs\IAUsage\leader.md" -Value @"
# AI Tool Usage Log — Technical Leader

**Member:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  

---

## Logged Interactions

### 1. Project Initialization & Architecture Setup
* **Prompt/Query:** Generation of 3-tier architecture package layouts (\com.creatinaboys.Ui\, \com.creatinaboys.service\, \com.creatinaboys.Repository\, \com.creatinaboys.model\), Maven \pom.xml\ dependencies, and GitFlow branch setup for Java Swing desktop applications.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Adapted generated folder hierarchy and Maven build configurations to meet Universidad Popular del Cesar (UPC) guidelines.

### 2. User Interface Design (Java Swing)
* **Prompt/Query:** Designing responsive \JFrame\ and \JTable\ components in Java Swing with custom event listeners and layout managers in \com.creatinaboys.Ui\.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Reviewed and incorporated generated Swing form structures into \FrmPrincipal\ and \FrmRegister\.

### 3. Technical Documentation
* **Prompt/Query:** Drafting English technical specifications for \README.md\, \	eam.md\, and project analysis documents.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Validated and added directly into project documentation.
