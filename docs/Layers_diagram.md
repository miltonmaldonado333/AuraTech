# Layers Diagram

Layered architecture of the project (`com.creatinaboys`). Every top-level class, interface and enum is placed in the package where it is **declared**; inner classes are listed in `class-diagram.md`. The arrows are the dependencies found in the source code between packages.

```mermaid
flowchart TB
    MAIN["<b>com.creatinaboys (Entry Point)</b><br/>Main"]
    subgraph UI["com.creatinaboys.ui (View Layer)"]
        UIROOT["<b>ui (frames and login)</b><br/>AthleteConsultFrame, AthleteRegisterFrame, LoginFrame<br/>PrincipalFrame, PrincipalFrames, RegisterPanel"]
        UIPANELS["<b>ui.panels (screens)</b><br/>AthleteListPanel, AthleteRegisterPanel, CategoryPanel<br/>CoachFormDialog, CoachPanel, DisciplinePanel<br/>HomePanel, PaymentPanel, ReportsPanel"]
        UICOMP["<b>ui.components (reusable components)</b><br/>ModernComboBox, BrandHeader, CardPanel, ClubLogo<br/>CoachLabels, IconBadge, InputBox, LinkLabel<br/>ModernButton, ModernTable, Money, PlaceholderPasswordField<br/>PlaceholderTextField, SearchText, ShareBar, SidebarButton<br/>SpanishLabels, VectorIcon"]
        UITHEME["<b>ui.theme</b><br/>ThemeConstants"]
    end
    SVC["<b>com.creatinaboys.service (Service Layer)</b><br/>AthleteService, Authservice, BillingService, CoachService<br/>DisciplineService, HealthService, IActivationPolicy, IAuthService<br/>IBillingService, ICoachService, IDisciplineService, IHealthService<br/>ISaleService, MembershipExpirationPolicy, SaleService"]
    REPO["<b>com.creatinaboys.repository (Repository Layer)</b><br/>AthleteParser, AthleteParserRegistry, AthleteRepository, BasketballerParser<br/>CoachParser, FileAthleteRepository, FileCoachRepository, FileDisciplineRepository<br/>FileHealthRepository, FilePaymentRepository, FileStorage, Fileuserrepository<br/>FootballerParser, HealthRecordParser, IAthleteRepository, ICoachRepository<br/>IDisciplineRepository, IHealthRepository, IPaymentRepository, Iuserrepository<br/>PaymentParser, TextCodec"]
    MODEL["<b>com.creatinaboys.model (Model Layer)</b><br/>Athlete, Basketballer, CategoryEnum, Coach<br/>FitnessStatusEnum, Footballer, HealthRecord, InjuryRecord<br/>Payment, PaymentStatusEnum, UserAccount"]
    VAL["<b>com.creatinaboys.validation (cross-cutting)</b><br/>DataValidator"]
    EXC["<b>com.creatinaboys.exceptions (cross-cutting)</b><br/>AthleteNotFoundException, CoachNotFoundException, InvalidAgeException<br/>PersistenceException, ValidationException"]

    MAIN -->|creates and wires| UIROOT
    MAIN -->|creates and injects| SVC
    MAIN -->|creates| REPO
    UIROOT -->|opens| UIPANELS
    UIROOT -->|invokes| SVC
    UIPANELS -->|invokes| SVC
    UIPANELS -->|validates input| VAL
    UIPANELS -->|reads and builds entities| MODEL
    UIPANELS -->|uses| UICOMP
    UIROOT -->|uses| UICOMP
    UICOMP -->|labels| MODEL
    UIPANELS -->|uses| UITHEME
    UIROOT -->|uses| UITHEME
    UICOMP -->|uses| UITHEME
    SVC -->|persists / queries| REPO
    SVC -->|manipulates entities| MODEL
    REPO -->|stores| MODEL
    MODEL -.->|depends on abstraction| SVC
    UI -.->|catches| EXC
    SVC -.->|throws| EXC
    REPO -.->|throws| EXC
    MODEL -.->|throws| EXC
    VAL -.->|throws| EXC
    classDef view fill:#eff6ff,stroke:#2563eb,color:#0f172a
    classDef svc fill:#f0fdf4,stroke:#16a34a,color:#0f172a
    classDef repo fill:#fffbeb,stroke:#d97706,color:#0f172a
    classDef model fill:#faf5ff,stroke:#7c3aed,color:#0f172a
    classDef cross fill:#fef2f2,stroke:#dc2626,color:#0f172a
    class UIROOT,UIPANELS,UICOMP,UITHEME view
    class SVC svc
    class REPO repo
    class MODEL model
    class VAL,EXC cross
```

Each box lists every class, interface and enum declared in that package. Solid arrows are direct dependencies (creation, fields, calls); dashed arrows are dependencies on an abstraction or error propagation.

The dashed arrow from the model to the service layer is a dependency on the
`IBillingService` **interface** only (Dependency Inversion Principle):
`Athlete.calculateFee(IBillingService)` never knows the concrete pricing rules.

**Folder vs. package.** Three files are stored in a folder that does not match the package they declare, and the diagram uses the declared package: `RegisterPanel` is in `ui/panels/` but declares `com.creatinaboys.ui`; `ModernComboBox` is in `ui/Theme/` but declares `com.creatinaboys.ui.components`; `ThemeConstants` is in `ui/Theme/` and declares `com.creatinaboys.ui.theme`.

`SearchText` and `SpanishLabels` live in `ui.components`: `AthleteListPanel` and `CoachPanel` use them to normalize the search query
(no accents, no upper case) before filtering the lists returned by the service layer; the filtering never reaches the repository.