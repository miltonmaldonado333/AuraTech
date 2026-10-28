# Sistema de Gestión del Club Deportivo Creatina-Boys (AuraTech)

Aplicación de escritorio en Java (Swing + Maven) para gestionar deportistas, disciplinas,
categorías por edad y mensualidades del **Club Deportivo Creatina-Boys**.

Proyecto de aula — Universidad Popular del Cesar (UPC), Programación Computacional III.

---

## Características

- **Gestión de deportistas:** registro y consulta con búsqueda por documento, nombre o disciplina. Persistencia en archivo de texto.
- **Disciplinas:** atributos propios de cada deporte. Futbolistas (posición de juego) y basquetbolistas (estatura en metros).
- **Categoría y tarifa automáticas por edad:**

  | Categoría | Edad          | Tarifa mensual (COP) |
  | :-------- | :------------ | :------------------- |
  | Niños     | 5 a 12 años   | $50.000              |
  | Jóvenes   | 13 a 17 años  | $70.000              |
  | Adultos   | 18 años o más | $100.000             |

  Los menores de 5 años se rechazan con `InvalidAgeException`.
- **Validación de datos de entrada** centralizada en `DataValidator`.
- **Persistencia robusta:** archivos UTF-8, escritura mediante archivo temporal con copia `.bak`, y tolerancia a líneas dañadas.

## Estado del proyecto

| Módulo                                   | Estado                                                             |
| :--------------------------------------- | :----------------------------------------------------------------- |
| Modelo, excepciones y validación         | Implementado                                                       |
| Repositorios (atletas, entrenadores, salud) | Implementado                                                    |
| Servicios de atletas y facturación       | Implementado y conectado a la UI                                   |
| Servicios de entrenadores, salud y disciplinas | Implementados, **sin pantalla** en la UI                     |
| Pantallas Deportistas y Registrar        | Implementadas                                                      |
| Inicio, Categorías, Disciplinas, Entrenadores, Pagos, Reportes, Configuración | Pendiente (pantallas de relleno) |
| Control de pagos (RF4)                   | Pendiente: aún no se registra el estado de pago de cada mensualidad |
| Actualizar / eliminar deportistas desde la UI | Pendiente (el repositorio y el servicio ya lo soportan)       |

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
|   |-- analysis.md
|   |-- class-diagram.md
|   |-- hierarchy-diagram.md
|   |-- layers-diagram.md
|   `-- Proyecto_de_Aula_Creatina_Boys.docx
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

| Archivo                    | Contenido                |
| :------------------------- | :----------------------- |
| `data/athletes.txt`        | Deportistas              |
| `data/coaches.txt`         | Entrenadores             |
| `data/health_records.txt`  | Fichas de salud y lesiones |

Son archivos de texto UTF-8 con campos separados por `;`. Los caracteres `;`, `#`, `~` y `\` dentro de un
valor se escapan con `\`. Antes de cada escritura se conserva la versión anterior como `<archivo>.bak`.
Una línea con formato inválido se omite y se informa por la consola, sin impedir la carga del resto.

## Equipo

Ver [`team.md`](team.md).