# Tabla de trazabilidad de principios GRASP y SOLID

Proyecto AuraTech – Club Deportivo Creatina-Boys. Cada principio se relaciona con la clase o método concreto donde se aplica y la razón de diseño.

## Patrones GRASP

| Patrón | Clase / método donde se aplica | Justificación técnica |
| :-- | :-- | :-- |
| **Controller** | `AthleteService` (`registerAthlete`, `toggleAthleteStatus`, `renewMembership`) | Recibe las solicitudes de la interfaz y coordina modelo, política de unicidad y repositorio. Las pantallas no manipulan los datos directamente. |
| **Creator** | `FootballerParser.fromTextFields`, `BasketballerParser.fromTextFields`, `CoachParser.fromTextFields`, `PaymentParser` | Cada parser tiene todos los campos necesarios para construir su entidad (`new Footballer(...)`, `new Coach(...)`) al leer los archivos. |
| **Information Expert** | `BillingService.determineCategory` / `calculateFee` | Es quien conoce los rangos de edad y las tarifas, por lo que calcula categoría y tarifa. |
| **Information Expert** | `SaleService` (`getPendingBalance`, `calculateEstimatedMonthlyRevenue`, `getRevenueByCategory`) | Conoce los pagos y los deportistas, por lo que calcula saldos e ingresos. |
| **Information Expert** | `PaymentStatusEnum.of(fee, paid)` | El estado Pagado / Parcial / Pendiente se calcula donde está la regla, a partir de la tarifa y lo pagado. |
| **Information Expert** | `Payment.belongsTo(AthleteKey)` | El pago decide a qué registro (documento + disciplina) pertenece, sin que el servicio tenga que inspeccionar sus campos. |
| **Low Coupling** | `AthleteService(IAthleteRepository, IBillingService)`, `CoachPanel(ICoachService)` | Dependen de interfaces, no de archivos ni de implementaciones concretas. Se puede cambiar la persistencia sin tocar el servicio. |
| **High Cohesion** | `DataValidator`, `SearchText`, `TextCodec`, `FileStorage`, un repositorio por entidad | Cada clase tiene una sola tarea: validar, normalizar texto para búsquedas, escapar caracteres o leer y escribir archivos. |
| **Polymorphism** | `Athlete.calculateFee` / `getDiscipline` (abstractos), sobrescritos en `Footballer` y `Basketballer` | El comportamiento depende del tipo concreto del deportista, sin condicionales por tipo. |
| **Polymorphism** | `AthleteParserRegistry.register(Class, AthleteParser)` | Elige el parser según la clase del deportista, sin `if` por disciplina. |
| **Pure Fabrication** | `AthleteUniquenessPolicy`, `PaymentDisciplineMigration` | No representan conceptos del dominio: agrupan reglas para mantener `AthleteService` y `SaleService` cohesivos. |
| **Protected Variations** | `IActivationPolicy` (implementada por `MembershipExpirationPolicy`) | Aísla el punto que puede cambiar, la regla de cuándo se desactiva un deportista, detrás de una interfaz estable. |

## Principios SOLID

| Principio | Clase / relación donde es observable | Justificación técnica |
| :-- | :-- | :-- |
| **SRP** (responsabilidad única) | `FileAthleteRepository` solo gestiona el archivo; `FootballerParser` solo el formato de su registro; `TextCodec` solo el escape; `FileStorage` solo el acceso a disco; `DataValidator` solo valida | Cada clase tiene un único motivo para cambiar. Cambiar el formato de un archivo no toca la lógica de negocio. |
| **SRP** | `AthleteUniquenessPolicy` separada de `AthleteService` | La regla de unicidad de documento y dorsal vive en su propia clase, no mezclada con el registro. |
| **OCP** (abierto/cerrado) | Jerarquía `Athlete` → `Footballer`, `Basketballer`; `AthleteParserRegistry` | Agregar una disciplina (por ejemplo voleibol) implica crear una subclase y su parser, sin modificar las clases existentes. |
| **OCP** | `IActivationPolicy` + `AthleteService.applyActivationPolicy(IActivationPolicy)` | Una nueva regla de activación es otra implementación de la interfaz, sin cambiar `AthleteService`. |
| **OCP** | `IPaymentRepository.findByAthleteKey` (método `default`) | Se agregó la consulta por clave compuesta sin romper las implementaciones existentes. |
| **LSP** (sustitución de Liskov) | `Footballer` y `Basketballer` donde se espera un `Athlete` | Los servicios y repositorios trabajan con `Athlete` y funcionan igual con cualquier subclase. |
| **ISP** (segregación de interfaces) | `IBillingService`, `IAuthService`, `IActivationPolicy`, `AthleteParser` | Interfaces pequeñas y específicas: ninguna clase implementa métodos que no usa. |
| **DIP** (inversión de dependencias) | `AthleteService(IAthleteRepository, IBillingService)`; `SaleService(IAthleteRepository, IBillingService, IPaymentRepository)`; `Main` | Los servicios dependen de abstracciones recibidas por constructor. `Main` crea las implementaciones y las inyecta. |
