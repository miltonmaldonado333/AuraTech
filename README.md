# Sistema de Gestión del Club Deportivo Creatina-Boys (AuraTech)

Aplicación de escritorio en Java (Swing + Maven) para gestionar deportistas, disciplinas,
entrenadores, categorías por edad y mensualidades del **Club Deportivo Creatina-Boys**.

Proyecto de aula — Universidad Popular del Cesar (UPC), Programación Computacional III.

---

## Características

- **Gestión de deportistas:** registro, edición y consulta con panel de detalle del deportista seleccionado. Desde la lista se editan nombre, edad, teléfono, dorsal y posición o estatura (el documento y la disciplina no cambian). La búsqueda por documento, nombre, disciplina o categoría ignora tildes y mayúsculas (`ninos`, `NIÑOS` y `Niños` dan el mismo resultado). Los deportistas se guardan en una base de datos PostgreSQL (ver sección *Base de datos*).
- **Disciplinas:** atributos propios de cada deporte: futbolistas (posición de juego) y basquetbolistas (estatura en metros). El catálogo vive en PostgreSQL y se pueden agregar disciplinas nuevas desde la pantalla *Disciplinas*; sus deportistas se manejan con los datos de inscripción comunes (`GeneralAthlete`). Una misma persona puede estar en varias disciplinas (clave documento + disciplina), con pagos y saldo independientes.
- **Entrenadores:** registro, edición, activación/desactivación y eliminación, con disciplina y categoría que entrena, búsqueda y paginación.
- **Control de pagos:** pagos totales o parciales de la mensualidad, estado del mes (Pagado, Parcial, Pendiente) e historial; el monto se valida antes de registrarse (no acepta negativos ni caracteres no numéricos).
- **Acceso:** inicio de sesión, registro de usuarios y cierre de sesión; contraseñas con sal y PBKDF2, bloqueo de 15 s tras 5 intentos fallidos.
- **Inicio y reportes:** panel de inicio con indicadores del club y reportes financieros (proyectado, recaudado, pendiente, cumplimiento, por disciplina y categoría, cartera pendiente y recaudo mes a mes).
- **Categoría y tarifa automáticas por edad:** el catálogo de categorías se guarda en PostgreSQL y se pueden crear categorías nuevas (nombre, rango de edades y tarifa) desde la pantalla *Categorías*; se usan para las nuevas inscripciones y los deportistas ya inscritos conservan su categoría y tarifa. Si una edad pertenece a varias categorías, la aplicación pregunta en cuál desea estar el deportista. Catálogo inicial:

  | Categoría | Edad          | Tarifa mensual (COP) |
  | :-------- | :------------ | :------------------- |
  | Niños     | 5 a 12 años   | $50.000              |
  | Jóvenes   | 13 a 17 años  | $70.000              |
  | Adultos   | 18 años o más | $100.000             |

  Los menores de 5 años se rechazan con `InvalidAgeException`.
- **Validación de datos de entrada** centralizada en `DataValidator` y aplicada en `AthleteService.registerAthlete` (documento de 6 a 12 dígitos, nombre solo con letras, teléfono de 7 a 10 dígitos, dorsal de 0 a 99, estatura de 0,5 a 2,5 m), además de la unicidad de documento y dorsal por disciplina.
- **Fichas de salud:** estado físico, restricciones e historial de lesiones por persona (documento), con pantalla propia *Salud*.
- **Persistencia robusta:** deportistas, categorías y disciplinas en PostgreSQL; el resto de datos en archivos UTF-8, escritura mediante archivo temporal con copia `.bak`, y tolerancia a líneas dañadas.

## Fases del proyecto

