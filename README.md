# Sistema de Gesti�n del Club Deportivo Creatina-Boys

Una aplicación de escritorio en Java estructurada bajo una **Arquitectura de 4 Capas** construida con Apache Maven y Java Swing para la gestión de deportistas, asignaci�n de disciplinas, evaluación de tarifas por edad y pago de mensualidades en el **Club Deportivo Creatina-Boys**.

---

## Características

- **Gestión de Deportistas (CRUD):** Registrar, actualizar, consultar y eliminar deportistas.
- **Asignación de Disciplina:** Atributos específicos para Futbolistas (posición de juego) y Basquetbolistas (estatura).
- **Categorización Automática por Edad:** Clasificación y cálculo automático de la tarifa mensual según la edad:
  - **Niños (5-12 años):** $50.000 COP
  - **Jovenes (13-17 años):** 70.000 COP
  - **Adultos (18 + años):** $100.000 COP
- **Arquitectura de 4 Capas:** Desacoplamiento total entre:
  1. **Capa de Presentacion / UI:** \com.creatinaboys.Ui\ (Formularios e interfaz Swing).
  2. **Capa de Servicios / Logica:** \com.creatinaboys.service\ (Reglas de negocio y tarifas).
  3. **Capa de Repositorio / Persistencia:** \com.creatinaboys.Repository\ (Acceso a datos y archivos).
  4. **Capa de Modelo / Dominio:** \com.creatinaboys.model\ (Entidades y objetos del sistema).

---

## Estructura del Proyecto

```text
AuraTech/
|-- docs/
|   |-- IAUsage/
|   |   |-- developer1.md
|   |   `-- leader.md
|   |-- analysis.md
|   |-- class-diagram.md
|   |-- hierarchy-diagram.md
|   `-- layers-diagram.md
|-- src/
|   `-- main/
|       `-- java/
|           `-- com/
|               `-- creatinaboys/
|                   |-- model/
|                   |-- repository/
|                   |-- service/
|                   |-- ui/
|                   `-- Main.java
|-- .gitignore
|-- pom.xml
|-- README.md
`-- team.md
---
```

## Requisitos Previos e Instalacion

1. **JDK 17** o superior instalado.
2. **Apache Maven 3.8+** instalado.
3. Clonar el repositorio:
   \\\ash
   git clone https://github.com/miltonmaldonado333/AuraTech.git
   cd AuraTech
   \\\
4. Compilar y ejecutar la aplicacion:
   \\\ash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.creatinaboys.Main"
   \\\
   "@

# 2. Actualizar docs/analysis.md en español (4 Capas)

Set-Content -Path "docs\analysis.md" -Value @"

# Documento de Requerimientos y Analisis del Proyecto

## 1. Planteamiento del Problema

El Club Deportivo Creatina-Boys actualmente lleva el control de membresias de atletas, inscripciones por disciplina (Futbol y Baloncesto) y cobros mensuales de forma manual. Esto genera una asignacion incorrecta de categorias por edad, inconsistencias en el cobro de tarifas y falta de reportes consolidados.

## 2. Reglas de Negocio (Matriz de Tarifas)

| Categoria   | Rango de Edad | Tarifa Mensual (COP) |
| :---------- | :------------ | :------------------- |
| **Niños**   | 5 a 12 años   | $50.000 COP            |
| **Jovenes** | 13 a 17 años  | $70.000 COP          |
| **Adultos** | 18+ años      | 110.000 COP            |

## 3. Requerimientos Funcionales (RF)

- **RF1 - Inscripcion de Deportistas:** El sistema debe permitir crear, consultar, actualizar y eliminar registros de deportistas (Identificacion, Nombre Completo, Edad, Telefono).
- **RF2 - Asignacion de Disciplina:** El sistema debe permitir asignar deportistas a Futbol o Baloncesto, capturando atributos especificos de cada deporte (ej. Posicion de Juego en Futbol, Estatura en Baloncesto).
- **RF3 - Evaluacion Automatica de Edad:** El sistema debe determinar automaticamente la categoria de edad del deportista y asignar la tarifa de suscripcion mensual correspondiente.
- **RF4 - Gestion de Cobros:** El sistema debe realizar el seguimiento del estado de pago de las mensualidades y mostrar tablas de resumen.

## 4. Requerimientos No Funcionales (RNF)

- **RNF1 - Plataforma:** Desarrollado en Java (JDK 17) utilizando Apache Maven para la gestion de construccion.
- **RNF2 - Interfaz de Usuario:** Construida usando Java Swing (\JFrame\, \JTable\, \JOptionPane\) dentro del paquete \com.creatinaboys.Ui\.
- **RNF3 - Arquitectura de Software:** Estricto cumplimiento de la **Arquitectura de 4 Capas** (\Ui\, \service\, \Repository\, \model\).
- **RNF4 - Manejo de Excepciones:** Excepciones personalizadas del dominio para la validacion de datos y errores de repositorio.
