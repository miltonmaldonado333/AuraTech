# Tabla de trazabilidad de principios GRASP y SOLID

Proyecto AuraTech – Club Deportivo Creatina-Boys. Cada principio se relaciona con la clase o método concreto donde se aplica y la razón de diseño.

## Patrones GRASP

| Patrón | Clase / método donde se aplica | Justificación técnica |
| :-- | :-- | :-- |
| **Controller** | `AthleteService` (`registerAthlete`, `updateAthleteData`, `toggleAthleteStatus`, `renewMembership`) | Recibe las solicitudes de la interfaz y coordina modelo, política de unicidad y repositorio. Las pantallas no manipulan los datos directamente. |
| **Controller** | `CategoryService.createCategory`, `DisciplineService.registerDiscipline` | Reciben la solicitud de las pantallas *Categorías* y *Disciplinas*, validan los datos y delegan el guardado en el repositorio. |
| **Creator** | `FootballerParser.fromTextFields`, `BasketballerParser.fromTextFields`, `CoachParser.fromTextFields`, `PaymentParser` | Cada parser tiene todos los campos necesarios para construir su entidad (`new Footballer(...)`, `new Coach(...)`) al leer los archivos. |
| **Creator** | `PostgresAthleteRepository` (lectura de filas) | Tiene la disciplina y los datos de cada fila, por lo que crea el `Footballer`, el `Basketballer` o el `GeneralAthlete` que corresponde. |
| **Information Expert** | `BillingService.determineEnrollmentCategory` / `findEnrollmentCategories` | Consulta el catálogo de categorías y conoce a qué categoría (y tarifa) pertenece una edad. Si la edad cae en varias, devuelve todas las opciones. |
| **Information Expert** | `CategoryService` | Conoce las reglas del catálogo (nombre, rango de edades y tarifa válidos) y las verifica antes de crear una categoría. |
| **Information Expert** | `SaleService` (`getPendingBalance`, `calculateEstimatedMonthlyRevenue`, `getRevenueByCategory`) | Conoce los pagos y los deportistas, por lo que calcula saldos e ingresos. |
| **Information Expert** | `PaymentStatusEnum.of(fee, paid)` | El estado Pagado / Parcial / Pendiente se calcula donde está la regla, a partir de la tarifa y lo pagado. |
| **Information Expert** | `Payment.belongsTo(AthleteKey)` | El pago decide a qué registro (documento + disciplina) pertenece, sin que el servicio tenga que inspeccionar sus campos. |
| **Low Coupling** | `AthleteService(IAthleteRepository, IBillingService)`, `CategoryService(ICategoryRepository)`, `DisciplineService(IDisciplineRepository)`, `CoachPanel(ICoachService)` | Dependen de interfaces, no de archivos, de la base de datos ni de implementaciones concretas. Se puede cambiar la persistencia sin tocar el servicio. |
| **High Cohesion** | `DataValidator`, `SearchText`, `TextCodec`, `FileStorage`, un repositorio por entidad, `PostgresCategoryRepository` | Cada clase tiene una sola tarea: validar, normalizar texto para búsquedas, escapar caracteres, leer y escribir archivos o consultar un catálogo. |
| **Polymorphism** | `Athlete.calculateFee` / `getDiscipline` (abstractos), sobrescritos en `Footballer`, `Basketballer` y `GeneralAthlete` | El comportamiento depende del tipo concreto del deportista, sin condicionales por tipo. |
| **Polymorphism** | `AthleteParserRegistry.register(Class, AthleteParser)` | Elige el parser según la clase del deportista, sin `if` por disciplina. |
| **Pure Fabrication** | `AthleteUniquenessPolicy`, `PaymentDisciplineMigration` | No representan conceptos del dominio: agrupan reglas para mantener `AthleteService` y `SaleService` cohesivos. |
| **Protected Variations** | `IActivationPolicy` (implementada por `MembershipExpirationPolicy`) | Aísla el punto que puede cambiar, la regla de cuándo se desactiva un deportista, detrás de una interfaz estable. |
| **Protected Variations** | `IAthleteRepository` (`FileAthleteRepository` y `PostgresAthleteRepository`), `IDisciplineRepository` (`FileDisciplineRepository` y `PostgresDisciplineRepository`), `ICategoryRepository` | El paso de archivos a PostgreSQL en la fase 3 se hizo cambiando la implementación que `Main` inyecta, sin modificar los servicios ni las pantallas. |

## Principios SOLID

| Principio | Clase / relación donde es observable | Justificación técnica |
| :-- | :-- | :-- |
| **SRP** (responsabilidad única) | `FileAthleteRepository` solo gestiona el archivo; `FootballerParser` solo el formato de su registro; `TextCodec` solo el escape; `FileStorage` solo el acceso a disco; `DataValidator` solo valida | Cada clase tiene un único motivo para cambiar. Cambiar el formato de un archivo no toca la lógica de negocio. |
| **SRP** | `AthleteUniquenessPolicy` separada de `AthleteService` | La regla de unicidad de documento y dorsal vive en su propia clase, no mezclada con el registro. |
| **SRP** | `CategoryService` (reglas y validación), `PostgresCategoryRepository` (SQL y transacción) y `CategoryChooser` (pregunta la categoría al usuario) | Validar una categoría, guardarla y elegirla en pantalla son tres motivos de cambio distintos y están en tres clases. |
| **OCP** (abierto/cerrado) | Jerarquía `Athlete` → `Footballer`, `Basketballer`, `GeneralAthlete`; `AthleteParserRegistry` | Una disciplina nueva con los datos de inscripción comunes se agrega desde la pantalla *Disciplinas* y se maneja con `GeneralAthlete`, sin cambiar código. Una disciplina con datos propios (por ejemplo voleibol) implica crear una subclase y su parser, sin modificar las clases existentes. |
| **OCP** | `CategoryService.createCategory` y el catálogo `age_categories` | Las categorías y tarifas son datos: una categoría nueva no requiere modificar ni recompilar el código. |
| **OCP** | `IActivationPolicy` + `AthleteService.applyActivationPolicy(IActivationPolicy)` | Una nueva regla de activación es otra implementación de la interfaz, sin cambiar `AthleteService`. |
| **OCP** | `IPaymentRepository.findByAthleteKey` (método `default`) | Se agregó la consulta por clave compuesta sin romper las implementaciones existentes. |
| **LSP** (sustitución de Liskov) | `Footballer`, `Basketballer` y `GeneralAthlete` donde se espera un `Athlete` | Los servicios y repositorios trabajan con `Athlete` y funcionan igual con cualquier subclase. |
| **LSP** | `FileAthleteRepository` y `PostgresAthleteRepository` donde se espera un `IAthleteRepository` | Los servicios funcionan igual con cualquiera de las dos implementaciones. |
| **ISP** (segregación de interfaces) | `IBillingService`, `IAuthService`, `IActivationPolicy`, `AthleteParser`, `ICategoryRepository` (`findAll`, `create`), `IDisciplineRepository` (`findAll`, `add`) | Interfaces pequeñas y específicas: ninguna clase implementa métodos que no usa. |
| **DIP** (inversión de dependencias) | `AthleteService(IAthleteRepository, IBillingService)`; `SaleService(IAthleteRepository, IBillingService, IPaymentRepository)`; `CategoryService(ICategoryRepository)`; `DisciplineService(IDisciplineRepository)`; `Main` | Los servicios dependen de abstracciones recibidas por constructor. `Main` crea las implementaciones (`PostgresAthleteRepository`, `PostgresCategoryRepository`, `PostgresDisciplineRepository`) y las inyecta. |