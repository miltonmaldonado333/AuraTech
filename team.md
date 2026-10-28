# Team Structure & Roles

## Project: Creatina-Boys Sports Club Management System (AuraTech)

**Institution:** Universidad Popular del Cesar (UPC)  
**Course:** Computer Programming III  
**Instructor:** Eng. Alfredo Bautista  

---

## Team Members

### 1. Milton Andrés Maldonado Salcedo
* **Role:** Technical Leader
* **Branch:** `feature/gui-services-main`
* **Responsibilities:**
  * Graphical User Interface (`com.creatinaboys.ui`)
  * Service / Logic Layer (`com.creatinaboys.service`)
  * Application Main Entry Point (`com.creatinaboys.Main`)
  * Maven Build Configuration (`pom.xml`)
  * Repository & Project Documentation (`README.md`, `team.md`, `docs/IAUsage/leader.md`)

### 2. Jesus Martinez Baute
* **Role:** Developer 1
* **Branch:** `feature/domain-persistence`
* **Responsibilities:**
  * Domain Model Layer (`com.creatinaboys.model`)
  * Persistence / Repository Layer (`com.creatinaboys.repository`)
  * Custom Exception Handling (`com.creatinaboys.exceptions`)
  * Input Validation (`com.creatinaboys.validation`)
  * UML Architecture & Diagrams (`docs/class-diagram.md`, `docs/hierarchy-diagram.md`, `docs/layers-diagram.md`)
  * Analysis Documentation (`docs/analysis.md`, `docs/IAUsage/developer1.md`)

---

## GitFlow Branching Strategy

* **`main`**: Production-ready stable releases.
* **`develop`**: Integration branch for feature merging.
* **`feature/gui-services-main`**: Assigned to Milton Maldonado for UI, Services, and Maven setup.
* **`feature/domain-persistence`**: Assigned to Jesus Martinez for Domain Models, Repository, Exceptions, Validation, and UML Specs.