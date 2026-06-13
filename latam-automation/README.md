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
   *Se recomienda limpiar el perfil de prueba para evitar cookies corruptas o bloqueadas y luego ejecutar:*
   ```bash
   rm -rf .chrome-profile && mvn clean verify
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

---

## Estrategias Anti-Bot y Configuración de Navegador

Para mitigar el bloqueo por detección de bots ("La búsqueda está tardando más de lo normal") en el portal de LATAM, el framework implementa las siguientes estrategias en `serenity.conf` y los steps:

1. **Perfil Persistente (`--user-data-dir`)**:
   - Evita el uso del modo incógnito por defecto. En modo incógnito, la CDN (Akamai) bloquea con mayor agresividad al detectar sesiones 100% limpias (sin cookies de consentimiento previas ni historial).
   - El perfil persistente guarda el estado y cookies de sesiones exitosas. Si deseas limpiar el caché manualmente, puedes borrar el directorio antes de iniciar:
     ```bash
     rm -rf .chrome-profile
     ```
2. **User-Agent Dinámico (Evitar v126 fijo)**:
   - **No debes usar un User-Agent fijo desactualizado (como v126)** si tu Chrome local se ha actualizado a una versión superior (por ejemplo, v149).
   - Los sistemas anti-bot comparan las capacidades reales de la API de JavaScript de tu motor de Chrome con la firma declarada en el User-Agent. Si hay inconsistencia, te identifican inmediatamente como bot.
   - Al omitir el argumento `--user-agent` en la configuración de Serenity, Chrome autogenera su User-Agent nativo real, asegurando que coincida al 100% con tu versión del navegador.
3. **Inyección de Sigilo (CDP Stealth)**:
   - Se inyecta un script en el navegador antes de cada carga de página usando Chrome DevTools Protocol (CDP) para enmascarar `navigator.webdriver = false` y emular variables nativas de Chrome.