| Fase   | Periodo        | Alcance                                                                                                  |
| :----- | :------------- | :------------------------------------------------------------------------------------------------------- |
| Fase 1 | 14 – 24 oct    | Arquitectura de capas, modelo de dominio, jerarquía de deportistas, persistencia en archivos, excepciones, validación y diagramas UML. |
| Fase 2 | 28 – 30 oct    | Interfaz completa (Inicio, Deportistas, Registrar, Categorías, Disciplinas, Entrenadores, Pagos y Reportes), servicios de pagos, entrenadores y disciplinas, autenticación, validaciones en la UI y documentación de calidad (trazabilidad GRASP/SOLID y código limpio). |
| Fase 3 | 11 – 21 nov    | Migración de los deportistas a PostgreSQL, catálogos de categorías y disciplinas en la base de datos, edición de datos de deportistas, creación de categorías y disciplinas desde la interfaz, selector de categoría cuando la edad pertenece a varias, dashboard con todas las categorías y correcciones de errores. |

## Estado del proyecto

| Módulo                                                                  | Estado                                                        |
| :---------------------------------------------------------------------- | :------------------------------------------------------------ |
| Modelo, excepciones y validación                                        | Implementado                                                  |
| Repositorios (deportistas, categorías y disciplinas en PostgreSQL; entrenadores, pagos, usuarios y salud en archivos) | Implementado |
| Servicios de deportistas, facturación, categorías, pagos, entrenadores, disciplinas y autenticación | Implementado y conectado a la UI |
| Pantallas: Inicio, Deportistas, Registrar, Categorías, Disciplinas, Entrenadores, Pagos, Salud y Reportes | Implementado                    |
| Login, registro de usuarios y cierre de sesión                          | Implementado                                                  |
| Control de pagos (RF4) con validación de monto                          | Implementado                                                  |
| Un deportista en varias disciplinas (RF2.1)                             | Implementado                                                  |
| Búsqueda sin distinguir tildes ni mayúsculas (RF3.1)                    | Implementado (deportistas y entrenadores)                     |
| Fichas de salud (RF6): servicio, repositorio y pantalla *Salud*         | Implementado y conectado en `Main`                            |
| Edición de datos de un deportista (RF1)                                 | Implementado (botón *Editar* en la lista de deportistas)      |
| Catálogo de disciplinas en PostgreSQL y nuevas disciplinas (RF2)        | Implementado                                                  |
| Catálogo de categorías en PostgreSQL y creación de categorías (RF3.2)   | Implementado                                                  |



## Arquitectura de 4 capas

Desacoplamiento entre capas mediante interfaces (DIP), más dos paquetes transversales:

1. **Presentación / UI:** `com.creatinaboys.ui` (ventanas, paneles en `ui.panels`, componentes Swing reutilizables en `ui.components` y estilos en `ui.Theme`).
2. **Servicios / Lógica:** `com.creatinaboys.service` (reglas de negocio, tarifas y categorías).
3. **Repositorio / Persistencia:** `com.creatinaboys.repository` (acceso a datos y archivos).
4. **Modelo / Dominio:** `com.creatinaboys.model` (entidades y enumerados).

Transversales: `com.creatinaboys.exceptions` (excepciones del dominio) y `com.creatinaboys.validation` (validación de entradas).

## Documentación

Todo está en [`docs/`](docs/):

| Documento                                   | Contenido                                                        |
| :------------------------------------------ | :--------------------------------------------------------------- |
| `Analisys.md`                               | Reglas de negocio, requisitos funcionales y no funcionales       |
| `class-diagram.md`                          | Diagrama de clases                                               |
| `hierarchy-diagram.md`                      | Jerarquía de deportistas y disciplinas                           |
| `Layers_diagram.md`                         | Diagrama de la arquitectura en capas                             |
| `traceability.md`                           | Trazabilidad de los patrones GRASP y principios SOLID al código  |
| `CleanCode.md`                              | Prácticas de código limpio aplicadas                             |
| `commits.md`                                | Reporte de commits por integrante                                |
| `IAUsage/leader.md`, `IAUsage/developer1.md` | Bitácoras de uso de IA de cada integrante                       |
| `Proyecto_de_Aula_Creatina_Boys_actualizado.docx` | Informe del proyecto de aula                               |

## Estructura del proyecto

