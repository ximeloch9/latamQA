# ✈️ Latam Airlines QA POC Ecosystem

¡Bienvenido al ecosistema unificado de pruebas automatizadas y generación de datos para **Latam Airlines**! Este repositorio consolida las herramientas de desarrollo y los frameworks de automatización diseñados para garantizar la calidad y robustez de los flujos de negocio del portal de la aerolínea.

---

## 📁 Estructura del Workspace

El repositorio está organizado en módulos independientes que cooperan para ejecutar pruebas end-to-end de manera autosuficiente:

```text
LatamQAPOC/
├── data-generator/          # Generador robusto de datos ficticios persistidos en SQLite / CSV
│   ├── src/                 # Lógica de negocio (Java 17, Patrones, SOLID)
│   └── pom.xml
│
├── latam-automation/        # Framework de automatización de pruebas UI (Serenity BDD)
│   ├── src/                 # Escenarios Gherkin, Tareas, UI y Step Definitions
│   └── pom.xml
│
├── agent.md                 # Documentación e instrucciones para asistentes de IA en el proyecto
├── localizadoresDOM.md      # Catálogo y estrategia de selectores del portal Latam
├── prueba.md                # Documento de anotaciones del evaluador / QA
└── .gitignore               # Configuración global de exclusiones (IDE, target, bases de datos)
```

---

## 🛠️ Requisitos Previos

Antes de ejecutar cualquiera de los módulos, asegúrate de tener instalado en tu máquina local:
* **Java Development Kit (JDK) 11 o 17**
* **Apache Maven 3.6+**
* Un navegador **Google Chrome** actualizado (para las pruebas de UI)

---

## 🚀 Guía de Inicio Rápido (Flujo de Pruebas End-to-End)

El flujo de pruebas completo requiere generar primero un lote de datos válidos de prueba para que luego el framework de automatización los consuma dinámicamente desde la base de datos o archivo plano CSV.

### Paso 1: Generar Datos de Prueba (`data-generator`)

El generador crea usuarios ficticios (personas naturales y empresas) validados con reglas de negocio estrictas de aerolíneas, guardándolos en una base de datos local SQLite y exportando un reporte CSV.

1. Navega al módulo de generación:
   ```bash
   cd data-generator
   ```
2. Compila el proyecto e instala dependencias:
   ```bash
   mvn clean install
   ```
3. Ejecuta la aplicación e inicia el menú interactivo por consola:
   ```bash
   mvn exec:java -Dexec.mainClass="com.latam.datagenerator.Main"
   ```
   *Elige la opción **[1]** para generar nuevos registros en lote. Esto creará el archivo de base de datos `data-generator.db` en el directorio de trabajo y exportará el archivo `output/datos_latam.csv`.*

---

### Paso 2: Ejecutar Automatización de UI (`latam-automation`)

El framework consume dinámicamente los registros de pasajeros generados en el paso anterior para simular compras de vuelos en el portal de Latam Airlines Colombia usando el patrón **Screenplay** y **Serenity BDD**.

1. Navega al módulo de automatización:
   ```bash
   cd ../latam-automation
   ```
2. Ejecuta la suite de pruebas automatizadas:
   ```bash
   mvn clean verify
   ```
   *Por defecto, la suite buscará los datos en `../data-generator/data-generator.db` e interactuará con la web en modo headless.*

3. **Ver Reportes de Pruebas:**
   Una vez concluida la ejecución, abre el reporte interactivo generado por Serenity en tu navegador:
   ```text
   latam-automation/target/site/serenity/index.html
   ```

---

## 📦 Descripción de Módulos

### 1. [Data Generator](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator)
* **Propósito:** Creación y gestión de datos de prueba para QA.
* **Características:**
  * Implementación rigurosa de patrones de diseño **Factory**, **Builder** y **Singleton**.
  * Arquitectura basada en los principios **SOLID**, **DRY**, **KISS** y **YAGNI**.
  * Soporte de generación multihilo concurrente (paralelismo automático a partir de 100 registros).
  * Envío automático de reportes CSV por correo mediante SMTP.

### 2. [Latam Automation](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation)
* **Propósito:** Automatización del flujo de compra y reserva en la web de Latam.
* **Características:**
  * Uso de **Serenity BDD** + **Cucumber** para una especificación viva y legible.
  * Patrón **Screenplay** para separar las tareas de negocio de la interacción técnica.
  * Estrategia de selectores estables documentados en [localizadoresDOM.md](file:///Users/Marlopch/Documents/LatamQAPOC/localizadoresDOM.md).

---

## ⚠️ Advertencia sobre Cortafuegos (WAF)

> [!WARNING]
> Dado que el framework interactúa con el portal público en producción (`https://www.latamairlines.com/co/es`), las ejecuciones repetitivas o concurrentes desde servidores cloud/headless pueden activar el firewall de seguridad de Latam Airlines, resultando en respuestas **403 Access Denied**.
>
> Para mitigar esto:
> - Ejecuta las pruebas localmente con un navegador visible (desactivando `headless` en [serenity.conf](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/resources/serenity.conf)).
> - De ser posible, apunta las pruebas a entornos controlados de desarrollo de la aerolínea (Sandbox/Staging).
