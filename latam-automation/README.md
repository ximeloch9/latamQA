# ✈️ Latam Automation Framework

Este módulo contiene el framework de automatización de pruebas de UI para el portal **Latam Airlines Colombia**, implementado con **Serenity BDD + Cucumber** y el patrón **Screenplay**.

> **Referencia de reglas:** Todas las clases y métodos de este módulo deben cumplir con las directrices definidas en [`agent.md`](file:///Users/Marlopch/Documents/LatamQAPOC/agent.md) (SOLID, DRY, KISS, YAGNI, SLF4J, Java 17).

---

## 📁 Arquitectura — Paquetes (`com.latam.automation`)

```text
com.latam.automation
├── tasks/           ← Screenplay Tasks: acciones de negocio de alto nivel
│   ├── BuscarVuelo.java              # Búsqueda de vuelo (origen, destino, fecha, pasajeros)
│   ├── SeleccionarVuelo.java         # Selección de tarifa más económica
│   ├── SeleccionarTarifaReembolsable.java
│   ├── ElegirAsientosMasTarde.java
│   ├── OmitirServiciosAdicionales.java
│   ├── ConfirmarCarrito.java
│   └── IngresarPasajero.java         # Formulario de datos de pasajero (ADT + CHD)
├── ui/              ← Page Objects / selectores DOM estables
│   ├── LatamSearchPage.java
│   └── LatamCheckoutPage.java        # Selectores dinámicos por sufijo (ADT_1, CHD_1)
├── stepdefinitions/ ← Glue code Cucumber ↔ Screenplay Tasks
└── util/            ← Helpers: AutomationConfig, DataHelper (conexión SQLite/CSV)
```

> **Regla (§5 agent.md):** No se crean clases fuera de estos paquetes sin justificación documentada.

---

## 🧪 Escenarios implementados

Todos los escenarios están activos y habilitados para ejecución en `src/test/resources/features/buscar_vuelo.feature`.

### CP1 — Búsqueda de vuelo nacional para usuario adulto residente en Colombia (Solo Ida) ✅
* **Caso activo**
* **Flujo:** Carga un usuario `Adulto` desde la fuente de datos, accede al portal de Colombia, configura el viaje como Solo Ida y realiza la búsqueda del tramo Bogotá (BOG) a Medellín (MDE), validando la aparición de resultados de vuelos.

### CP2 — Búsqueda de vuelo internacional multilenguaje (Usuario Extranjero - Ida y Vuelta) ✅
* **Caso activo**
* **Flujo:** Carga un usuario `Extranjero` (fuera de Colombia), abre el portal regional de LATAM correspondiente a su país e idioma, realiza la búsqueda de ida y vuelta a Orlando (MCO), y valida la correcta carga de la moneda y la configuración regional en el portal.
* > **Nota sobre limitaciones de cobertura (Deuda Técnica):**
  > * Este escenario funciona correctamente con todos los países, **excepto España**.
  > * Si el país correspondiente del usuario no se encuentra dentro de las opciones directas de Latam (por ejemplo, las opciones "Otros países" o "Resto de Europa"), estas opciones **no están soportadas** por el flujo actual de automatización. Esto queda documentado como deuda técnica para ser resuelto en iteraciones futuras.

### CP3 — Validación de datos de pasajero menor de edad en Checkout ✅
* **Caso activo y pasando**
* **Flujo completo automatizado:**
  1. Carga un usuario `Menor` desde `data-generator.db`
  2. Navega al portal de Latam Airlines Colombia
  3. Busca un vuelo de ida (BOG → MDE por defecto)
  4. Selecciona la tarifa más económica
  5. Selecciona opción de tarifa reembolsable
  6. Elige asientos más tarde de manera aleatoria
  7. Omite servicios adicionales (equipaje / embarque prioritario)
  8. Confirma el carrito y avanza al Checkout
  9. **Completa el formulario del Adulto acompañante (ADT_1)**
  10. **Completa el formulario del Menor (CHD_1)** con datos de la BD
  11. Verifica que el formulario acepta el documento especial del menor.

---

## 🔑 Selectores DOM — `LatamCheckoutPage`

Los selectores del formulario de Checkout son **dinámicos por sufijo de pasajero**, lo que elimina duplicación (DRY) y permite escalar a N pasajeros sin modificar código existente (OCP):

| Campo | ID DOM | Método |
|-------|--------|--------|
| Nombre | `passengerDetails-firstName-{suffix}` | `firstNameInput(suffix)` |
| Apellido | `passengerDetails-lastName-{suffix}` | `lastNameInput(suffix)` |
| Tipo de documento | XPath dinámico por `document`+`{suffix}` | `documentTypeSelect(suffix)` |
| Número de documento | `documentInfo-documentNumber-{suffix}` | `documentNumberInput(suffix)` |
| Fecha de nacimiento | `passengerInfo-dateOfBirth-{suffix}` | `birthDateInput(suffix)` |
| Email | `passengerInfo-emails-{suffix}` | `emailInput(suffix)` |
| Teléfono | `passengerInfo-phones0-number-{suffix}` | `phoneInput(suffix)` |
| Botón confirmar | `passengerFormSubmitButton{suffix}` | `submitPassengerForm(suffix)` |

Los sufijos en uso son `ADT_1` (adulto acompañante) y `CHD_1` (menor). Para el catálogo completo de selectores DOM del portal, ver [`localizadoresDOM.md`](file:///Users/Marlopch/Documents/LatamQAPOC/localizadoresDOM.md).

---

## 🤖 Estrategia de escritura en formularios React — `IngresarPasajero`

Los formularios del portal de Latam son componentes **React con debounce** que no responden a `driver.clear()` ni a `sendKeys` en bloque. La clase `IngresarPasajero` implementa el método privado `escribirComoHumano()` con las siguientes garantías:

1. **Scroll al elemento** via `scrollIntoView({behavior: 'smooth', block: 'center'})` antes de interactuar.
2. **Validación de estado** del campo: omite silenciosamente campos `readonly` o `disabled` (evita `InvalidElementStateException`).
3. **Limpieza robusta**: `CTRL+A` + `CMD+A` + `BACKSPACE` antes de escribir, compatible con macOS y Linux.
4. **Escritura carácter a carácter** con delay aleatorio entre `MIN_DELAY_TECLA_MS` y `MAX_DELAY_TECLA_MS` (configurado en `AutomationConfig`) para activar el debouncer de React.
5. **Guard clauses** con `try/catch` por cada campo: un fallo aislado no detiene el formulario completo.

**Datos usados en el formulario:**

| Pasajero | Tipo Doc | Fecha Nacimiento | Email | Teléfono |
|----------|----------|-----------------|-------|----------|
| ADT_1 (Adulto) | CC | `15061990` (15/06/1990) | `qatest@gmail.com` | `3112223344` |
| CHD_1 (Menor) | TI | `15062020` (15/06/2020, ~4 años) | `qatest@gmail.com` | `3112223344` |

> **Regla de negocio Menor:** La edad del menor debe estar entre **2 y 12 años**. El año 2020 se usa para estabilidad del test (4 años al momento de implementación).

---

## ⚙️ Configuración — `AutomationConfig`

Valores de delay para mantener el comportamiento humano y evitar bloqueos del debouncer React:

| Constante | Valor configurado | Propósito |
|-----------|------------------|-----------|
| `MIN_DELAY_TECLA_MS` | 10 ms | Velocidad mínima de tecleo |
| `MAX_DELAY_TECLA_MS` | 40 ms | Velocidad máxima de tecleo |
| `DELAY_HUMANO_CAMPO_MS` | 200 ms | Pausa entre campos |

> ⚠️ Bajar estos valores puede romper la sincronización con React y causar campos vacíos o incompletos.

---

## 🚀 Instrucciones de ejecución

### Prerrequisitos
- **Java 17** instalado y en el `PATH`
- **Maven 3.6+** instalado globalmente
- **Google Chrome** actualizado (versión ≥ 120 recomendada)
- Base de datos de prueba generada: `../data-generator/data-generator.db`

### Comandos

```bash
# Desde la raíz del módulo latam-automation/

# (Recomendado) Limpiar perfil de Chrome y ejecutar suite completa
rm -rf .chrome-profile && mvn clean verify

# Solo ejecutar sin limpiar perfil (más rápido si el perfil ya tiene cookies válidas)
mvn clean verify
```

### Ver reportes Serenity

```text
latam-automation/target/site/serenity/index.html
```

---

## ⚠️ Consideraciones de Seguridad (WAF)

> [!WARNING]
> Este framework interactúa con el portal público en producción (`https://www.latamairlines.com/co/es`). Ejecuciones repetitivas o concurrentes pueden activar el WAF de Latam Airlines con respuestas **403 Access Denied**.
>
> Para mitigar esto:
> - Ejecutar localmente con navegador visible (sin `headless`) en `serenity.conf`.
> - Limpiar el perfil de Chrome con `rm -rf .chrome-profile` si aparecen bloqueos.
> - Apuntar las pruebas a un entorno Staging/Sandbox si se dispone de uno.

---

## 🛡️ Estrategias Anti-Bot

Para evadir la detección de bots por parte de la CDN (Akamai), el framework implementa en `serenity.conf` y en los steps:

1. **Perfil persistente Chrome (`--user-data-dir`)**: Evita sesiones 100% limpias (incógnito) que Akamai bloquea agresivamente. El perfil guarda cookies de sesiones exitosas previas.
2. **User-Agent nativo (sin forzar versión fija)**: No se declara un User-Agent desactualizado. Chrome autogenera el suyo real, garantizando coherencia con las APIs JS del motor.
3. **CDP Stealth**: Se inyecta un script vía Chrome DevTools Protocol para enmascarar `navigator.webdriver = false` y emular variables nativas de Chrome antes de cada carga de página.

---

## 🔍 Señales de alerta conocidas y Deuda Técnica (§10 agent.md)

> [!NOTE]
> Las siguientes situaciones han sido identificadas y están pendientes de refactoring o corrección en una iteración futura:
> - `IngresarPasajero.schedulerWait()`: método privado que delega en `esperar()` sin valor adicional → candidato YAGNI a eliminar.
> - El método `escribirComoHumano` excede las 20 líneas recomendadas por KISS → candidato a extracción de submétodos (`scrollToElement`, `limpiarCampo`, `enviarCaracteres`).
> - **Known Issue (Bug Funcional)**: En `DataHelper.java`, la validación `fields.length >= 9` nunca se cumple debido a que `CsvExporter.java` exporta el archivo CSV con únicamente 8 columnas. Esto ocasiona que el lector de CSV falle de manera silenciosa y utilice siempre el usuario mock de respaldo. (Identificado en auditoría técnica).

---

## 📝 Convención de commits (§11 agent.md)

```
feat(latam-automation): <descripción en español, imperativo>
fix(latam-automation): eliminar imports no usados en BuscarVuelo
refactor(checkout): extraer submétodos en escribirComoHumano (KISS)
test(cp3): ajustar fecha de nacimiento CHD_1 a año 2020
```
