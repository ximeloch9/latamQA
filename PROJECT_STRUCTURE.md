# INVENTARIO TÉCNICO Y AUDITORÍA DE CALIDAD DE CÓDIGO

| Metadato | Valor |
| :--- | :--- |
| **Última Actualización** | 2026-06-11 |
| **Fase de Desarrollo** | Sprint 1 (Prueba de Concepto - Shift Right) |
| **Versión del Proyecto** | v1.0.0-POC |

---

## 1. ÁRBOL DE CARPETAS (Tree)

```text
LatamQAPOC/                                            ← Raíz del Workspace
├── data-generator/                                    ← [Sprint 1] Módulo de generación de datos
│   ├── src/
│   │   └── main/
│   │       ├── java/com/latam/datagenerator/
│   │       │   ├── builder/
│   │       │   │   └── UserBuilder.java               ← Patrón Builder para AbstractUser
│   │       │   ├── factory/
│   │       │   │   ├── UserFactory.java               ← Factory Method para tipos de usuario
│   │       │   │   └── UserTypeFactory.java           ← Resolutor de enums
│   │       │   ├── model/
│   │       │   │   ├── AbstractUser.java              ← Clase base (Abstracción OOP / SOLID-O)
│   │       │   │   ├── CompanyUser.java               ← Especialización Persona Jurídica
│   │       │   │   ├── NaturalPersonUser.java         ← Especialización Persona Natural
│   │       │   │   ├── Documentable.java              ← Contrato generación documento
│   │       │   │   ├── Validatable.java               ← Contrato validación de negocio
│   │       │   │   ├── UserRecord.java                ← DTO inmutable para mapeo
│   │       │   │   └── UserType.java                  ← Clasificador del dominio
│   │       │   ├── repository/
│   │       │   │   ├── DatabaseManager.java           ← Singleton de conexión SQLite
│   │       │   │   └── UserRepository.java            ← DAO de usuarios (SOLID-S / JDBC)
│   │       │   ├── service/
│   │       │   │   ├── DataGeneratorService.java      ← Motor de generación secuencial y paralelo
│   │       │   │   └── DocumentGeneratorService.java  ← Lógica de IDs para aerolíneas
│   │       │   ├── util/
│   │       │   │   ├── CsvExporter.java               ← Exportador CSV
│   │       │   │   └── MailSender.java                ← Envío SMTP de reportes
│   │       │   └── Main.java                          ← CLI interactivo
│   │       └── resources/
│   │           ├── config.properties                  ← Configuración SMTP y BD
│   │           └── logback.xml                        ← Registro SLF4J
│   └── pom.xml                                        ← Configuración Maven (Faker, SQLite)
│
├── latam-automation/                                  ← [Sprint 1] Módulo de automatización UI
│   ├── src/
│   │   └── test/
│   │       ├── java/com/latam/automation/
│   │       │   ├── stepdefinitions/
│   │       │   │   └── BuscarVueloStepDefinitions.java ← Glue code Cucumber
│   │       │   ├── tasks/
│   │       │   │   ├── BuscarVuelo.java               ← Tarea de búsqueda con limpieza de overlays
│   │       │   │   ├── IngresarPasajero.java          ← Completado de Checkout
│   │       │   │   └── SeleccionarVuelo.java          ← Selección de tarifa económica
│   │       │   ├── ui/
│   │       │   │   ├── LatamCheckoutPage.java         ← Selectores del Checkout
│   │       │   │   └── LatamSearchPage.java           ← Selectores del Home buscador
│   │       │   ├── util/
│   │       │   │   └── DataHelper.java                ← Lector híbrido BD/CSV
│   │       │   └── CucumberTestSuite.java             ← Runner de la suite
│   │       └── resources/
│   │           ├── features/
│   │           │   └── buscar_vuelo.feature           ← Escenarios BDD (CP1, CP2, CP3)
│   │           ├── config.properties                  ← Variables globales
│   │           ├── overlays.properties                ← Selectores CSS de banners intrusivos
│   │           └── serenity.conf                      ← Configuración Webdriver
│   └── pom.xml                                        ← Maven Serenity / Screenplay
│
├── agent.md                                           ← Políticas de desarrollo IA
├── localizadoresDOM.md                                 ← Catálogo de selectores web
└── README.md                                          ← Instrucciones de ejecución
```

---

## 2. INVENTARIO DETALLADO POR FEATURE

