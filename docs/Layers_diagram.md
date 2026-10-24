# Layers Diagram

```mermaid
flowchart TB
    subgraph UI["com.creatinaboys.ui (View Layer)"]
        PrincipalFrame
        AthleteRegisterFrame
        AthleteConsultFrame
    end
    subgraph SVC["com.creatinaboys.service (Service Layer)"]
        AthleteService
        IBillingService
        BillingService
    end
    subgraph REPO["com.creatinaboys.repository (Repository Layer)"]
        IAthleteRepository
        AthleteRepository
        FileAthleteRepository
        AthleteParser
        AthleteParserRegistry
        FootballerParser
        BasketballerParser
    end
    subgraph MODEL["com.creatinaboys.model (Model Layer)"]
        Athlete
        Footballer
        Basketballer
        CategoryEnum
    end
    subgraph EXC["com.creatinaboys.exceptions (cross-cutting)"]
        AthleteNotFoundException
        InvalidAgeException
        PersistenceException
    end

    UI -->|invokes| SVC
    SVC -->|persists / queries| REPO
    SVC -->|manipulates entities| MODEL
    REPO -->|stores| MODEL
    MODEL -.->|depends on abstraction| SVC
    REPO -.->|throws| EXC
    SVC -.->|throws| EXC
```

The dashed arrow from the model to the service layer is a dependency on the
`IBillingService` **interface** only (Dependency Inversion Principle):
`Athlete.calculateFee(IBillingService)` never knows the concrete pricing rules.