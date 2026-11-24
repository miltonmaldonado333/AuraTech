# Class Diagram

Complete class diagram of the project (`com.creatinaboys`), updated against Aura-Tech.zip (November 2026): every principal class, interface, enum and inner class is included. Because of its size it is split by layer, so each diagram stays readable. The sections follow the architecture: model, repository, service and UI.

**How to read it**

| Notation | Meaning |
| :-- | :-- |
| `A <\|-- B` | `B` extends `A` |
| `A <\|.. B` | `B` implements interface `A` |
| `A --> B` | `A` has a field of type `B` (association) |
| `A ..> B : creates / throws / uses` | `A` instantiates, throws or uses `B` |
| `A *-- B : inner class` | `B` is declared inside `A`; it is written `A_B` |
| `+` `-` `#` `~` | public, private, protected, package-private |
| `$` / `*` | static / abstract |

Classes drawn without members are endpoints of a relationship: they are described in the section of their own layer. Swing parents (`JPanel`, `JFrame`, ...) are shown in `hierarchy-diagram.md`. In the UI sections, constants and Swing fields are omitted.

## 1. Model, exceptions and validation

Domain entities and enums (`model`), custom exceptions (`exceptions`) and input validation (`validation`).

```mermaid
classDiagram
    direction TB

    class DuplicateDataException {
        -DuplicateDataException_Field field
        +DuplicateDataException(DuplicateDataException_Field, String)
        +getField() DuplicateDataException_Field
    }
    class AgeCategory {
        <<record>>
        -String name
        -BigDecimal monthlyFee
        -List~Integer~ ages
        +AgeCategory(String, BigDecimal, List~Integer~)
        +toString() String
        +hashCode() int
        +equals(Object) boolean
        +name() String
        +monthlyFee() BigDecimal
        +ages() List~Integer~
    }
    class AthleteKey {
        <<record>>
        -String id
        -String discipline
        +AthleteKey(String, String)
        +toString() String
        +hashCode() int
        +equals(Object) boolean
        +id() String
        +discipline() String
    }
    class GeneralAthlete {
        -String discipline
        +GeneralAthlete(String, String, int, String, int, String)
        +getDiscipline() String
        +calculateFee(IBillingService) void
    }
    class AthleteNotFoundException {
        -long serialVersionUID$
        -String searchedId
        +AthleteNotFoundException(String)
        +getSearchedId() String
    }
    class CoachNotFoundException {
        -long serialVersionUID$
        -String searchedId
        +CoachNotFoundException(String)
        +getSearchedId() String
    }
    class InvalidAgeException {
        -long serialVersionUID$
        -int enteredAge
        +InvalidAgeException(int)
        +getEnteredAge() int
    }
    class PersistenceException {
        -long serialVersionUID$
        +PersistenceException(String)
        +PersistenceException(String, Throwable)
    }
    class ValidationException {
        -long serialVersionUID$
        +ValidationException(String)
        +ValidationException(String, Throwable)
    }
    class Athlete {
        <<abstract>>
        +int MAX_AGE$
        -String id
        -String fullName
        -int age
        -String phone
        -int jerseyNumber
        -double monthlyFee
        -String categoryName
        -boolean active
        -boolean manualOverride
        -LocalDate paidUntil
        #Athlete(String, String, int, String, int)
        +calculateFee(IBillingService) void
        +getDiscipline() String
        #requireValidAge(int)$ int
        #requireText(String, String)$ String
        +getId() String
        +setId(String) void
        +getFullName() String
        +setFullName(String) void
        +getAge() int
        +setAge(int) void
        +getPhone() String
        +setPhone(String) void
        +getJerseyNumber() int
        +setJerseyNumber(int) void
        +getMonthlyFee() double
        +setMonthlyFee(double) void
        +getCategory() CategoryEnum
        +setCategory(CategoryEnum) void
        +getCategoryName() String
        +setCategoryName(String) void
        +isActive() boolean
        +setActive(boolean) void
        +isManualOverride() boolean
        +setActiveManually(boolean) void
        +clearManualOverride() void
        +getPaidUntil() LocalDate
        +setPaidUntil(LocalDate) void
    }
    class Basketballer {
        -double height
        +Basketballer(String, String, int, String, int, double)
        -requireValidHeight(double)$ double
        +calculateFee(IBillingService) void
        +getDiscipline() String
        +getHeight() double
        +setHeight(double) void
    }
    class CategoryEnum {
        <<enumeration>>
        CHILDREN
        YOUTH
        ADULTS
        -String name
        -CategoryEnum(String)
        +getName() String
        +toString() String
    }
    class Coach {
        -String id
        -String fullName
        -String phone
        -String discipline
        -CategoryEnum category
        -boolean active
        +Coach(String, String, String, String, CategoryEnum)
        +Coach(String, String, String, String, CategoryEnum, boolean)
        +getId() String
        +getFullName() String
        +setFullName(String) void
        +getPhone() String
        +setPhone(String) void
        +getDiscipline() String
        +setDiscipline(String) void
        +getCategory() CategoryEnum
        +setCategory(CategoryEnum) void
        +isActive() boolean
        +setActive(boolean) void
        +equals(Object) boolean
        +hashCode() int
        +toString() String
    }
    class FitnessStatusEnum {
        <<enumeration>>
        FIT
        FIT_WITH_RESTRICTIONS
        UNFIT
        -String description
        -FitnessStatusEnum(String)
        +getDescription() String
    }
    class Footballer {
        -String playingPosition
        +Footballer(String, String, int, String, int, String)
        +calculateFee(IBillingService) void
        +getDiscipline() String
        +getPlayingPosition() String
        +setPlayingPosition(String) void
    }
    class HealthRecord {
        -String athleteId
        -FitnessStatusEnum fitnessStatus
        -LocalDate assessmentDate
        -String restrictions
        -List~InjuryRecord~ injuries
        +HealthRecord(String, FitnessStatusEnum, LocalDate, String)
        +addInjury(InjuryRecord) void
        +findInjury(String) Optional~InjuryRecord~
        +removeInjury(String) boolean
        +updateFitnessStatus(FitnessStatusEnum, String, LocalDate) boolean
        +getAthleteId() String
        +getFitnessStatus() FitnessStatusEnum
        +setFitnessStatus(FitnessStatusEnum) void
        +getAssessmentDate() LocalDate
        +setAssessmentDate(LocalDate) void
        +getRestrictions() String
        +setRestrictions(String) void
        +getInjuries() List~InjuryRecord~
    }
    class InjuryRecord {
        -String DEFAULT_STATUS$
        -String id
        -String description
        -LocalDate date
        -String status
        -String observations
        +InjuryRecord(String, String, LocalDate, String, String)
        +getId() String
        +getDescription() String
        +setDescription(String) void
        +getDate() LocalDate
        +setDate(LocalDate) void
        +getStatus() String
        +setStatus(String) void
        +getObservations() String
        +setObservations(String) void
        +equals(Object) boolean
        +hashCode() int
    }
    class Payment {
        -String athleteId
        -double amount
        -LocalDate paymentDate
        -YearMonth period
        -String discipline
        +Payment(String, double, LocalDate, YearMonth, String)
        +Payment(String, double, LocalDate, YearMonth)
        +getAthleteId() String
        +getAmount() double
        +getPaymentDate() LocalDate
        +getPeriod() YearMonth
        +getDiscipline() String
        +belongsTo(AthleteKey) boolean
        +toString() String
    }
    class PaymentStatusEnum {
        <<enumeration>>
        PAID
        PARTIAL
        PENDING
        -double EPSILON$
        -String label
        -PaymentStatusEnum(String)
        +getLabel() String
        +of(double, double)$ PaymentStatusEnum
        +toString() String
    }
    class UserAccount {
        -String username
        -String salt
        -String passwordHash
        +UserAccount(String, String, String)
        +getUsername() String
        +getSalt() String
        +getPasswordHash() String
    }
    class DataValidator {
        +int MIN_AGE$
        +int MAX_AGE$
        +int MAX_JERSEY_NUMBER$
        +double MAX_HEIGHT_METERS$
        +double MIN_HEIGHT_METERS$
        -DataValidator()
        +validateRequiredString(String, String)$ void
        +validateId(String)$ void
        +validateFullName(String)$ void
        +validatePhone(String)$ void
        +validateRequiredPhone(String)$ void
        +parsePositiveInt(String, String)$ int
        +parseIntInRange(String, String, int, int)$ int
        +parseAge(String)$ int
        +parseJerseyNumber(String)$ int
        +parsePositiveDouble(String, String)$ double
        +parseHeight(String)$ double
    }

    Athlete <|-- Basketballer
    Athlete <|-- Footballer
    Athlete --> CategoryEnum
    Coach --> CategoryEnum
    HealthRecord --> FitnessStatusEnum
    HealthRecord --> InjuryRecord
    DataValidator ..> ValidationException : creates
    Athlete ..> InvalidAgeException : throws
    Basketballer ..> InvalidAgeException : throws
    Footballer ..> InvalidAgeException : throws
    Athlete ..> IBillingService : uses
    Basketballer ..> IBillingService : uses
    Footballer ..> IBillingService : uses
    Athlete <|-- GeneralAthlete
    ValidationException <|-- DuplicateDataException
    Athlete ..> AthleteKey : creates identity
    Athlete ..> AgeCategory : category fee
    GeneralAthlete ..> IBillingService : calculates fee
    GeneralAthlete ..> AgeCategory : uses
    AgeCategory ..> BigDecimal : fee
    Payment ..> AthleteKey : belongs to registration
```

