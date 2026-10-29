# Sistema de Gestión del Club Deportivo Creatina-Boys (AuraTech)

Aplicación de escritorio en Java (Swing + Maven) para gestionar deportistas, disciplinas,
categorías por edad y mensualidades del **Club Deportivo Creatina-Boys**.

Proyecto de aula — Universidad Popular del Cesar (UPC), Programación Computacional III.

---

## Características

- **Gestión de deportistas:** registro y consulta con búsqueda por documento, nombre o disciplina. Persistencia en archivo de texto.
- **Disciplinas:** atributos propios de cada deporte. Futbolistas (posición de juego) y basquetbolistas (estatura en metros). Una misma persona puede estar en varias disciplinas (clave documento + disciplina), con pagos y saldo independientes.
- **Control de pagos:** pagos totales o parciales de la mensualidad, estado del mes (Pagado, Parcial, Pendiente) e historial; el monto se valida antes de registrarse.
- **Acceso:** inicio de sesión, registro de usuarios y cierre de sesión; contraseñas con sal y PBKDF2, bloqueo de 15 s tras 5 intentos fallidos.
- **Inicio y reportes:** panel con indicadores del club y reportes financieros (proyectado, recaudado, pendiente, por disciplina y categoría).
- **Categoría y tarifa automáticas por edad:**

  | Categoría | Edad          | Tarifa mensual (COP) |
  | :-------- | :------------ | :------------------- |
  | Niños     | 5 a 12 años   | $50.000              |
  | Jóvenes   | 13 a 17 años  | $70.000              |
  | Adultos   | 18 años o más | $100.000             |

  Los menores de 5 años se rechazan con `InvalidAgeException`.
- **Validación de datos de entrada** centralizada en `DataValidator` y aplicada en `AthleteService.registerAthlete` (documento de 6 a 12 dígitos, nombre solo con letras, teléfono de 7 a 10 dígitos, dorsal de 0 a 99, estatura de 0,5 a 2,5 m), además de la unicidad de documento y dorsal por disciplina.
- **Persistencia robusta:** archivos UTF-8, escritura mediante archivo temporal con copia `.bak`, y tolerancia a líneas dañadas.

## Estado del proyecto

| Módulo                                                                  | Estado                                                        |
| :---------------------------------------------------------------------- | :------------------------------------------------------------ |
| Modelo, excepciones y validación                                        | Implementado                                                  |
| Repositorios (deportistas, entrenadores, pagos, usuarios, salud, disciplinas) | Implementado                                            |
| Servicios de deportistas, facturación, pagos, entrenadores, disciplinas y autenticación | Implementado y conectado a la UI            |
| Pantallas: Inicio, Deportistas, Registrar, Categorías, Disciplinas, Entrenadores, Pagos y Reportes | Implementado                    |
| Login, registro de usuarios y cierre de sesión                          | Implementado                                                  |
| Control de pagos (RF4) con validación de monto                          | Implementado                                                  |
| Un deportista en varias disciplinas (RF2.1)                             | Implementado                                                  |
| Servicio de salud (RF6)                                                 | Implementado, **sin pantalla** y sin conectar en `Main`       |
| Edición de datos personales de un deportista (RF1)                      | Pendiente (el repositorio ya lo soporta)                      |
| Persistencia del catálogo de disciplinas (RF2)                          | Pendiente (`FileDisciplineRepository` no está inyectado en `Main`) |

## Arquitectura de 4 capas

Desacoplamiento entre capas mediante interfaces (DIP), más dos paquetes transversales:

1. **Presentación / UI:** `com.creatinaboys.ui` (ventana principal, paneles y componentes Swing).
2. **Servicios / Lógica:** `com.creatinaboys.service` (reglas de negocio, tarifas y categorías).
3. **Repositorio / Persistencia:** `com.creatinaboys.repository` (acceso a datos y archivos).
4. **Modelo / Dominio:** `com.creatinaboys.model` (entidades y enumerados).

Transversales: `com.creatinaboys.exceptions` (excepciones del dominio) y `com.creatinaboys.validation` (validación de entradas).

Los diagramas están en [`docs/`](docs/).

## Estructura del proyecto

```text
AuraTech/
|-- docs/
|   |-- IAUsage/
|   |   |-- developer1.md
|   |   `-- leader.md
|   |-- Analisys.md
|   |-- class-diagram.md
|   |-- hierarchy-diagram.md
|   |-- Layers_diagram.md
|   `-- Proyecto_de_Aula_Creatina_Boys_actualizado.docx
|-- src/main/java/com/creatinaboys/
|   |-- exceptions/
|   |-- model/
|   |-- repository/
|   |-- service/
|   |-- ui/
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

## Instalación y ejecución

```bash
git clone https://github.com/miltonmaldonado333/AuraTech.git
cd AuraTech
mvn clean compile
mvn exec:java
```

`mvn exec:java` usa la clase principal `com.creatinaboys.Main` configurada en el `pom.xml`.

## Datos

Los datos se guardan en la carpeta `data/` junto al lugar desde donde se ejecuta la aplicación
(se crea automáticamente):

| Archivo                    | Contenido                  |
| :------------------------- | :------------------------- |
| `data/athletes.txt`        | Deportistas                |
| `data/coaches.txt`         | Entrenadores               |
| `data/payments.txt`        | Pagos de mensualidades     |
| `data/users.txt`           | Usuarios (sal y hash, sin contraseña en claro) |
| `data/disciplines.txt`     | Catálogo de disciplinas    |
| `data/health_records.txt`  | Fichas de salud y lesiones |

Son archivos de texto UTF-8 con campos separados por `;`. Los caracteres `;`, `#`, `~` y `\` dentro de un
valor se escapan con `\`. Antes de cada escritura se conserva la versión anterior como `<archivo>.bak`.
Una línea con formato inválido se omite y se informa por la consola, sin impedir la carga del resto.

## Acceso inicial

La primera vez que se ejecuta se crea el usuario `admin` (contraseña inicial en `Authservice`); cámbiela o cree una cuenta propia desde el login.

## Equipo

Ver [`team.md`](team.md).