### Feature: Generador de Datos (`data-generator`)

| # | Archivo | Líneas | Capa | Descripción | Estado |
|---|:---|:---:|:---:|:---|:---:|
| 1 | [Main.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/Main.java) | 204 | UI | CLI interactivo con menús de consola para el usuario. | 🔵 MENOR |
| 2 | [DataGeneratorService.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/service/DataGeneratorService.java) | 288 | Domain | Motor de generación lógica, unicidad y paralelismo. | 🔵 MENOR |
| 3 | [DocumentGeneratorService.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/service/DocumentGeneratorService.java) | 47 | Domain | Generador especializado de números de documento por tipo. | ✅ OK |
| 4 | [AbstractUser.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/model/AbstractUser.java) | 143 | Domain | Plantilla base abstracta con datos encapsulados de usuario. | ✅ OK |
| 5 | [NaturalPersonUser.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/model/NaturalPersonUser.java) | 98 | Domain | Especialización de usuario natural (adulto/menor). | ✅ OK |
| 6 | [CompanyUser.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/model/CompanyUser.java) | 81 | Domain | Especialización jurídica corporativa (sin apellidos). | ✅ OK |
| 7 | [UserBuilder.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/builder/UserBuilder.java) | 117 | Domain | Construcción fluida de objetos de usuario. | ✅ OK |
| 8 | [UserFactory.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/factory/UserFactory.java) | 49 | Domain | Instanciador polimórfico de clases según tipo. | ✅ OK |
| 9 | [UserRepository.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/repository/UserRepository.java) | 289 | Data | Operaciones CRUD y persistencia sobre base de datos SQLite. | 🔵 MENOR |
| 10 | [DatabaseManager.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/repository/DatabaseManager.java) | 97 | Data | Singleton gestor de conexiones JDBC. | ✅ OK |
| 11 | [CsvExporter.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/util/CsvExporter.java) | 58 | Util | Volcado de datos estructurados a archivos CSV. | ✅ OK |
| 12 | [MailSender.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/util/MailSender.java) | 129 | Util | Configuración y envío SMTP de reportes adjuntos. | ✅ OK |

### Feature: Automatización (`latam-automation`)

| # | Archivo | Líneas | Capa | Descripción | Estado |
|---|:---|:---:|:---:|:---|:---:|
| 1 | [BuscarVueloStepDefinitions.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/stepdefinitions/BuscarVueloStepDefinitions.java) | 188 | State | Glue code Cucumber que gestiona la interacción del actor. | ✅ OK |
| 2 | [BuscarVuelo.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/tasks/BuscarVuelo.java) | 186 | Domain | Tarea Screenplay de ingreso de parámetros y calendario. | ✅ OK |
| 3 | [IngresarPasajero.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/tasks/IngresarPasajero.java) | 59 | Domain | Tarea Screenplay de ingreso de datos del checkout. | ✅ OK |
| 4 | [SeleccionarVuelo.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/tasks/SeleccionarVuelo.java) | 36 | Domain | Tarea Screenplay de interacción en la lista de vuelos. | ✅ OK |
| 5 | [LatamSearchPage.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/ui/LatamSearchPage.java) | 76 | UI | Selectores y constantes de la página de inicio. | ✅ OK |
| 6 | [LatamCheckoutPage.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/ui/LatamCheckoutPage.java) | 31 | UI | Selectores de validación de compra y pasajeros. | ✅ OK |
| 7 | [DataHelper.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/util/DataHelper.java) | 162 | Util | Consumidor híbrido SQLite y CSV para inyección de datos. | 🔵 MENOR |

---

## 3. RESUMEN ESTADÍSTICO

### Métricas Generales
*   **Total de archivos evaluados:** 19 (excluyendo archivos auxiliares/config)
*   **Total de líneas de código evaluadas:** 2,254
*   **Archivos generados automáticamente:** 0
*   **Nuevos archivos del último sprint:** 19 (Sprint 1 POC completo)

### Top Archivos Más Grandes