## 2. Repository layer

Repository interfaces, file-based implementations, parsers and the text helpers (`repository`).

```mermaid
classDiagram
    direction TB

    class ICategoryRepository {
        <<interface>>
        +findAll() List~AgeCategory~
        +create(String, BigDecimal, int, int) boolean
    }
    class PostgresAthleteRepository {
        -String CONSULTA$
        +PostgresAthleteRepository()
        +save(Athlete) boolean
        +findByKey(AthleteKey) Athlete
        +findById(String) List~Athlete~
        +findAll() List~Athlete~
        +deleteByKey(AthleteKey) boolean
        +update(Athlete) boolean
        -guardarPersona(Connection, Athlete) void
        -datosInscripcion(PreparedStatement, Athlete) void
        -asignarDatosDeporte(PreparedStatement, Athlete, int, int) void
        -convertir(ResultSet) Athlete
    }
    class PostgresCategoryRepository {
        -String SELECT_CATEGORIES$
        -String CREATE_CATEGORY$
        -String ASSIGN_AGES$
        +PostgresCategoryRepository()
        +findAll() List~AgeCategory~
        +create(String, BigDecimal, int, int) boolean
        -catalogError(String, SQLException) PersistenceException
    }
    class PostgresDisciplineRepository {
        +PostgresDisciplineRepository()
        +findAll() List~String~
        +add(String) boolean
        -catalogError(String, SQLException) PersistenceException
    }
    class AthleteParser {
        <<interface>>
        +getType() String
        +toTextLine(Athlete) String
        +fromTextFields(String[]) Athlete
    }
    class AthleteParserRegistry {
        -Map~String, AthleteParser~ readerMap
        -Map~Class~Object~, AthleteParser~ writerMap
        +AthleteParserRegistry()
        +register(Class~Object~, AthleteParser) AthleteParserRegistry
        +toTextLine(Athlete) String
        +fromTextLine(String, String[]) Athlete
        -findWriter(Class~Object~) AthleteParser
    }
    class AthleteRepository {
        -List~Athlete~ athleteList
        +AthleteRepository()
        +save(Athlete) boolean
        +findByKey(AthleteKey) Athlete
        +findById(String) List~Athlete~
        +findAll() List~Athlete~
        +deleteByKey(AthleteKey) boolean
        +update(Athlete) boolean
        -indexOf(AthleteKey) int
    }
    class BasketballerParser {
        -String TYPE$
        -int FIELD_COUNT$
        +BasketballerParser()
        +getType() String
        +toTextLine(Athlete) String
        +fromTextFields(String[]) Athlete
    }
    class CoachParser {
        -String TYPE$
        -int FIELD_COUNT$
        +CoachParser()
        +toTextLine(Coach) String
        +fromTextFields(String[]) Coach
    }
    class FileAthleteRepository {
        -String DEFAULT_FILE_PATH$
        -String EXTRAS_MARK$
        -Path path
        -AthleteParserRegistry parsers
        +FileAthleteRepository()
        +FileAthleteRepository(String)
        +FileAthleteRepository(String, AthleteParserRegistry)
        -defaultParsers()$ AthleteParserRegistry
        +save(Athlete) boolean
        +findByKey(AthleteKey) Athlete
        +findById(String) List~Athlete~
        +findAll() List~Athlete~
        +deleteByKey(AthleteKey) boolean
        +update(Athlete) boolean
        -saveAll(List~Athlete~) void
        -toLine(Athlete) String
        -parseLine(String) Athlete
        -applyExtras(Athlete, String) void
    }
    class FileCoachRepository {
        -String DEFAULT_FILE_PATH$
        -Path path
        -CoachParser parser
        +FileCoachRepository()
        +FileCoachRepository(String)
        +save(Coach) boolean
        +findById(String) Optional~Coach~
        +findAll() List~Coach~
        +findByDisciplineAndCategory(String, CategoryEnum) List~Coach~
        +deleteById(String) boolean
        +update(Coach) boolean
        -saveAll(List~Coach~) void
    }
    class FileDisciplineRepository {
        -String DEFAULT_FILE_PATH$
        -Path path
        +FileDisciplineRepository()
        +FileDisciplineRepository(String)
        +findAll() List~String~
        +add(String) boolean
        +saveAll(List~String~) void
    }
    class FileHealthRepository {
        -String DEFAULT_FILE_PATH$
        -Path path
        -HealthRecordParser parser
        +FileHealthRepository()
        +FileHealthRepository(String)
        +save(HealthRecord) boolean
        +findByAthleteId(String) Optional~HealthRecord~
        +findAll() List~HealthRecord~
        +deleteByAthleteId(String) boolean
        +update(HealthRecord) boolean
        -saveAll(List~HealthRecord~) void
    }
    class FilePaymentRepository {
        -String DEFAULT_FILE_PATH$
        -Path path
        -PaymentParser parser
        +FilePaymentRepository()
        +FilePaymentRepository(String)
        +save(Payment) boolean
        +replaceAll(List~Payment~) void
        +findAll() List~Payment~
        +findByAthleteId(String) List~Payment~
    }
    class FileStorage {
        -Charset LEGACY_CHARSET$
        -FileStorage()
        ~readLines(Path)$ List~String~
        ~writeLines(Path, List~String~)$ void
        -backupOf(Path)$ Path
    }
    class Fileuserrepository {
        -String DEFAULT_FILE_PATH$
        -Path path
        +Fileuserrepository()
        +Fileuserrepository(String)
        +save(UserAccount) boolean
        +findByUsername(String) Optional~UserAccount~
        +findAll() List~UserAccount~
    }
    class FootballerParser {
        -String TYPE$
        -int FIELD_COUNT$
        +FootballerParser()
        +getType() String
        +toTextLine(Athlete) String
        +fromTextFields(String[]) Athlete
    }
    class HealthRecordParser {
        -String TYPE$
        -int FIELD_COUNT$
        +HealthRecordParser()
        +toTextLine(HealthRecord) String
        +fromTextFields(String[]) HealthRecord
    }
    class IAthleteRepository {
        <<interface>>
        +save(Athlete) boolean
        +findByKey(AthleteKey) Athlete
        +findById(String) List~Athlete~
        +findAll() List~Athlete~
        +deleteByKey(AthleteKey) boolean
        +update(Athlete) boolean
    }
    class ICoachRepository {
        <<interface>>
        +save(Coach) boolean
        +findById(String) Optional~Coach~
        +findAll() List~Coach~
        +findByDisciplineAndCategory(String, CategoryEnum) List~Coach~
        +deleteById(String) boolean
        +update(Coach) boolean
    }
    class IDisciplineRepository {
        <<interface>>
        +findAll() List~String~
        +add(String) boolean
    }
    class IHealthRepository {
        <<interface>>
        +save(HealthRecord) boolean
        +findByAthleteId(String) Optional~HealthRecord~
        +findAll() List~HealthRecord~
        +deleteByAthleteId(String) boolean
        +update(HealthRecord) boolean
    }
    class IPaymentRepository {
        <<interface>>
        +save(Payment) boolean
        +findAll() List~Payment~
        +findByAthleteId(String) List~Payment~
        +replaceAll(List~Payment~) void
        +findByAthleteKey(AthleteKey) List~Payment~
    }
    class Iuserrepository {
        <<interface>>
        +save(UserAccount) boolean
        +findByUsername(String) Optional~UserAccount~
        +findAll() List~UserAccount~
    }
    class PaymentParser {
        -String TYPE$
        -int LEGACY_FIELD_COUNT$
        -int FIELD_COUNT$
        +PaymentParser()
        +toTextLine(Payment) String
        +fromTextFields(String[]) Payment
    }
    class TextCodec {
        ~String LEGACY_EMPTY$
        -String SPECIAL$
        -TextCodec()
        ~escape(String)$ String
        ~escapeOptional(String)$ String
        ~unescape(String)$ String
        ~unescapeOptional(String)$ String
        ~split(String, char)$ String[]
        ~requireFields(String[], int, String)$ void
    }

    IAthleteRepository <|.. AthleteRepository
    AthleteParser <|.. BasketballerParser
    IAthleteRepository <|.. FileAthleteRepository
    ICoachRepository <|.. FileCoachRepository
    IDisciplineRepository <|.. FileDisciplineRepository
    IHealthRepository <|.. FileHealthRepository
    IPaymentRepository <|.. FilePaymentRepository
    Iuserrepository <|.. Fileuserrepository
    AthleteParser <|.. FootballerParser
    AthleteParserRegistry --> AthleteParser
    AthleteRepository --> Athlete
    FileAthleteRepository --> AthleteParserRegistry
    FileCoachRepository --> CoachParser
    FileHealthRepository --> HealthRecordParser
    FilePaymentRepository --> PaymentParser
    AthleteParserRegistry ..> PersistenceException : creates
    AthleteRepository ..> AthleteNotFoundException : creates
    BasketballerParser ..> PersistenceException : creates
    BasketballerParser ..> Basketballer : creates
    CoachParser ..> PersistenceException : creates
    CoachParser ..> Coach : creates
    FileAthleteRepository ..> AthleteNotFoundException : creates
    FileAthleteRepository ..> BasketballerParser : creates
    FileAthleteRepository ..> FootballerParser : creates
    FileStorage ..> PersistenceException : creates
    Fileuserrepository ..> UserAccount : creates
    FootballerParser ..> PersistenceException : creates
    FootballerParser ..> Footballer : creates
    HealthRecordParser ..> PersistenceException : creates
    HealthRecordParser ..> HealthRecord : creates
    HealthRecordParser ..> InjuryRecord : creates
    PaymentParser ..> PersistenceException : creates
    PaymentParser ..> Payment : creates
    TextCodec ..> PersistenceException : creates
    FileCoachRepository ..> PersistenceException : throws
    FileDisciplineRepository ..> PersistenceException : throws
    FileHealthRepository ..> PersistenceException : throws
    FilePaymentRepository ..> PersistenceException : throws
    Fileuserrepository ..> PersistenceException : throws
    IAthleteRepository ..> AthleteNotFoundException : throws
    ICoachRepository ..> PersistenceException : throws
    IDisciplineRepository ..> PersistenceException : throws
    IHealthRepository ..> PersistenceException : throws
    IPaymentRepository ..> PersistenceException : throws
    Iuserrepository ..> PersistenceException : throws
    BasketballerParser ..> TextCodec : uses
    CoachParser ..> TextCodec : uses
    FileAthleteRepository ..> FileStorage : uses
    FileAthleteRepository ..> TextCodec : uses
    FileCoachRepository ..> FileStorage : uses
    FileCoachRepository ..> TextCodec : uses
    FileDisciplineRepository ..> FileStorage : uses
    FileHealthRepository ..> FileStorage : uses
    FileHealthRepository ..> TextCodec : uses
    FilePaymentRepository ..> FileStorage : uses
    FilePaymentRepository ..> TextCodec : uses
    Fileuserrepository ..> FileStorage : uses
    Fileuserrepository ..> TextCodec : uses
    FootballerParser ..> TextCodec : uses
    HealthRecordParser ..> TextCodec : uses
    PaymentParser ..> TextCodec : uses
    IAthleteRepository <|.. PostgresAthleteRepository
    ICategoryRepository <|.. PostgresCategoryRepository
    IDisciplineRepository <|.. PostgresDisciplineRepository
    PostgresAthleteRepository --> Athlete
    PostgresAthleteRepository ..> AthleteKey : lookup
    PostgresAthleteRepository ..> conection : JDBC connection
    PostgresCategoryRepository --> AgeCategory
    PostgresCategoryRepository ..> conection : JDBC connection
    PostgresDisciplineRepository ..> conection : JDBC connection
```

