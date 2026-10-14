\\\mermaid
classDiagram
class Athlete {
<<abstract>> - String id - String fullName - int age - String phone - CategoryEnum category - double monthlyFee + calculateCategoryAndFee()\*
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