| Archivo | Líneas | Límite Teórico | Líneas en Exceso | Estado |
|:---|:---:|:---:|:---:|:---:|
| [UserRepository.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/repository/UserRepository.java) | 289 | 200 | +89 | 🔵 MENOR |
| [DataGeneratorService.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/service/DataGeneratorService.java) | 288 | 200 | +88 | 🔵 MENOR |
| [Main.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/Main.java) | 204 | 200 | +4 | 🔵 MENOR |
| [BuscarVueloStepDefinitions.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/stepdefinitions/BuscarVueloStepDefinitions.java) | 188 | 200 | 0 | ✅ OK |
| [BuscarVuelo.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/tasks/BuscarVuelo.java) | 186 | 200 | 0 | ✅ OK |
| [DataHelper.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/util/DataHelper.java) | 162 | 150 | +12 | 🔵 MENOR |
| [AbstractUser.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/model/AbstractUser.java) | 143 | 150 | 0 | ✅ OK |
| [UserRecord.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/model/UserRecord.java) | 143 | 150 | 0 | ✅ OK |
| [MailSender.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/util/MailSender.java) | 129 | 150 | 0 | ✅ OK |
| [UserBuilder.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/builder/UserBuilder.java) | 117 | 150 | 0 | ✅ OK |

### Conteo de Violaciones

| Estado | Rango de Exceso | Cantidad | % del Total |
| :--- | :--- | :---: | :---: |
| 🔴 **CRÍTICO** | > +300% | 0 | 0% |
| 🟠 **GRAVE** | hasta +300% | 0 | 0% |
| 🟡 **MEDIO** | hasta +150% | 0 | 0% |
| 🔵 **MENOR** | hasta +50% | 4 | 21% |
| ✅ **CUMPLE LÍMITES** | Dentro de límites | 15 | 79% |

### Distribución por Capa

| Capa | Cantidad de Archivos | % del Total |
| :--- | :---: | :---: |
| **Domain** (Modelos, Servicios, Tareas) | 11 | 58% |
| **Data** (Conexiones, Repositorios) | 2 | 10% |
| **UI** (CLI, Page Objects) | 3 | 16% |
| **State** (Glue code, Runners) | 1 | 5% |
| **Util** (Helpers, SMTP, Exportadores) | 2 | 10% |

### Estado por Módulo (Matriz de Cobertura)

| Módulo | Dominio | Estado | UI | Notas de Deuda Técnica |
| :--- | :---: | :---: | :---: | :--- |
| `data-generator` | Completa | N/A | Completa | Acoplamiento multihilo en el servicio principal; dependencias YAGNI no depuradas. |
| `latam-automation` | Completa | Completa | Completa | La lógica regional del portal en `BuscarVueloStepDefinitions` está acoplada al idioma. |

---

## 4. HALLAZGOS CRÍTICOS — PRIORIDAD DE REFACTOR

| Prioridad | Archivo | Violación | Regla Violada | Acción Sugerida |
| :---: | :--- | :---: | :--- | :--- |
| **P1** | [UserRepository.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/repository/UserRepository.java) | +44.5% líneas | Responsabilidad Única (SRP) y YAGNI | 1. Eliminar métodos redundantes no utilizados en el proyecto: `existsDocument()`, `existsFullName()`, y `deleteByCreatedBefore()`. <br>2. Extraer el mapeador `mapRowToRecord` a una clase mapper especializada. |
| **P1** | [DataGeneratorService.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/service/DataGeneratorService.java) | +44.0% líneas | Responsabilidad Única (SRP) | 1. Extraer la lógica compleja de generación multihilo concurrente a una clase dedicada `ConcurrentGeneratorExecutor`. <br>2. Inyectar `Faker` desde un inicializador global en lugar de instanciarlo directamente dentro del constructor. |
| **P2** | [DataHelper.java](file:///Users/Marlopch/Documents/LatamQAPOC/latam-automation/src/test/java/com/latam/automation/util/DataHelper.java) | +8.0% líneas | Responsabilidad Única (SRP) | 1. Desacoplar la consulta SQL del helper de pruebas. El framework de pruebas debe delegar las consultas JDBC de lectura a `UserRepository` del módulo `data-generator`. <br>2. Mantener en esta clase exclusivamente la lógica de normalización de negocio de datos simulados. |
| **P2** | [Main.java](file:///Users/Marlopch/Documents/LatamQAPOC/data-generator/src/main/java/com/latam/datagenerator/Main.java) | +2.0% líneas | Responsabilidad Única (SRP) | 1. Extraer la lógica de renderizado del menú de consola interactivo y los diálogos de texto a una clase de vista `ConsoleView`. <br>2. Reducir la responsabilidad de `Main` a la orquestación inicial del ciclo de vida del programa. |