## 3. Service layer

Business services, their interfaces and the activation policy (`service`).

```mermaid
classDiagram
    direction TB

    class AthleteUniquenessPolicy {
        +AthleteUniquenessPolicy()
        +check(Athlete, List~Athlete~, AthleteKey) void
    }
    class CategoryService {
        -BigDecimal MAX_FEE$
        -ICategoryRepository repository
        +CategoryService(ICategoryRepository)
        +getCategories() List~AgeCategory~
        +createCategory(String, int, int, BigDecimal) void
    }
    class DashboardService {
        -double EPSILON$
        -IAthleteRepository athleteRepository
        -IPaymentRepository paymentRepository
        -ICoachService coachService
        -Clock clock
        -CategoryService categoryService
        +DashboardService(IAthleteRepository, IPaymentRepository, ICoachService)
        +DashboardService(IAthleteRepository, IPaymentRepository, ICoachService, CategoryService)
        +DashboardService(IAthleteRepository, IPaymentRepository, ICoachService, Clock)
        +DashboardService(IAthleteRepository, IPaymentRepository, ICoachService, CategoryService, Clock)
        +loadDashboard() DashboardSnapshot
        -calculateMonthlyPayments(List~Athlete~, List~Payment~, YearMonth) Map~AthleteKey, Double~
    }
    class DashboardSnapshot {
        <<record>>
        -List~Athlete~ athletes
        -long active
        -int coaches
        -Map~AthleteKey, Double~ paidMap
        -double collected
        -double expected
        -List~Athlete~ pending
        -List~Payment~ history
        -Map~String, String~ names
        -List~String~ categories
        +DashboardSnapshot(List~Athlete~, long, int, Map~AthleteKey, Double~, double, double, List~Athlete~, List~Payment~, Map~String, String~, List~String~)
        +toString() String
        +hashCode() int
        +equals(Object) boolean
        +athletes() List~Athlete~
        +active() long
        +coaches() int
        +paidMap() Map~AthleteKey, Double~
        +collected() double
        +expected() double
        +pending() List~Athlete~
        +history() List~Payment~
        +names() Map~String, String~
        +categories() List~String~
    }
    class IAthlteService {
        <<interface>>
        +registerAthlete(Athlete) boolean
        +registerAthlete(Athlete, String) boolean
        +categoryOptionsForAge(int) List~AgeCategory~
        +updateAthleteData(AthleteKey, String, int, String, int, String, String) boolean
        +findAthlete(AthleteKey) Athlete
        +findAthletesById(String) List~Athlete~
        +getAllAthletes() List~Athlete~
        +existsByKey(AthleteKey) boolean
        +getTotalAthletesCount() int
        +getAthletesCountByType(Class~T~) long
        +updateAthleteData(AthleteKey, String, int, String, int, String) boolean
        +categoryForAge(int) CategoryEnum
        +feeForAge(int) double
        +deleteAthlete(AthleteKey) boolean
        +toggleAthleteStatus(AthleteKey) boolean
        +setAthleteActive(AthleteKey, boolean) boolean
        +renewMembership(AthleteKey, int) boolean
        +applyActivationPolicy(IActivationPolicy) void
    }
    class IDashBoardService {
        <<interface>>
        +loadDashboard() DashboardSnapshot
    }
    class PaymentDisciplineMigration {
        -IPaymentRepository paymentRepository
        -IAthleteRepository athleteRepository
        +PaymentDisciplineMigration(IPaymentRepository, IAthleteRepository)
        +run() int
        -groupAthletesById(List~Athlete~) Map~String, List~Athlete~~
    }
    class AthleteService {
        -IAthleteRepository repository
        -IBillingService billingService
        -AthleteUniquenessPolicy uniqueness
        +AthleteService(IAthleteRepository, IBillingService)
        +registerAthlete(Athlete) boolean
        +registerAthlete(Athlete, String) boolean
        +updateAthleteData(AthleteKey, String, int, String, int, String) boolean
        +updateAthleteData(AthleteKey, String, int, String, int, String, String) boolean
        +categoryOptionsForAge(int) List~AgeCategory~
        -resolveCategory(int, String) AgeCategory
        +categoryForAge(int) CategoryEnum
        +feeForAge(int) double
        +findAthlete(AthleteKey) Athlete
        +findAthletesById(String) List~Athlete~
        +getAllAthletes() List~Athlete~
        +existsByKey(AthleteKey) boolean
        +deleteAthlete(AthleteKey) boolean
        +getTotalAthletesCount() int
        +getAthletesCountByType(Class~T~) long
        +applyActivationPolicy(IActivationPolicy) void
        +toggleAthleteStatus(AthleteKey) boolean
        +setAthleteActive(AthleteKey, boolean) boolean
        +renewMembership(AthleteKey, int) boolean
        -persist(Athlete) void
    }
    class Authservice {
        +String DEFAULT_USER$
        +String DEFAULT_PASSWORD$
        -int ITERATIONS$
        -int KEY_BITS$
        -int SALT_BYTES$
        -int USERNAME_MIN$
        -int USERNAME_MAX$
        -int PASSWORD_MIN$
        -Iuserrepository repository
        -SecureRandom random
        +Authservice(Iuserrepository)
        +ensureDefaultUser() void
        +authenticate(String, char[]) boolean
        +register(String, char[], char[]) void
        -hasLetterAndDigit(char[])$ boolean
        -createAccount(String, char[]) UserAccount
        -hash(char[], byte[])$ byte[]
    }
    class BillingService {
        -CategoryService categoryService
        +BillingService(CategoryService)
        +determineEnrollmentCategory(int) AgeCategory
        +findEnrollmentCategories(int) List~AgeCategory~
        +determineCategory(int) CategoryEnum
        +calculateFee(int) double
    }
    class CoachService {
        -ICoachRepository coachRepository
        +CoachService(ICoachRepository)
        +registerCoach(Coach) boolean
        +assignCoachToCategory(String, String, CategoryEnum) boolean
        +getAllCoaches() List~Coach~
        +findCoachById(String) Optional~Coach~
        +getCoachesByDiscipline(String) List~Coach~
        +getCoachesByCategory(CategoryEnum) List~Coach~
        +updateCoach(Coach) boolean
        +deleteCoach(String) boolean
        +getCoachesCountByDiscipline() Map~String, Long~
        +getTotalCoachesCount() int
        +toggleCoachStatus(String) boolean
    }
    class DisciplineService {
        -List~String~ DEFAULTS$
        -Map~String, String~ ALIASES$
        -List~String~ disciplines
        -IDisciplineRepository repository
        +DisciplineService()
        +DisciplineService(List~String~)
        +DisciplineService(IDisciplineRepository)
        -load(List~String~) void
        +getAvailableDisciplines() List~String~
        +registerDiscipline(String) boolean
        +isDisciplineAvailable(String) boolean
        +findDisciplineByName(String) Optional~String~
        +getTotalDisciplinesCount() int
        -indexOf(String) int
        -canonical(String)$ String
    }
    class HealthService {
        -IHealthRepository healthRepository
        -IAthleteRepository athleteRepository
        +HealthService(IHealthRepository, IAthleteRepository)
        +saveOrUpdateHealthRecord(HealthRecord) boolean
        +getHealthRecordByAthleteId(String) Optional~HealthRecord~
        +addInjuryToAthlete(String, InjuryRecord) boolean
        +updateInjury(String, String, String, String, String) boolean
        +removeInjury(String, String) boolean
        +updateFitnessStatus(String, FitnessStatusEnum, String, LocalDate) boolean
        +isAthleteFitToTrain(String) boolean
        +getAllHealthRecords() List~HealthRecord~
        -requireAthleteId(String)$ void
    }
    class IActivationPolicy {
        <<interface>>
        +shouldBeActive(Athlete) boolean
    }
    class IAuthService {
        <<interface>>
        +authenticate(String, char[]) boolean
        +ensureDefaultUser() void
        +register(String, char[], char[]) void
    }
    class IBillingService {
        <<interface>>
        +calculateFee(int) double
        +determineCategory(int) CategoryEnum
        +determineEnrollmentCategory(int) AgeCategory
        +findEnrollmentCategories(int) List~AgeCategory~
    }
    class ICoachService {
        <<interface>>
        +registerCoach(Coach) boolean
        +assignCoachToCategory(String, String, CategoryEnum) boolean
        +getAllCoaches() List~Coach~
        +findCoachById(String) Optional~Coach~
        +getCoachesByDiscipline(String) List~Coach~
        +getCoachesByCategory(CategoryEnum) List~Coach~
        +updateCoach(Coach) boolean
        +deleteCoach(String) boolean
        +getCoachesCountByDiscipline() Map~String, Long~
        +getTotalCoachesCount() int
        +toggleCoachStatus(String) boolean
    }
    class IDisciplineService {
        <<interface>>
        +getAvailableDisciplines() List~String~
        +registerDiscipline(String) boolean
        +isDisciplineAvailable(String) boolean
        +findDisciplineByName(String) Optional~String~
        +getTotalDisciplinesCount() int
    }
    class IHealthService {
        <<interface>>
        +saveOrUpdateHealthRecord(HealthRecord) boolean
        +getHealthRecordByAthleteId(String) Optional~HealthRecord~
        +addInjuryToAthlete(String, InjuryRecord) boolean
        +updateInjury(String, String, String, String, String) boolean
        +removeInjury(String, String) boolean
        +updateFitnessStatus(String, FitnessStatusEnum, String, LocalDate) boolean
        +isAthleteFitToTrain(String) boolean
        +getAllHealthRecords() List~HealthRecord~
    }
    class ISaleService {
        <<interface>>
        +processMonthlyPayment(AthleteKey, double) boolean
        +getPendingBalance(AthleteKey) double
        +calculateEstimatedMonthlyRevenue() double
        +calculateTotalCollectedRevenue() double
        +getRevenueByDiscipline() Map~String, Double~
        +getRevenueByCategory() Map~CategoryEnum, Double~
        +getRevenueByCategoryName() Map~String, Double~
        +getAthletesWithPendingPayment() List~Athlete~
        +getPaidThisMonthByAthlete() Map~AthleteKey, Double~
        +getPaymentHistory() List~Payment~
    }
    class MembershipExpirationPolicy {
        -int graceDays
        +MembershipExpirationPolicy(int)
        +shouldBeActive(Athlete) boolean
    }
    class SaleService {
        -double EPSILON$
        -IAthleteRepository athleteRepository
        -IPaymentRepository paymentRepository
        -Clock clock
        +SaleService(IAthleteRepository, IBillingService)
        +SaleService(IAthleteRepository, IBillingService, IPaymentRepository)
        +SaleService(IAthleteRepository, IBillingService, IPaymentRepository, Clock)
        +processMonthlyPayment(AthleteKey, double) boolean
        +getPendingBalance(AthleteKey) double
        +calculateEstimatedMonthlyRevenue() double
        +calculateTotalCollectedRevenue() double
        +getRevenueByDiscipline() Map~String, Double~
        +getRevenueByCategory() Map~CategoryEnum, Double~
        +getRevenueByCategoryName() Map~String, Double~
        +getAthletesWithPendingPayment() List~Athlete~
        +getPaidThisMonthByAthlete() Map~AthleteKey, Double~
        +getPaymentHistory() List~Payment~
        +getAthletesByDiscipline(String) List~Athlete~
        +getAthletesByCategory(CategoryEnum) List~Athlete~
        -currentPeriod() YearMonth
        -paidThisMonth() Map~AthleteKey, Double~
    }

    IAuthService <|.. Authservice
    IBillingService <|.. BillingService
    ICoachService <|.. CoachService
    IDisciplineService <|.. DisciplineService
    IHealthService <|.. HealthService
    IActivationPolicy <|.. MembershipExpirationPolicy
    ISaleService <|.. SaleService
    AthleteService --> IAthleteRepository
    AthleteService --> IBillingService
    Authservice --> Iuserrepository
    CoachService --> ICoachRepository
    DisciplineService --> IDisciplineRepository
    HealthService --> IAthleteRepository
    HealthService --> IHealthRepository
    SaleService --> IAthleteRepository
    SaleService --> IPaymentRepository
    Authservice ..> ValidationException : creates
    Authservice ..> UserAccount : creates
    BillingService ..> InvalidAgeException : creates
    CoachService ..> CoachNotFoundException : creates
    HealthService ..> HealthRecord : creates
    SaleService ..> Payment : creates
    SaleService ..> FilePaymentRepository : creates
    AthleteService ..> AthleteNotFoundException : throws
    AthleteService ..> InvalidAgeException : throws
    Authservice ..> PersistenceException : throws
    CoachService ..> PersistenceException : throws
    DisciplineService ..> PersistenceException : throws
    HealthService ..> AthleteNotFoundException : throws
    HealthService ..> PersistenceException : throws
    IAuthService ..> PersistenceException : throws
    IAuthService ..> ValidationException : throws
    IBillingService ..> InvalidAgeException : throws
    ICoachService ..> CoachNotFoundException : throws
    ICoachService ..> PersistenceException : throws
    IDisciplineService ..> PersistenceException : throws
    IHealthService ..> AthleteNotFoundException : throws
    IHealthService ..> PersistenceException : throws
    ISaleService ..> AthleteNotFoundException : throws
    SaleService ..> AthleteNotFoundException : throws
    AthleteService ..> IActivationPolicy : uses
    SaleService ..> IBillingService : uses
    IAthlteService <|.. AthleteService
    IDashBoardService <|.. DashboardService
    AthleteService --> AthleteUniquenessPolicy
    AthleteService ..> AgeCategory : selects category
    CategoryService --> ICategoryRepository
    CategoryService ..> AgeCategory : manages
    BillingService --> ICategoryRepository
    BillingService ..> AgeCategory : calculates fee
    DashboardService --> IAthleteRepository
    DashboardService --> ICoachRepository
    DashboardService --> ISaleService
    DashboardService ..> DashboardSnapshot : creates
    PaymentDisciplineMigration --> IPaymentRepository
    PaymentDisciplineMigration --> IAthleteRepository
    AthleteUniquenessPolicy ..> DuplicateDataException : throws
```

