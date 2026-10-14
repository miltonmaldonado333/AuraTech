# Sistema de Gesti�n del Club Deportivo Creatina-Boys

Una aplicaci�n de escritorio en Java estructurada bajo una **Arquitectura de 4 Capas** construida con Apache Maven y Java Swing para la gesti�n de deportistas, asignaci�n de disciplinas, evaluaci�n de tarifas por edad y pago de mensualidades en el **Club Deportivo Creatina-Boys**.

---

## Caracter�sticas

- **Gesti�n de Deportistas (CRUD):** Registrar, actualizar, consultar y eliminar deportistas.
- **Asignaci�n de Disciplina:** Atributos espec�ficos para Futbolistas (posici�n de juego) y Basquetbolistas (estatura).
- **Categorizaci�n Autom�tica por Edad:** Clasificaci�n y c�lculo autom�tico de la tarifa mensual seg�n la edad:
  - **Ni�os (5-12 a�os):** $50.000 COP
  - **J�venes (13-17 a�os):** \.000 COP
  - **Adultos (18+ a�os):** $100.000 COP
- **Arquitectura de 4 Capas:** Desacoplamiento total entre:
  1. **Capa de Presentaci�n / UI:** \com.creatinaboys.Ui\ (Formularios e interfaz Swing).
  2. **Capa de Servicios / L�gica:** \com.creatinaboys.service\ (Reglas de negocio y tarifas).
  3. **Capa de Repositorio / Persistencia:** \com.creatinaboys.Repository\ (Acceso a datos y archivos).
  4. **Capa de Modelo / Dominio:** \com.creatinaboys.model\ (Entidades y objetos del sistema).

---

## Estructura del Proyecto

\\\ ext
AuraTech/
+-- docs/
� +-- IAUsage/
� � +-- leader.md
� � +-- developer1.md
� +-- analysis.md
� +-- class-diagram.md
� +-- hierarchy-diagram.md
� +-- layers-diagram.md
+-- src/
� +-- main/
� +-- java/
� +-- com/
� +-- creatinaboys/
� +-- model/
� +-- Repository/
� +-- service/
� +-- Ui/
� +-- Main.java
+-- .gitignore
+-- pom.xml
+-- README.md
+-- team.md
\\\

---

## Requisitos Previos e Instalaci�n

1. **JDK 17** o superior instalado.
2. **Apache Maven 3.8+** instalado.
3. Clonar el repositorio:
   \\\ash
   git clone https://github.com/miltonmaldonado333/AuraTech.git
   cd AuraTech
   \\\
4. Compilar y ejecutar la aplicaci�n:
   \\\ash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.creatinaboys.Main"
   \\\
   "@

# 2. Actualizar docs/analysis.md en espa�ol (4 Capas)

Set-Content -Path "docs\analysis.md" -Value @"

# Documento de Requerimientos y An�lisis del Proyecto

## 1. Planteamiento del Problema

El Club Deportivo Creatina-Boys actualmente lleva el control de membres�as de atletas, inscripciones por disciplina (F�tbol y Baloncesto) y cobros mensuales de forma manual. Esto genera una asignaci�n incorrecta de categor�as por edad, inconsistencias en el cobro de tarifas y falta de reportes consolidados.

## 2. Reglas de Negocio (Matriz de Tarifas)

| Categor�a   | Rango de Edad | Tarifa Mensual (COP) |
| :---------- | :------------ | :------------------- |
| **Ni�os**   | 5 � 12 a�os   | \.000 COP            |
| **J�venes** | 13 � 17 a�os  | $70.000 COP          |
| **Adultos** | 18+ a�os      | \.000 COP            |

## 3. Requerimientos Funcionales (RF)

- **RF1 - Inscripci�n de Deportistas:** El sistema debe permitir crear, consultar, actualizar y eliminar registros de deportistas (Identificaci�n, Nombre Completo, Edad, Tel�fono).
- **RF2 - Asignaci�n de Disciplina:** El sistema debe permitir asignar deportistas a F�tbol o Baloncesto, capturando atributos espec�ficos de cada deporte (ej. Posici�n de Juego en F�tbol, Estatura en Baloncesto).
- **RF3 - Evaluaci�n Autom�tica de Edad:** El sistema debe determinar autom�ticamente la categor�a de edad del deportista y asignar la tarifa de suscripci�n mensual correspondiente.
- **RF4 - Gesti�n de Cobros:** El sistema debe realizar el seguimiento del estado de pago de las mensualidades y mostrar tablas de resumen.

## 4. Requerimientos No Funcionales (RNF)

- **RNF1 - Plataforma:** Desarrollado en Java (JDK 17) utilizando Apache Maven para la gesti�n de construcci�n.
- **RNF2 - Interfaz de Usuario:** Construida usando Java Swing (\JFrame\, \JTable\, \JOptionPane\) dentro del paquete \com.creatinaboys.Ui\.
- **RNF3 - Arquitectura de Software:** Estricto cumplimiento de la **Arquitectura de 4 Capas** (\Ui\, \service\, \Repository\, \model\).
- **RNF4 - Manejo de Excepciones:** Excepciones personalizadas del dominio para la validaci�n de datos y errores de repositorio.
