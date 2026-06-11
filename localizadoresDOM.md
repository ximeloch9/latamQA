**LATAM Airlines Colombia**

Catálogo de Localizadores DOM

para Automatización con Serenity BDD + Cucumber

URL: https://www.latamairlines.com/co/es

Fuente: Web scraping del DOM -- junio 2026

**1. Introducción**

Este documento centraliza todos los localizadores DOM extraídos del sitio oficial de LATAM Airlines Colombia (https://www.latamairlines.com/co/es) mediante análisis estático del HTML. Los identificadores cubren atributos id, data-testid, aria-label, name, placeholder y class, organizados por módulo funcional para facilitar su uso directo en pruebas automatizadas con Serenity BDD y Cucumber.

**Convención de colores en las tablas:**

-   Celdas azul oscuro: encabezados de columna

-   Celdas amarillo claro: filas impares (lectura alternada)

-   Fondo rojo en código: selector literal a usar en el Page Object

**2. Módulo: Formulario de Búsqueda de Vuelos**

El formulario principal se encuentra dentro del contenedor con id searchbox-container y data-testid fsb-form. Agrupa los controles de tipo de viaje, origen, destino, fechas, pasajeros, cabina, código promocional y canje de millas.

Localizadores del formulario principal (id y data-testid coinciden en todos los casos):

  -------------------------------------------------------------------------- -------------------------------------------- -----------------------------------------------------------
  **ID / data-testid**                                                       **Descripción funcional**                    **Caso de uso en prueba**

  fsb-one-way                                                                Radio: Solo ida                              CP1 -- Seleccionar tipo de vuelo ida

  fsb-round-trip                                                             Radio: Ida y vuelta                          CP2/CP3 -- Vuelo internacional

  fsb-multicity                                                              Radio: Multidestino                          Escenario avanzado multitramo

  fsb-origin\--text-field                                                    Campo texto: Ciudad origen                   CP1/CP2 -- Ingresar ciudad de residencia (ej. Cali → CLO)

  fsb-origin\--text-field\_\_label                                           Label del campo origen                       Verificar accesibilidad / aria

  fsb-origin\--autocomplete\_\_popper\--popper-backdrop                      Backdrop desplegable autocompletado origen   Validar apertura del dropdown de sugerencias

  fsb-destination\--text-field                                               Campo texto: Ciudad destino                  CP1 -- Ingresar destino nacional; CP2 -- destino intl.

  fsb-destination\--text-field\_\_label                                      Label del campo destino                      Verificar texto del label en idioma correcto

  fsb-destination\--autocomplete\_\_popper\--popper-backdrop                 Backdrop autocompletado destino              Validar desplegable de sugerencias destino

  fsb-swap-origin-destination                                                Botón: Invertir origen ↔ destino             Validar intercambio de ciudades

  fsb-departure\--text-field                                                 Campo fecha: Ida (departure)                 CP1/CP2/CP3 -- Seleccionar fecha de viaje

  fsb-departure\--text-field\_\_label                                        Label fecha ida                              Verificar texto en idioma configurado

  fsb-departure\--text-field\_\_end-icon                                     Ícono calendario -- fecha ida                Click alternativo para abrir datepicker

  fsb-return\--text-field                                                    Campo fecha: Vuelta (return)                 CP2 -- Vuelo ida y vuelta internacional

  fsb-return\--text-field\_\_end-icon                                        Ícono calendario -- fecha vuelta             Click alternativo para abrir datepicker vuelta

  departure-return-container                                                 Contenedor fechas ida/vuelta                 Verificar visibilidad según tipo de viaje

  fsb-passengers\--text-field                                                Campo: Número de pasajeros                   CP1 -- 1 adulto; CP3 -- menor de edad

  fsb-passengers\--text-field\_\_label                                       Label pasajeros                              Verificar texto en idioma correcto

  fsb-cabin\--select                                                         Selector tipo de cabina (Economy/Premium)    Todos los CP -- seleccionar Economy por defecto

  fsb-cabin\--select\_\_trigger                                              Trigger del select de cabina                 Click para desplegar opciones de cabina

  fsb-cabin\--select\_\_trigger\--text-field                                 Input visible de selección de cabina         Verificar valor seleccionado \"Economy\"

  fsb-cabin\--select\_\_options\--autocomplete\_\_popper\--popper-backdrop   Backdrop opciones de cabina                  Validar apertura del dropdown de cabina

  fsb-promocode\--text-field                                                 Campo código promocional                     Ingresar/omitir código promo en flujo de compra

  fsb-promocode\--text-field\_\_label                                        Label código promo                           Verificar accesibilidad del campo

  fsb-redemption\--checkbox                                                  Checkbox: Canjear millas LATAM Pass          Caso adicional: flujo de canje de millas

  fsb-redemption\--input                                                     Input valor millas a canjear                 Completar cantidad de millas en canje

  fsb-search-flights\--button                                                Botón principal: BUSCAR VUELOS               CP1/CP2/CP3 -- Acción de búsqueda

  fsb-complete-your-flight-search                                            Mensaje validación formulario incompleto     CP negativo -- verificar error al no completar campos
  -------------------------------------------------------------------------- -------------------------------------------- -----------------------------------------------------------

**3. Módulo: Tabs de Navegación (Productos)**

El buscador principal ofrece múltiples productos a través de una barra de tabs. El tab activo cambia el formulario visible. El contenedor general tiene id products-searchbox-tab con role tablist.

  ---------------------- ------------------------------------------------ ---------------------------------------------
  **ID / data-testid**   **Descripción funcional**                        **Caso de uso en prueba**

  id-tab-flight          Tab: Vuelos                                      Activar módulo de búsqueda de vuelos

  id-tab-hotel           Tab: Hoteles                                     Activar módulo de hoteles

  id-tab-package         Tab: Paquetes                                    Activar módulo de paquetes (Despegar)

  id-tab-car             Tab: Carros                                      Activar módulo de autos

  id-tab-insurance       Tab: Asistencia en viaje                         Activar módulo Assist Card

  id-tab-upg             Tab: Upgrade de cabina                           Activar opción de upgrade Premium

  id-tab-oneSearch       Tab: Búsqueda universal                          Activar buscador global de LATAM

  id-tab-esim            Tab: eSIM                                        Activar módulo eSIM

  tabpanel-flight        Panel activo del tab de vuelos (role=tabpanel)   Verificar que el panel correcto esté activo
  ---------------------- ------------------------------------------------ ---------------------------------------------

**4. Módulo: Header -- Autenticación y Configuración Regional**

El encabezado contiene los controles de autenticación, selección de moneda y portal regional. Estos localizadores son críticos para validar el contexto de sesión y el idioma del portal en el Caso de Prueba 2.

  -------------------------------------------- ----------------------------------------- -----------------------------------------------
  **ID / data-testid**                         **Descripción funcional**                 **Caso de uso en prueba**

  communications-button-login\--button         Botón: Iniciar sesión                     CP2 -- Autenticarse con usuario LATAM Pass

  communications-button-register\--button      Botón: Crear cuenta                       Flujo de registro de nuevo usuario

  header\_\_profile\_\_lnk-sign-in             Link de perfil / inicio sesión (header)   Validar estado autenticado vs. anónimo

  header\_\_currentCurrency                    Selector de moneda actual (header)        CP2 -- Verificar moneda según portal regional

  currency-selector-container                  Contenedor selector de divisa             Validar moneda COP para portal Colombia

  cityselector\--selector                      Selector de ciudad/región en header       Validar portal regional correcto

  sidebar-mobile-hamburger\--unstyled-button   Botón menú hamburguesa (móvil)            Validar navegación en dispositivos móviles
  -------------------------------------------- ----------------------------------------- -----------------------------------------------

**5. Módulo: Carrusel de Ofertas y Destinos**

La sección home-flights expone un carrusel de deals con filtros por categoría de destino. Los IDs de las cards de destino siguen el patrón {Ciudad}-Economy-id y se pueden usar para validar que las ofertas se despliegan correctamente según el origen configurado (Cali/CLO en el portal CO).

  -------------------------------------------- ---------------------------------- -------------------------------------------
  **ID / data-testid**                         **Descripción funcional**          **Caso de uso en prueba**

  deals-carousel\--test-id                     Carrusel de ofertas de vuelos      Verificar que se muestren ofertas activas

  deal-filter-on_sale\--button                 Filtro: En oferta                  Filtrar carrusel por vuelos en descuento

  deal-filter-south_america\--button           Filtro: Joyas Sudamericanas        Filtrar destinos sudamericanos

  deal-filter-tourist_beach_resorts\--button   Filtro: Destinos playeros          Filtrar destinos de playa

  deal-filter-urban\--button                   Filtro: Aventuras urbanas          Filtrar destinos urbanos

  deal-filter-night_life\--button              Filtro: Vida nocturna              Filtrar destinos de entretenimiento

  deal-filter-nature_and_landscapes\--button   Filtro: Retiros naturales          Filtrar destinos naturales

  Bogotá-Economy-id                            Card oferta Economy -- Bogotá      CP1 -- Seleccionar oferta CLO→BOG

  Cartagena de Indias-Economy-id               Card oferta Economy -- Cartagena   CP1 -- Seleccionar oferta CLO→CTG

  Lima-Economy-id                              Card oferta Economy -- Lima        CP2 -- Oferta vuelo internacional Lima

  Orlando-Economy-id                           Card oferta Economy -- Orlando     CP2 -- Oferta vuelo internacional Orlando

  Cúcuta-Economy-id                            Card oferta Economy -- Cúcuta      CP1 -- Oferta vuelo nacional Cúcuta
  -------------------------------------------- ---------------------------------- -------------------------------------------

**6. Localizadores por aria-label -- Elementos de Accesibilidad**

Los siguientes aria-labels son relevantes para pruebas de accesibilidad y para automatización en contextos donde no hay id disponible. Se usan con By.cssSelector(\'\[aria-label=\"\...\"\]\') o By.xpath(\'//\*\[@aria-label=\"\...\"\]\').

  ----------------------------------------------------------------- -------------------------------------------
  **aria-label**                                                    **Propósito / uso en prueba**

  Escribe una ciudad de origen y luego selecciónala.                Instrucción campo origen (accesibilidad)

  Escribe una ciudad de destino y luego selecciónala.               Instrucción campo destino (accesibilidad)

  Elige la fecha de ida para el viaje.                              Activar datepicker de fecha de ida

  Elige la fecha de vuelta para el viaje.                           Activar datepicker de fecha de vuelta

  Escoge el tipo y cantidad de pasajeros.                           Abrir selector de pasajeros

  Selecciona el tipo de cabina                                      Abrir selector de cabina

  Invertir ciudad de origen con ciudad de destino.                  Botón swap origen ↔ destino

  Buscar vuelos                                                     Botón submit del formulario

  Sin campos completados: Debes completar\...                       Alerta de validación formulario vacío

  Iniciar sesión                                                    Botón login header

  Crear cuenta                                                      Botón registro header

  Multidestino. Nueva forma de viajar incluyendo varias ciudades.   Radio button multidestino
  ----------------------------------------------------------------- -------------------------------------------

**7. Placeholders de Campos de Texto**

Los placeholders pueden usarse como localizadores de respaldo cuando no hay id o data-testid disponible. Se acceden con By.cssSelector(\'\[placeholder=\"\...\"\]\').

  -------------------------- ----------------------------------------------------
  **Placeholder**            **Campo correspondiente**

  Ingresa un origen          Campo de ciudad de origen (primer tramo)

  Ingresa un destino         Campo de ciudad de destino (primer tramo)

  Selecciona otro origen     Campo origen de tramos adicionales en multidestino
  -------------------------- ----------------------------------------------------

**8. Mapeo de Datos Generados → Localizadores DOM**

La siguiente tabla conecta directamente los datos ficticios generados en la Parte 1 del proyecto con los localizadores DOM identificados, indicando la estrategia de inyección y el caso de prueba aplicable.

  ------------------------------- ------------------------- ----------------------------- ---------------------------------------------------- --------
  **Dato generado**               **Campo en LATAM**        **Localizador DOM**           **Estrategia**                                       **CP**

  Ciudad de residencia            Campo origen              fsb-origin\--text-field       Mapear ciudad → código IATA (BOG, CLO, MDE, CTG)     1,2

  Idioma generado                 URL portal / UI           cityselector\--selector       Validar idioma en labels de fsb-departure\_\_label   2

  País de residencia              Portal regional           header\_\_currentCurrency     URL base: /co/es, /us/en, /pe/es según país          2

  Edad (≥18 adulto, \<18 menor)   Tipo pasajero             fsb-passengers\--text-field   Adulto → Adult; \<18 → Child en selector             1,3

  Nombre + Apellido               Formulario checkout       (En pág. resultados)          Completar campos nombre/apellido en checkout         1,3

  Documento (CC/CE)               Doc. identidad checkout   (En pág. resultados)          Menor: doc inicia en 11000000; adulto: doc normal    3
  ------------------------------- ------------------------- ----------------------------- ---------------------------------------------------- --------

**9. Referencia de Implementación -- Page Object Java**

Fragmento de referencia para el Page Object del formulario de búsqueda usando Serenity + WebDriver:

// LATAMSearchPage.java -- Page Object con Serenity

\@DefaultUrl(\"https://www.latamairlines.com/co/es\")

public class LATAMSearchPage extends PageObject {

\@FindBy(id = \"fsb-origin\--text-field\") WebElementFacade originField;

\@FindBy(id = \"fsb-destination\--text-field\") WebElementFacade destinationField;

\@FindBy(id = \"fsb-departure\--text-field\") WebElementFacade departureDateField;

\@FindBy(id = \"fsb-return\--text-field\") WebElementFacade returnDateField;

\@FindBy(id = \"fsb-passengers\--text-field\") WebElementFacade passengersField;

\@FindBy(id = \"fsb-cabin\--select\") WebElementFacade cabinSelect;

\@FindBy(id = \"fsb-search-flights\--button\") WebElementFacade searchButton;

\@FindBy(id = \"fsb-one-way\") WebElementFacade oneWayRadio;

\@FindBy(id = \"fsb-round-trip\") WebElementFacade roundTripRadio;

\@FindBy(id = \"fsb-swap-origin-destination\") WebElementFacade swapButton;

}

**10. Observaciones y Riesgos**

**Estabilidad de los localizadores:**

-   Los atributos id y data-testid (fsb-\*) son los más estables para automatización. LATAM los usa consistentemente en todos los campos del formulario principal.

-   Los class names con hash (Styledxxxx-sc-xxxxx-N) son generados dinámicamente por styled-components y cambiarán con cada deploy. NO usar como localizadores primarios.

-   Los aria-label contienen texto en español que puede cambiar si se actualiza el copy del sitio.

**Consideraciones de sincronización:**

-   El autocomplete de origen/destino requiere esperar el backdrop (fsb-origin\--autocomplete\_\_popper\--popper-backdrop) antes de seleccionar la opción.

-   Tras hacer click en fsb-search-flights\--button, esperar a que la URL cambie a /booking/flights antes de interactuar con resultados.

-   El datepicker de fechas es un componente reactivo; usar SendKeys o JavascriptExecutor según el comportamiento en el entorno de prueba.

**Portal regional:**

-   Para CP1 (usuario colombiano): URL base https://www.latamairlines.com/co/es

-   Para CP2 (usuario extranjero): ajustar URL según país generado; validar cityselector\--selector\_\_value.