## 4. UI: frames, login and theme

Windows and the login/register flow (`ui`, `ui.theme`). `RegisterPanel` is declared in package `ui` although its file is in `ui/panels/`.

```mermaid
classDiagram
    direction TB


    class AthleteConsultFrame {
        -AthleteService service
        +AthleteConsultFrame(AthleteService)
        +loadTable() void
    }
    class AthleteRegisterFrame {
        -AthleteService service
        +AthleteRegisterFrame(AthleteService)
        +clearFields() void
    }
    class LoginFrame {
        -int MAX_ATTEMPTS$
        -int LOCK_SECONDS$
        -IAuthService authService
        -function.Consumer~String~ onSuccess
        -PlaceholderTextField txtUser
        -PlaceholderPasswordField txtPassword
        +LoginFrame(IAuthService, function.Consumer~String~)
        -buildBrandSide() JPanel
        -buildFormSide() JPanel
        -showRegisterCard() void
        -showLoginCard(String, String) void
        -attemptLogin() void
        -onFailedAttempt() void
        -startLock() void
        -setBusy(boolean) void
        -showError(String) void
        -showInfo(String) void
    }
    class PrincipalFrame {
        +PrincipalFrame()
        +openRegisterForm() void
        +openConsultForm() void
    }
    class PrincipalFrames {
        -AthleteService athleteService
        -ISaleService saleService
        -CategoryService categoryService
        -IDisciplineService disciplineService
        -ICoachService coachService
        -IHealthService healthService
        +PrincipalFrames(AthleteService, ISaleService, CategoryService, IDisciplineService, ICoachService, IHealthService, IDashBoardService)
        +setOnLogout(Runnable) void
        +setCurrentUser(String) void
        -navigateTo(String) void
        -initFrame() void
        -initSidebar() void
        -initMainContent() void
        -ensurePanel(String) boolean
        -refreshPanel(String) void
        -selectSidebarButton(SidebarButton) void
    }
    class ThemeConstants {
        +Color SIDEBAR_BG$
        +Color SIDEBAR_HOVER$
        +Color SIDEBAR_ACTIVE$
        +Color SIDEBAR_TEXT$
        +Color PRIMARY_BLUE$
        +Color PRIMARY_HOVER$
        +ThemeConstants()
    }
    class RegisterPanel {
        -IAuthService authService
        -function.Consumer~String~ onRegistered
        -Runnable onBack
        -PlaceholderTextField txtUser
        -PlaceholderPasswordField txtPassword
        -PlaceholderPasswordField txtConfirm
        +RegisterPanel(IAuthService, function.Consumer~String~, Runnable)
        +getDefaultButton() JButton
        +reset() void
        -attemptRegister() void
        -setBusy(boolean) void
    }

    AthleteConsultFrame --> AthleteService
    AthleteRegisterFrame --> AthleteService
    LoginFrame --> IAuthService
    LoginFrame --> ModernButton
    LoginFrame --> PlaceholderPasswordField
    LoginFrame --> PlaceholderTextField
    LoginFrame --> RegisterPanel
    PrincipalFrames --> AthleteService
    PrincipalFrames --> IBillingService
    PrincipalFrames --> ICoachService
    PrincipalFrames --> IDisciplineService
    PrincipalFrames --> ISaleService
    PrincipalFrames --> SidebarButton
    PrincipalFrames --> AthleteListPanel
    PrincipalFrames --> CategoryPanel
    PrincipalFrames --> CoachPanel
    PrincipalFrames --> DisciplinePanel
    PrincipalFrames --> HomePanel
    PrincipalFrames --> PaymentPanel
    PrincipalFrames --> ReportsPanel
    RegisterPanel --> IAuthService
    RegisterPanel --> ModernButton
    RegisterPanel --> PlaceholderPasswordField
    RegisterPanel --> PlaceholderTextField
    LoginFrame ..> ClubLogo : creates
    LoginFrame ..> InputBox : creates
    LoginFrame ..> LinkLabel : creates
    PrincipalFrames ..> MembershipExpirationPolicy : creates
    PrincipalFrames ..> BrandHeader : creates
    PrincipalFrames ..> AthleteRegisterPanel : creates
    RegisterPanel ..> InputBox : creates
    RegisterPanel ..> LinkLabel : creates
    LoginFrame ..> ThemeConstants : uses
    LoginFrame ..> VectorIcon : uses
    PrincipalFrames ..> ThemeConstants : uses
    RegisterPanel ..> ThemeConstants : uses
    RegisterPanel ..> VectorIcon : uses

```

