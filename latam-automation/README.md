# Latam QA Automation Framework

Este proyecto contiene el framework de automatización de pruebas de UI diseñado para el portal de Latam Airlines Colombia, utilizando **Serenity BDD, Screenplay y Cucumber**.

## Arquitectura y Componentes
El proyecto sigue el patrón **Screenplay** estructurando las interacciones del actor:
* **Features** (`src/test/resources/features/`): Historias de usuario y escenarios en lenguaje Gherkin.
* **UI/Page Objects** (`com.latam.automation.ui`): Definición de selectores DOM estables.
* **Tasks** (`com.latam.automation.tasks`): Acciones de negocio de alto nivel (`BuscarVuelo`, `SeleccionarVuelo`, `IngresarPasajero`).
* **Step Definitions** (`com.latam.automation.stepdefinitions`): Enlace entre los escenarios Gherkin y las tareas.
* **Utilities** (`com.latam.automation.util`): Conexión dinámica con SQLite (`data-generator.db`) y CSV para consumir datos generados en la Parte 1.

---

## Requisitos Previos
* **Java 11 o 17** instalado y configurado en tus variables de entorno.
* **Maven 3.6+** instalado globalmente.

---

## Instrucciones de Ejecución

Para iniciar la ejecución de las pruebas:

1. **Abrir una terminal** en la raíz del módulo de automatización:
   ```bash
   cd scratch/latam-automation
   ```

2. **Ejecutar el comando de Maven**:
   ```bash
   mvn clean verify
   ```
   *Nota: Por defecto, la suite está configurada para ejecutarse usando Chrome en modo **headless** (según se define en `src/test/resources/serenity.conf`).*

3. **Ver los Reportes**:
   Una vez terminada la ejecución, Serenity genera un reporte interactivo detallado con logs paso a paso y capturas de pantalla. Puedes consultarlo abriendo el siguiente archivo en tu navegador:
   ```text
   scratch/latam-automation/target/site/serenity/index.html
   ```

---

## Consideraciones de Seguridad (WAF)
> [!WARNING]
> Dado que este desarrollo es una Prueba de Concepto que interactúa con el portal público en producción (`https://www.latamairlines.com/co/es`), las ejecuciones automatizadas continuas o concurrentes desde servidores cloud/headless pueden ser bloqueadas por el firewall de Latam (WAF), mostrando errores de **Access Denied**.
>
> Para mitigar esto y ver las interacciones completas, se recomienda:
> - Ejecutar en tu entorno local sin la opción `--headless` en `serenity.conf`.
> - Apuntar las pruebas a un ambiente de desarrollo (Staging o Sandbox) provisto por la aerolínea con las restricciones de seguridad deshabilitadas.
