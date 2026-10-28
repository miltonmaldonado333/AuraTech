# Hierarchy Diagram

```mermaid
classDiagram
    class Athlete {
        <<abstract>>
        +calculateFee(IBillingService)* void
        +getDiscipline()* String
    }
    class Footballer {
        -String playingPosition
    }
    class Basketballer {
        -double height
    }
    class Volleyballer {
        <<future extension>>
    }
    Athlete <|-- Footballer
    Athlete <|-- Basketballer
    Athlete <|-- Volleyballer

    class Exception
    class RuntimeException
    class AthleteNotFoundException
    class InvalidAgeException
    class PersistenceException
    Exception <|-- AthleteNotFoundException
    Exception <|-- InvalidAgeException
    Exception <|-- RuntimeException
    RuntimeException <|-- PersistenceException

    class IAthleteRepository {
        <<interface>>
    }
    class AthleteRepository
    class FileAthleteRepository
    IAthleteRepository <|.. AthleteRepository
    IAthleteRepository <|.. FileAthleteRepository

    class AthleteParser {
        <<interface>>
    }
    class FootballerParser
    class BasketballerParser
    class VolleyballerParser {
        <<future extension>>
    }
    AthleteParser <|.. FootballerParser
    AthleteParser <|.. BasketballerParser
    AthleteParser <|.. VolleyballerParser

    class IBillingService {
        <<interface>>
    }
    class BillingService
    IBillingService <|.. BillingService
```

`Volleyballer` and `VolleyballerParser` are not implemented: they only show how the hierarchy
and the persistence parsers can grow without modifying existing code (Open/Closed Principle).