## 5. UI: panels and dialogs

Application screens (`ui.panels`). `Outer_Inner` is an inner class declared inside `Outer`.

```mermaid
classDiagram
    direction TB

    class HealthPanel {
        -format.DateTimeFormatter DATE_FMT$
        -Color OK_GREEN$
        -Color AMBER$
        -String ST_TREATMENT$
        -String ST_OBSERVATION$
        -String ST_RECOVERED$
        -String[] INJURY_STATUS$
        -Map~String, Color[]~ INJURY_PALETTE$
        +HealthPanel(AthleteService, IHealthService)
        -buildHeader() JComponent
        -buildStats() JComponent
        -statCard(VectorIcon_Type, Color, JLabel, String)$ JComponent
        -buildForm() JComponent
        -buildInjuriesCard() JComponent
        +refresh() void
        -reloadStats() void
        -loadRecord() void
        -showSummary(HealthRecord, boolean) void
        -saveRecord() void
        -addInjury() void
        -editInjury(InjuryRecord) void
        -injuryForm(PlaceholderTextField, ModernComboBox, PlaceholderTextField)$ JPanel
        -selected() HealthPanel_Person
        -selectedFitness() FitnessStatusEnum
        -descriptions()$ String[]
        -feedback(String, boolean) void
    }
    class AthleteListPanel {
        -Color SELECTION_BG$
        -Color GRID_COLOR$
        -String STATUS_ACTIVE$
        -String STATUS_INACTIVE$
        -int COL_STATUS$
        -format.DateTimeFormatter DATE_FMT$
        +AthleteListPanel(AthleteService)
        +AthleteListPanel(AthleteService, ISaleService)
        -buildHeader() JComponent
        -buildCard() JComponent
        -buildTable() JComponent
        -buildFooter() JComponent
        +loadAthleteData() void
        -applyFilter() void
        -applyFilterKeepingPage() void
        -refresh() void
        -rebuildPagination(int) void
        -buildDetailPanel() JComponent
        -athleteAtRow(int) Athlete
        -onSelectionChanged() void
        -restoreSelection() void
        -showDetail(AthleteKey) void
        -setDetailVisible(boolean) void
        -addSection(String) void
        -addRow(String, String) void
    }
    class AthleteListPanel_RoundedPanel {
        ~RoundedPanel()
        #paintComponent(Graphics) void
        #paintChildren(Graphics) void
        +paint(Graphics) void
    }
    class AthleteListPanel_BadgeRenderer {
        +getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int) Component
    }
    class AthleteListPanel_Badge {
        -String text
        ~Badge(String, Color)
        #paintComponent(Graphics) void
    }
    class AthleteListPanel_PageButton {
        -boolean active
        -VectorIcon icon
        -boolean hover
        ~PageButton(String, Type, boolean)
        #paintComponent(Graphics) void
    }
    class AthleteRegisterPanel {
        -AthleteService athleteService
        -IDisciplineService disciplineService
        -PlaceholderTextField txtId
        -PlaceholderTextField txtName
        -PlaceholderTextField txtAge
        -PlaceholderTextField txtPhone
        +AthleteRegisterPanel(AthleteService, IDisciplineService)
        -buildHeader() JComponent
        -buildScrollableForm() JComponent
        -buildCard() JComponent
        -field(String) PlaceholderTextField
        -label(String) JLabel
        -setRequiredText(JLabel, String) void
        -section(String, VectorIcon_Type) JComponent
        -addGroup(JPanel, JLabel, JComponent, GridBagConstraints, int, int) void
        -addFull(JPanel, JComponent, GridBagConstraints, int) void
        +refreshDisciplines() void
        -updateSpecificVisibility() void
        -updateDisciplineFields() void
        -saveAthlete() void
        -required(JTextField) String
        -showSuccessDialog(String, double) void
        -clearForm() void
    }
    class CategoryPanel {
        -AthleteService athleteService
        -CategoryService categoryService
        -IDisciplineService disciplineService
        -JPanel cardsRow
        -JLabel lblSummary
        -ModernButton addButton
        +CategoryPanel(AthleteService, CategoryService, IDisciplineService)
        -buildHeader() JComponent
        +refresh() void
        -renderCategories(CategoryPanel_CategoryView) void
        -setBusy(boolean, String) void
        -showCategoryForm(CategoryPanel_CategoryInput) void
        -ageSpinner(int) JSpinner
        -saveCategory(CategoryPanel_CategoryInput) void
        -buildCategoryCard(AgeCategory, List~Athlete~, List~String~, int) JComponent
        -categoryLabel(String)$ String
        -rangeText(List~Integer~)$ String
        -label(String, Font, Color)$ JLabel
        -metricRow(String, String)$ JComponent
        -separator()$ JComponent
    }
    class CoachFormDialog {
        -Coach existing
        -CoachFormDialog_Saver saver
        -List~String~ disciplines
        -boolean saved
        -PlaceholderTextField txtId
        -PlaceholderTextField txtName
        +CoachFormDialog(Window, Coach, List~String~, CoachFormDialog_Saver)
        +isSaved() boolean
        -buildCard() JComponent
        -label(String, boolean) JLabel
        -group(JLabel, JComponent) JPanel
        -addGroup(JPanel, JLabel, JComponent, GridBagConstraints, int, int) void
        -fillFromCoach(Coach) void
        -onSave(event.ActionEvent) void
        -showError(String) void
    }
    class CoachFormDialog_Saver {
        <<interface>>
        +save(Coach) void
    }
    class CoachPanel {
        -Color SELECTION_BG$
        -Color GRID_COLOR$
        -String STATUS_ACTIVE$
        -String STATUS_INACTIVE$
        -int COL_DISCIPLINE$
        -int COL_CATEGORY$
        +CoachPanel(ICoachService, IDisciplineService)
        -buildHeader() JComponent
        -buildCard() JComponent
        -buildTable() JComponent
        -buildFooter() JComponent
        +loadCoaches() void
        -applyFilter() void
        -applyFilterKeepingPage() void
        -refresh() void
        -rebuildPagination(int) void
        -coachAt(int) Coach
        -hit(int, int) boolean
        -isClickable(event.MouseEvent) boolean
        -handleClick(event.MouseEvent) void
        -newCoach() void
        -editCoach(Coach) void
        -showCoachForm(Coach, CoachFormDialog_Saver) boolean
        -toggleStatus(Coach) void
        -deleteCoach(Coach) void
    }
    class CoachPanel_IconTextRenderer {
        ~IconTextRenderer(Function~String, Icon~)
        +getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int) Component
    }
    class CoachPanel_ActionsCell {
        -VectorIcon edit
        -VectorIcon trash
        ~ActionsCell(Color)
        #paintComponent(Graphics) void
    }
    class CoachPanel_StatusBadge {
        -String text
        ~StatusBadge(String, Color)
        #paintComponent(Graphics) void
    }
    class CoachPanel_RoundedPanel {
        ~RoundedPanel()
        #paintComponent(Graphics) void
        #paintChildren(Graphics) void
        +paint(Graphics) void
    }
    class CoachPanel_PageButton {
        -boolean active
        -VectorIcon icon
        -boolean hover
        ~PageButton(String, Type, boolean)
        #paintComponent(Graphics) void
    }
    class DisciplinePanel {
        -Color[] ACCENTS$
        -Color OK_GREEN$
        -AthleteService athleteService
        -IDisciplineService disciplineService
        -CategoryService categoryService
        -JPanel grid
        +DisciplinePanel(AthleteService, IDisciplineService)
        +DisciplinePanel(AthleteService, IDisciplineService, CategoryService)
        -buildHeader() JComponent
        -addDiscipline() void
        -feedback(String, boolean) void
        +refresh() void
        -buildCard(String, List~Athlete~, List~String~, Color) JComponent
        -categoryRow(String, long, int, Color)$ JComponent
        -label(String, Font, Color)$ JLabel
    }
    class HomePanel {
        -Locale ES$
        -Color GREEN$
        -Color AMBER$
        -Color SKY$
        -Color TRACK$
        -Color[] PALETTE$
        +HomePanel(IDashBoardService, function.Consumer~String~)
        +setUsername(String) void
        +refresh() void
        -showInitialView() void
        -renderDashboard() void
        -showStatus(String, Color) void
        -build(DashboardSnapshot) void
        -addRow(JComponent, int) void
        -grid(int, int)$ JPanel
        -buildHero() JComponent
        -quickAction(String, VectorIcon_Type, String, int) ModernButton
        -statCard(VectorIcon_Type, Color, String, String, String) JComponent
        -card(String, String, JComponent) JComponent
        -collectionBody(double, double) JComponent
        -legendRow(Color, String, String) JComponent
        -disciplineBars(List~Athlete~) JComponent
        -categoryBars(List~Athlete~, List~String~) JComponent
        -bars(Map~String, Long~, int, String) JComponent
        -recentPayments(List~Payment~, Map~String, String~) JComponent
    }
    class HomePanel_ScrollBody {
        +getPreferredScrollableViewportSize() Dimension
        +getScrollableUnitIncrement(Rectangle, int, int) int
        +getScrollableBlockIncrement(Rectangle, int, int) int
        +getScrollableTracksViewportWidth() boolean
        +getScrollableTracksViewportHeight() boolean
    }
    class HomePanel_Bar {
        -String name
        -String value
        -double fraction
        ~Bar(String, String, double, Color)
        #paintComponent(Graphics) void
    }
    class HomePanel_Donut {
        -double fraction
        ~Donut(double)
        #paintComponent(Graphics) void
    }
    class HomePanel_Avatar {
        -String initials
        ~Avatar(String, Color)
        #paintComponent(Graphics) void
    }
    class PaymentPanel {
        -format.DateTimeFormatter DATE_FMT$
        -String VIEW_STATUS$
        -String VIEW_HISTORY$
        -String SEPARATOR$
        -Color OK_GREEN$
        -Color AMBER$
        +PaymentPanel(AthleteService, ISaleService)
        -buildHeader() JComponent
        -buildStats() JComponent
        -statCard(VectorIcon_Type, Color, JLabel, String)$ JComponent
        -buildForm() JComponent
        -buildTablesCard() JComponent
        -showTab(String) void
        +refresh() void
        -reloadCombo(AthleteKey) void
        -reloadStats() void
        -reloadStatusTable() void
        -reloadHistoryTable() void
        -updateAthleteInfo(boolean) void
        -fillBalance() void
        -registerPayment() void
        -warn(String) void
        -feedback(String, boolean) void
        -selectedAthlete() Athlete
        -selectedKey() AthleteKey
    }
    class PaymentPanel_TabButton {
        -boolean active
        ~TabButton(String, boolean)
        ~setActive(boolean) void
        #paintComponent(Graphics) void
    }
    class ReportsPanel {
        -String VIEW_PENDING$
        -String VIEW_MONTHLY$
        -Color OK_GREEN$
        -Map~String, Color[]~ STATUS_PALETTE$
        -AthleteService athleteService
        -ISaleService saleService
        +ReportsPanel(AthleteService, ISaleService)
        -buildHeader() JComponent
        -buildStats() JComponent
        -statCard(VectorIcon_Type, Color, JLabel, String)$ JComponent
        -buildRevenueCard() JComponent
        -buildTablesCard() JComponent
        -showTab(String) void
        +refresh() void
        -reloadStats(List~Athlete~, Map~AthleteKey, Double~) void
        -reloadRevenue() void
        -addRevenueRow(String, double, double, Color) void
        -reloadPendingTable(List~Athlete~, Map~AthleteKey, Double~) void
        -reloadMonthlyTable() void
        -statValue()$ JLabel
        -cardTitle(String)$ JLabel
        -sectionLabel(String)$ JLabel
    }
    class ReportsPanel_TabButton {
        -boolean active
        ~TabButton(String, boolean)
        ~setActive(boolean) void
        #paintComponent(Graphics) void
    }

    AthleteListPanel *-- AthleteListPanel_RoundedPanel : inner class
    AthleteListPanel *-- AthleteListPanel_BadgeRenderer : inner class
    AthleteListPanel *-- AthleteListPanel_Badge : inner class
    AthleteListPanel *-- AthleteListPanel_PageButton : inner class
    CoachFormDialog *-- CoachFormDialog_Saver : inner class
    CoachPanel *-- CoachPanel_IconTextRenderer : inner class
    CoachPanel *-- CoachPanel_ActionsCell : inner class
    CoachPanel *-- CoachPanel_StatusBadge : inner class
    CoachPanel *-- CoachPanel_RoundedPanel : inner class
    CoachPanel *-- CoachPanel_PageButton : inner class
    HomePanel *-- HomePanel_ScrollBody : inner class
    HomePanel *-- HomePanel_Bar : inner class
    HomePanel *-- HomePanel_Donut : inner class
    HomePanel *-- HomePanel_Avatar : inner class
    PaymentPanel *-- PaymentPanel_TabButton : inner class
    ReportsPanel *-- ReportsPanel_TabButton : inner class
    AthleteListPanel --> Athlete
    AthleteListPanel --> AthleteService
    AthleteListPanel --> PlaceholderTextField
    AthleteListPanel_PageButton --> VectorIcon
    AthleteRegisterPanel --> AthleteService
    AthleteRegisterPanel --> ModernComboBox
    AthleteRegisterPanel --> InputBox
    AthleteRegisterPanel --> PlaceholderTextField
    CategoryPanel --> AthleteService
    CategoryPanel --> IBillingService
    CategoryPanel --> IDisciplineService
    CoachFormDialog --> Coach
    CoachFormDialog --> ModernComboBox
    CoachFormDialog --> PlaceholderTextField
    CoachPanel --> Coach
    CoachPanel --> ICoachService
    CoachPanel --> PlaceholderTextField
    CoachPanel_ActionsCell --> VectorIcon
    CoachPanel_PageButton --> VectorIcon
    DisciplinePanel --> AthleteService
    DisciplinePanel --> IDisciplineService
    DisciplinePanel --> PlaceholderTextField
    HomePanel --> AthleteService
    HomePanel --> ICoachService
    HomePanel --> ISaleService
    PaymentPanel --> Athlete
    PaymentPanel --> AthleteService
    PaymentPanel --> ISaleService
    PaymentPanel --> ModernComboBox
    PaymentPanel --> PlaceholderTextField
    ReportsPanel --> AthleteService
    ReportsPanel --> ISaleService
    AthleteListPanel ..> CardPanel : creates
    AthleteListPanel ..> InputBox : creates
    AthleteListPanel ..> ModernButton : creates
    AthleteListPanel ..> VectorIcon : creates
    AthleteRegisterPanel ..> Basketballer : creates
    AthleteRegisterPanel ..> Footballer : creates
    AthleteRegisterPanel ..> CardPanel : creates
    AthleteRegisterPanel ..> ModernButton : creates
    AthleteRegisterPanel ..> VectorIcon : creates
    CategoryPanel ..> CardPanel : creates
    CategoryPanel ..> IconBadge : creates
    CategoryPanel ..> ShareBar : creates
    CoachFormDialog ..> CardPanel : creates
    CoachFormDialog ..> InputBox : creates
    CoachFormDialog ..> ModernButton : creates
    CoachPanel ..> CardPanel : creates
    CoachPanel ..> InputBox : creates
    CoachPanel ..> ModernButton : creates
    CoachPanel ..> VectorIcon : creates
    CoachPanel ..> CoachFormDialog : creates
    DisciplinePanel ..> CardPanel : creates
    DisciplinePanel ..> IconBadge : creates
    DisciplinePanel ..> InputBox : creates
    DisciplinePanel ..> ModernButton : creates
    DisciplinePanel ..> ShareBar : creates
    HomePanel ..> CardPanel : creates
    HomePanel ..> IconBadge : creates
    HomePanel ..> ModernButton : creates
    PaymentPanel ..> CardPanel : creates
    PaymentPanel ..> IconBadge : creates
    PaymentPanel ..> InputBox : creates
    PaymentPanel ..> ModernButton : creates
    ReportsPanel ..> CardPanel : creates
    ReportsPanel ..> IconBadge : creates
    ReportsPanel ..> ShareBar : creates
    AthleteListPanel ..> SearchText : uses
    AthleteListPanel ..> SpanishLabels : uses
    CategoryPanel ..> SpanishLabels : uses
    CoachFormDialog ..> CoachLabels : uses
    CoachFormDialog ..> SpanishLabels : uses
    CoachFormDialog ..> VectorIcon : uses
    CoachPanel ..> CoachLabels : uses
    CoachPanel ..> SearchText : uses
    CoachPanel ..> SpanishLabels : uses
    DisciplinePanel ..> SpanishLabels : uses
    HomePanel ..> Money : uses
    HomePanel ..> SpanishLabels : uses
    HomePanel ..> VectorIcon : uses
    PaymentPanel ..> ModernTable : uses
    PaymentPanel ..> Money : uses
    ReportsPanel ..> ModernTable : uses
    ReportsPanel ..> Money : uses
    ReportsPanel ..> SpanishLabels : uses
    JPanel <|-- HealthPanel
    HealthPanel --> IHealthService : uses
    CategoryPanel --> CategoryService : manages categories
```

