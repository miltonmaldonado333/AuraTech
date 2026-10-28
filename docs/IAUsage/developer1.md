# AI Tool Usage Log - Developer 1

**Member:** Jesus Martines Baute  
**Role:** Developer 1  

---

## Logged Interactions

### 1. Domain Object Modeling & Inheritance
* **Prompt/Query:** Assistance in constructing abstract class \Athlete\ and derived concrete classes \Footballer\ and \Basketballer\ in Java applying SOLID design principles under package \com.creatinaboys.model\.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Integrated abstract method signatures and polymorphism structures into the model package.

### 2. Repository & Custom Exception Design
* **Prompt/Query:** Creating file-based persistence handlers (\AthleteRepository\ in \com.creatinaboys.repository\) and custom exceptions.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Refactored exception handling to ensure clean exception propagation up to the service layer.

### 3. UML Diagram Specifications
* **Prompt/Query:** Syntax generation for Mermaid.js class diagrams, layer architecture, and inheritance hierarchies.
* **AI Tool Used:** Gemini.
* **Output Utilization:** Published diagrams into \docs/class-diagram.md\, \docs/hierarchy-diagram.md\, and \docs/layers-diagram.md\.

### 4. Model Review, Custom Exceptions and In-Memory Repository
* **Prompt/Query:** Review of the domain model against GRASP and SOLID, design of `AthleteNotFoundException` and `InvalidAgeException`, and an in-memory `AthleteRepository` that implements `IAthleteRepository`.
* **AI Tool Used:** Claude.
* **Output Utilization:** Reviewed the generated code, adapted it and tested it manually before committing to `feature/domain-persistence`.

### 5. Documentation Alignment
* **Prompt/Query:** Aligning the Phase 2 report, UML diagrams and class names (English) with the real source code.
* **AI Tool Used:** Claude.
* **Output Utilization:** Updated `docs/*.md` and the Phase 2 document.