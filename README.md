# ✈️ Latam Airlines QA POC Ecosystem

¡Bienvenido al ecosistema unificado de pruebas automatizadas y generación de datos para **Latam Airlines**! Este repositorio consolida las herramientas de QA Automation diseñadas para garantizar la calidad y robustez de los flujos de negocio del portal de la aerolínea.

> **Reglas del agente IA:** Todo el código de este workspace debe cumplir con las directrices definidas en [`agent.md`](file:///Users/Marlopch/Documents/LatamQAPOC/agent.md) (SOLID, DRY, KISS, YAGNI, SLF4J, Java 17, commits convencionales).

---

## 📁 Estructura del Workspace

```text
LatamQAPOC/
├── data-generator/          # Generador robusto de datos ficticios (SQLite + CSV)
│   ├── src/                 # Lógica de negocio (Java 17, Factory, Builder, Singleton, SOLID)
│   └── pom.xml
│
├── latam-automation/        # Framework de automatización UI (Serenity BDD + Screenplay)
│   ├── src/                 # Escenarios Gherkin (CP3 activo), Tareas, UI, StepDefinitions
│   └── pom.xml
│
├── agent.md                 # Reglas vinculantes para agentes IA y desarrolladores
├── localizadoresDOM.md      # Catálogo de selectores DOM del portal Latam (actualizado CP3)
├── prueba.md                # Anotaciones del evaluador / QA
└── .gitignore               # Exclusiones globales (IDE, target, bases de datos)
```

---

## 🛠️ Requisitos Previos

Antes de ejecutar cualquiera de los módulos, asegúrate de tener instalado:

| Herramienta | Versión mínima | Notas |
|---|---|---|
| **Java JDK** | 17 | Target oficial del proyecto (§6 agent.md) |
| **Apache Maven** | 3.6+ | Gestor de dependencias |
| **Google Chrome** | ≥ 120 | Para las pruebas de UI (sin User-Agent fijo) |

---

## 🚀 Guía de Inicio Rápido

El flujo completo requiere primero generar datos de prueba y luego ejecutar la automatización.

### Paso 1 — Generar Datos de Prueba (`data-generator`)

```bash
cd data-generator
mvn clean install
mvn exec:java -Dexec.mainClass="com.latam.datagenerator.Main"
```

Elige la opción **[1]** para generar un lote de registros. Esto creará:
- `data-generator/data-generator.db` — Base de datos SQLite con usuarios generados
- `data-generator/output/datos_latam.csv` — Exportación plana CSV

### Paso 2 — Ejecutar Automatización UI (`latam-automation`)

```bash
cd ../latam-automation

# Limpiar perfil Chrome (recomendado si hay bloqueos WAF) y ejecutar
rm -rf .chrome-profile && mvn clean verify
```

### Paso 3 — Ver Reporte Serenity

```text
latam-automation/target/site/serenity/index.html
```

---

## 📦 Descripción de Módulos

### 1. [Data Generator](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/README.md)

* **Propósito:** Creación y gestión de datos de prueba para QA (personas naturales y empresas).
* **Características clave:**
  * Patrones de diseño: **Factory**, **Builder**, **Singleton**.
  * Principios: **SOLID**, **DRY**, **KISS**, **YAGNI**.
  * Generación paralela multihilo para lotes ≥ 100 registros (`ExecutorService`).
  * Exportación CSV y envío por correo SMTP (`JavaMail API`).
  * Persistencia en SQLite con validación de unicidad en memoria y BD.

### 2. [Latam Automation](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/README.md)

* **Propósito:** Automatización end-to-end del flujo de reserva y compra en el portal Latam.
* **Escenarios:**
  * ✅ **CP3** — Validación de datos de pasajero menor de edad en Checkout *(activo y pasando)*
  * 🔲 CP1 — Búsqueda de vuelo nacional adulto *(comentado / pendiente)*
  * 🔲 CP2 — Vuelo internacional multilenguaje *(comentado / pendiente)*
* **Stack técnico:** Serenity BDD + Cucumber + Screenplay Pattern.
* **Selectores estables:** Documentados en [`localizadoresDOM.md`](file:///Users/Marlopch/Documents/LatamQAPOC/localizadoresDOM.md).
* **Dato clave:** Los formularios React requieren escritura carácter a carácter con delays (ver `IngresarPasajero.escribirComoHumano()`).

---

## 🗂️ Documentos de referencia

| Documento | Propósito |
|---|---|
| [`agent.md`](file:///Users/Marlopch/Documents/LatamQAPOC/agent.md) | Reglas del agente IA: SOLID, KISS, DRY, YAGNI, naming, commits |
| [`localizadoresDOM.md`](file:///Users/Marlopch/Documents/LatamQAPOC/localizadoresDOM.md) | Catálogo de selectores DOM del portal (IDs, XPaths, data-testid) |
| [`prueba.md`](file:///Users/Marlopch/Documents/LatamQAPOC/prueba.md) | Notas y anotaciones del equipo QA evaluador |

---

## ⚠️ Advertencia sobre Cortafuegos (WAF)

> [!WARNING]
> El framework interactúa con el portal público en producción (`https://www.latamairlines.com/co/es`). Las ejecuciones repetitivas o concurrentes desde servidores cloud/headless pueden activar el WAF de Latam Airlines con respuestas **403 Access Denied**.
>
> Para mitigar esto:
> - Ejecutar localmente con navegador visible (sin `headless`) en `serenity.conf`.
> - Limpiar el perfil de Chrome antes de ejecutar: `rm -rf .chrome-profile`.
> - Apuntar las pruebas a entornos Staging/Sandbox cuando estén disponibles.

---

## 📝 Convención de Commits (§11 agent.md)

```
<tipo>(<scope>): <descripción en español, imperativo>

feat(latam-automation): agregar validación de menor en checkout CP3
fix(ingresar-pasajero): corregir año de nacimiento CHD_1 a 2020
refactor(checkout-page): extraer selectores dinámicos por sufijo
chore(pom): actualizar compiler target a Java 17
docs(readme): actualizar estado de CP3 y reglas agent.md
```

Tipos válidos: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`, `style`.