## 6. UI: reusable components

Swing components, icons, label/search helpers (`ui.components`).

```mermaid
classDiagram
    direction TB

    class CategoryChooser {
        -CategoryChooser()
        +ask(Component, int, List~AgeCategory~, String)$ AgeCategory
        -rangeText(List~Integer~)$ String
        -escape(String)$ String
    }
    class ModernComboBox {
        +ModernComboBox(String[])
        +ModernComboBox(String[], function.UnaryOperator~String~)
    }
    class ModernComboBox_ModernUI {
        #createArrowButton() JButton
        +paintCurrentValueBackground(Graphics, Rectangle, boolean) void
        #createPopup() ComboPopup
    }
    class BrandHeader {
        -VectorIcon ball
        +BrandHeader()
        #paintComponent(Graphics) void
    }
    class CardPanel {
        -int SHADOW$
        +CardPanel(LayoutManager, int)
        #paintComponent(Graphics) void
    }
    class ClubLogo {
        -Color NAVY$
        -Color NAVY_DARK$
        -Color SKY$
        -Color ORANGE$
        +ClubLogo()
        #paintComponent(Graphics) void
        -inset(Shape, double, double, double)$ Shape
        -drawSoccerBall(Graphics2D, double, double, double)$ void
        -drawBasketball(Graphics2D, double, double, double)$ void
        -drawStar(Graphics2D, double, double, double)$ void
    }
    class CoachLabels {
        +String[] DISCIPLINES_ES$
        -CoachLabels()
        +disciplineValue(String)$ String
        +categoryLabel(CategoryEnum)$ String
        +categoryLabels()$ String[]
        +categoryFromLabel(String)$ CategoryEnum
    }
    class IconBadge {
        -VectorIcon icon
        -Color tint
        +IconBadge(VectorIcon_Type, Color, int)
        #paintComponent(Graphics) void
    }
    class InputBox {
        -boolean focused
        -JLabel iconLabel
        -VectorIcon icon
        +InputBox(VectorIcon_Type, JComponent)
        +setIcon(VectorIcon_Type) void
        -setFocused(boolean) void
        #paintComponent(Graphics) void
    }
    class LinkLabel {
        +LinkLabel(String, Runnable)
    }
    class ModernButton {
        -ModernButton_Style style
        -VectorIcon icon
        -boolean hover
        -boolean pressed
        +ModernButton(String, VectorIcon_Type, ModernButton_Style, int)
        #paintComponent(Graphics) void
    }
    class ModernButton_Style {
        <<enumeration>>
        PRIMARY
        SECONDARY
        DANGER
    }
    class ModernTable {
        -Color SELECTION_BG$
        -Color GRID_COLOR$
        -ModernTable()
        +create(String[], int[], int[])$ JTable
        +setBadgeColumn(JTable, int, Map~String, Color[]~)$ void
        +pill(Color, Color)$ Color[]
        +wrap(JTable)$ JComponent
    }
    class ModernTable_Badge {
        -String text
        ~Badge(String, Color, Color[])
        #paintComponent(Graphics) void
    }
    class Money {
        -Locale CO$
        -Money()
        +cop(double)$ String
    }
    class PlaceholderPasswordField {
        -String placeholder
        +PlaceholderPasswordField(String)
        #paintComponent(Graphics) void
    }
    class PlaceholderTextField {
        -String placeholder
        +PlaceholderTextField(String)
        +setPlaceholder(String) void
        #paintComponent(Graphics) void
    }
    class SearchText {
        -SearchText()
        +normalize(String)$ String
        +contains(String, String)$ boolean
    }
    class ShareBar {
        -Color color
        -double value
        +ShareBar(Color)
        +setValue(double) void
        #paintComponent(Graphics) void
    }
    class SidebarButton {
        -Color TEXT_IDLE$
        -boolean active
        -boolean hover
        -VectorIcon icon
        +SidebarButton(String)
        +SidebarButton(String, VectorIcon_Type)
        -iconFor(String)$ VectorIcon_Type
        +setActive(boolean) void
        #paintComponent(Graphics) void
    }
    class SpanishLabels {
        -Map~String, String~ MAP$
        -SpanishLabels()
        +discipline(String)$ String
        +of(String)$ String
    }
    class VectorIcon {
        -VectorIcon_Type type
        -int size
        -Color color
        +VectorIcon(VectorIcon_Type, int, Color)
        +setColor(Color) void
        +getIconWidth() int
        +getIconHeight() int
        +paintIcon(Component, Graphics, int, int) void
        -draw(Graphics2D) void
        -line(Graphics2D, double, double, double, double)$ void
        -poly(Graphics2D, boolean, double[])$ void
    }
    class VectorIcon_Type {
        <<enumeration>>
        HOME
        USERS
        USER_PLUS
        TAG
        TROPHY
        WHISTLE
        CARD
        CHART
        GEAR
        ID
        USER
        PHONE
        HASH
        BALL
        CALENDAR
        CHECK
        CLOSE
        RULER
        FLAG
        CHEVRON
        SEARCH
        TRASH
        LOGOUT
        CHEV_LEFT
        CHEV_RIGHT
        EDIT
        LOCK
    }

    ModernComboBox *-- ModernComboBox_ModernUI : inner class
    ModernButton *-- ModernButton_Style : inner class
    ModernTable *-- ModernTable_Badge : inner class
    VectorIcon *-- VectorIcon_Type : inner class
    BrandHeader --> VectorIcon
    IconBadge --> VectorIcon
    InputBox --> VectorIcon
    ModernButton --> VectorIcon
    SidebarButton --> VectorIcon
    ModernComboBox ..> VectorIcon : creates
    CoachLabels ..> SpanishLabels : uses
    JComboBox <|-- CategoryChooser
```

