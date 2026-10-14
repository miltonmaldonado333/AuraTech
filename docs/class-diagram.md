````mermaid
classDiagram
    namespace com.creatinaboys.model {
        class CategoryEnum {
            <<enumeration>>
            CHILDREN
            YOUTH
            ADULTS
        }

        class Athlete {
            <<abstract>>
            #String id
            #String fullName
            #int age
            #String phone
            #int jerseyNumber
            #double monthlyFee
            #CategoryEnum category
            +Athlete(id, fullName, age, phone, jerseyNumber)
            +getId() String
            +getFullName() String
            +getAge() int
            +getPhone() String
            +getJerseyNumber() int
            +getMonthlyFee() double
            +getCategory() CategoryEnum
            +calculateFee()* void
        }

        class Footballer {
            -String playingPosition
            +Footballer(id, fullName, age, phone, jerseyNumber, playingPosition)
            +getPlayingPosition() String
            +calculateFee() void
        }

        class Basketballer {
            -double height
            +Basketballer(id, fullName, age, phone, jerseyNumber, height)
            +getHeight() double
            +calculateFee() void
        }
    }

    namespace com.creatinaboys.repository {
        class AthleteRepository {
            -List~Athlete~ athleteList
            +register(Athlete a) boolean
            +find(String id) Athlete
            +findAll() List~Athlete~
            +delete(String id) boolean
            +update(Athlete a) boolean
        }
    }

    namespace com.creatinaboys.service {
        class BillingService {
            +calculateFee(int age) double
            +determineCategory(int age) CategoryEnum
        }

        class AthleteService {
            -AthleteRepository repository
            -BillingService billingService
            +AthleteService(AthleteRepository repo, BillingService billing)
            +registerFootballer(...) boolean
            +registerBasketballer(...) boolean
            +findAthlete(String id) Athlete
            +getAll() List~Athlete~
            +deleteAthlete(String id) boolean
        }
    }

    namespace com.creatinaboys.ui {
        class PrincipalFrame {
            +main(String[] args) void
            +openRegisterForm() void
            +openConsultForm() void
        }
        class AthleteRegisterFrame {
            -AthleteService service
            +btnSaveActionPerformed() void
            +clearFields() void
        }
        class AthleteConsultFrame {
            -AthleteService service
            +loadTable() void
            +btnSearchActionPerformed() void
            +btnDeleteActionPerformed() void
        }
    }

    %% Relationships
    Athlete <|-- Footballer : Inheritance
    Athlete <|-- Basketballer : Inheritance
    Athlete --> CategoryEnum : uses
    AthleteRepository "1" o-- "*" Athlete : contains
    AthleteService --> AthleteRepository : uses
    AthleteService --> BillingService : uses
    AthleteRegisterFrame --> AthleteService : invokes
    AthleteConsultFrame --> AthleteService : invokes
    PrincipalFrame --> AthleteRegisterFrame : opens
    PrincipalFrame --> AthleteConsultFrame : opens
    ```
````