```text
AuraTech/
|-- docs/
|   |-- IAUsage/
|   |   |-- developer1.md
|   |   `-- leader.md
|   |-- Analisys.md
|   |-- class-diagram.md
|   |-- CleanCode.md
|   |-- commits.md
|   |-- hierarchy-diagram.md
|   |-- Layers_diagram.md
|   |-- traceability.md
|   `-- Proyecto_de_Aula_Creatina_Boys_actualizado.docx
|-- data/
|-- src/main/java/com/creatinaboys/
|   |-- exceptions/
|   |-- model/
|   |-- repository/
|   |-- service/
|   |-- ui/
|   |   |-- components/
|   |   |-- panels/
|   |   `-- Theme/
|   |-- validation/
|   `-- Main.java
|-- .gitignore
|-- pom.xml
|-- README.md
`-- team.md
```

## Requisitos previos

1. **JDK 17** o superior.
2. **Apache Maven 3.8+**.
3. Acceso a la base de datos PostgreSQL del proyecto y su contraseña (variable de entorno `AURATECH_DB_PASSWORD`, ver *Base de datos*).

## Instalación y ejecución

```bash
git clone https://github.com/miltonmaldonado333/AuraTech.git
cd AuraTech
export AURATECH_DB_PASSWORD=<contraseña-de-la-base-de-datos>   # Windows (PowerShell): $env:AURATECH_DB_PASSWORD="..."
mvn clean compile
mvn exec:java
```

Si `AURATECH_DB_PASSWORD` no está definida, la aplicación no arranca (`IllegalStateException: Missing database password.`).

`mvn exec:java` usa la clase principal `com.creatinaboys.Main` configurada en el `pom.xml`.

## Base de datos

Los **deportistas** (tablas `athlete_people` y `athletes`), las **categorías** (`age_categories` y `age_category_assignments`) y las **disciplinas** (`disciplines`) se guardan en una base de datos **PostgreSQL** alojada en Supabase. `PostgresAthleteRepository`, `PostgresCategoryRepository` y `PostgresDisciplineRepository` implementan sus interfaces (`IAthleteRepository`, `ICategoryRepository` e `IDisciplineRepository`) y la conexión la gestiona un pool HikariCP (`com.creatinaboys.conection.conection`, máximo 3 conexiones, SSL obligatorio). El usuario y la URL están en esa clase; la **contraseña no se guarda en el código**: se lee de la variable de entorno `AURATECH_DB_PASSWORD`. `TestConnection` sirve para comprobar la conexión.

El resto de la información (entrenadores, pagos, usuarios y salud) sigue en archivos de texto, como se describe a continuación.

## Datos en archivos

Los datos se guardan en la carpeta `data/` junto al lugar desde donde se ejecuta la aplicación
(se crea automáticamente):

| Archivo                    | Contenido                  |
| :------------------------- | :------------------------- |
| `data/athletes.txt`        | Deportistas (formato anterior a PostgreSQL; ya no lo usa `Main`) |
| `data/coaches.txt`         | Entrenadores               |
| `data/payments.txt`        | Pagos de mensualidades     |
| `data/users.txt`           | Usuarios (sal y hash, sin contraseña en claro) |
| `data/disciplines.txt`     | Catálogo de disciplinas (formato anterior a PostgreSQL; ya no lo usa `Main`) |
| `data/health_records.txt`  | Fichas de salud y lesiones |

Son archivos de texto UTF-8 con campos separados por `;`. Los caracteres `;`, `#`, `~` y `\` dentro de un
valor se escapan con `\`. Antes de cada escritura se conserva la versión anterior como `<archivo>.bak`.
Una línea con formato inválido se omite y se informa por la consola, sin impedir la carga del resto.

## Acceso inicial

La primera vez que se ejecuta se crea el usuario `admin` (contraseña inicial en `Authservice`); cámbiela o cree una cuenta propia desde el login.
Las cuentas nuevas piden un usuario de 3 a 20 caracteres y una contraseña de al menos 8 caracteres con una letra y un número.

## Flujo de trabajo (GitFlow)

Ramas `main` (estable), `develop` (integración) y una rama `feature/*` por integrante y por fase
(`feature/gui-services-main`, `feature/domain-persistence` y `feature/phase-2`); la fase 3 se trabajó en la rama `phase-3`. Detalle en [`team.md`](team.md).

## Equipo

Ver [`team.md`](team.md).