## 7. Application entry point

Composition root: `Main` creates the repositories, services and frames and injects them.

```mermaid
classDiagram
    direction TB


    class Main {
        +Main()
        +main(String[])$ void
        -showLogin(IAuthService, function.Supplier~PrincipalFrames~)$ void
    }

    Main ..> FileAthleteRepository : creates
    Main ..> FileCoachRepository : creates
    Main ..> Fileuserrepository : creates
    Main ..> AthleteService : creates
    Main ..> Authservice : creates
    Main ..> BillingService : creates
    Main ..> CoachService : creates
    Main ..> DisciplineService : creates
    Main ..> SaleService : creates
    Main ..> LoginFrame : creates
    Main ..> PrincipalFrames : creates
    Main ..> IAthleteRepository : uses
    Main ..> ICoachRepository : uses
    Main ..> IAuthService : uses
    Main ..> IBillingService : uses
    Main ..> ICoachService : uses
    Main ..> IDisciplineService : uses
    Main ..> ISaleService : uses
    Main ..> PostgresAthleteRepository : creates
    Main ..> PostgresCategoryRepository : creates
    Main ..> PostgresDisciplineRepository : creates
    Main ..> CategoryService : creates
    Main ..> DashboardService : creates
    Main ..> HealthService : creates
```

## 8. Database connection (JDBC + HikariCP)

The `conection` package supplies PostgreSQL connections to the three PostgreSQL repositories. Credentials come from an environment variable; no password is depicted.

```mermaid
classDiagram
    direction TB
    class conection {
        -HikariDataSource dataSource$
        -createDataSource()$ HikariDataSource
        +conectar()$ Connection
        +closePool()$ void
    }
    class TestConnection
    class PostgresAthleteRepository
    class PostgresCategoryRepository
    class PostgresDisciplineRepository
    class HikariDataSource {
        <<external>>
    }
    conection --> HikariDataSource
    PostgresAthleteRepository ..> conection
    PostgresCategoryRepository ..> conection
    PostgresDisciplineRepository ..> conection
    TestConnection ..> conection
```
