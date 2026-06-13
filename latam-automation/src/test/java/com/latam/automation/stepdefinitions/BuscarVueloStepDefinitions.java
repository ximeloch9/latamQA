package com.latam.automation.stepdefinitions;

import com.latam.automation.tasks.BuscarVuelo;
import com.latam.automation.tasks.IngresarPasajero;
import com.latam.automation.tasks.SeleccionarVuelo;
import com.latam.automation.tasks.SeleccionarTarifaReembolsable;
import com.latam.automation.tasks.ElegirAsientosMasTarde;
import com.latam.automation.tasks.OmitirServiciosAdicionales;
import com.latam.automation.tasks.ConfirmarCarrito;
import com.latam.automation.ui.LatamCheckoutPage;
import com.latam.automation.ui.LatamSearchPage;
import com.latam.automation.util.DataHelper;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hamcrest.Matchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import net.serenitybdd.screenplay.questions.Text;

public class BuscarVueloStepDefinitions {

    private static final Logger log = LoggerFactory.getLogger(BuscarVueloStepDefinitions.class);
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Managed
    private WebDriver herBrowser;

    private static final String ACTOR_NAME = "QA Tester";
    private Map<String, String> testUser;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Dado("que el actor carga un usuario {string} de la base de datos")
    public void cargarUsuario(String tipoUsuario) {
        testUser = DataHelper.getUsuarioPorTipo(tipoUsuario);
        theActorCalled(ACTOR_NAME).can(BrowseTheWeb.with(herBrowser));
        inyectarStealthViaCDP(herBrowser);
    }

    /**
     * Inyecta un script de stealth via CDP (Page.addScriptToEvaluateOnNewDocument).
     * Este script se ejecuta ANTES de que cada página cargue su propio JavaScript,
     * ocultando las huellas de Selenium/WebDriver que LATAM usa para detectar bots.
     */
    private void inyectarStealthViaCDP(WebDriver driver) {
        try {
            // Desempaquetar el driver real (Serenity envuelve con WebDriverFacade)
            WebDriver rawDriver = driver;
            while (rawDriver instanceof WrapsDriver) {
                rawDriver = ((WrapsDriver) rawDriver).getWrappedDriver();
            }
            if (!(rawDriver instanceof ChromeDriver)) return;

            ChromeDriver chromeDriver = (ChromeDriver) rawDriver;
            String stealthScript =
                // Ocultar navigator.webdriver (la señal más detectada)
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined, configurable: true});" +
                // Poblar window.chrome con propiedades reales (ausentes en Selenium por defecto)
                "if (!window.chrome) { window.chrome = {}; }" +
                "window.chrome.runtime = window.chrome.runtime || {};" +
                "window.chrome.app = window.chrome.app || { isInstalled: false };" +
                "window.chrome.csi = window.chrome.csi || function(){return {};};" +
                "window.chrome.loadTimes = window.chrome.loadTimes || function(){return {};};" +
                // Simular plugins como en un Chrome real (incognito tiene 0 plugins)
                "Object.defineProperty(navigator, 'plugins', {" +
                "  get: () => { var arr = [1,2,3]; arr.item = (i) => arr[i]; arr.namedItem = (n) => null; arr.refresh = () => {}; return arr; }," +
                "  configurable: true" +
                "});" +
                // Lenguajes del navegador coherentes con Colombia
                "Object.defineProperty(navigator, 'languages', {get: () => ['es-CO', 'es', 'en-US', 'en'], configurable: true});";

            chromeDriver.executeCdpCommand(
                "Page.addScriptToEvaluateOnNewDocument",
                Map.of("source", stealthScript)
            );
            log.info("[Stealth] CDP script inyectado correctamente en el navegador.");
        } catch (Exception e) {
            log.warn("[Stealth] No se pudo inyectar CDP stealth: {}. Continuando sin el.", e.getMessage());
        }
    }

    @Cuando("el actor ingresa al portal de Latam en Colombia")
    public void ingresarPortalColombia() {
        theActorInTheSpotlight().attemptsTo(
                Open.url(LatamSearchPage.URL));
    }

    @Y("selecciona el tipo de viaje solo ida")
    public void seleccionarSoloIda() {
        // TODO: La tarea BuscarVuelo ya maneja el tipo de viaje (Solo Ida), por lo que
        // este step actúa como placeholder del flujo de negocio.
    }

    @Y("busca un vuelo desde su ciudad de residencia {string} hacia {string}")
    public void buscarVueloNacional(String origen, String destino) {
        String fechaIda = LocalDate.now().plusDays(7).format(ISO);
        theActorInTheSpotlight().attemptsTo(
                BuscarVuelo.conParametrosYFechas(origen, destino, true, fechaIda, null));
    }

    @Entonces("el sistema debe mostrar los vuelos disponibles para la seleccion")
    public void verificarVuelosDisponibles() {
        verificarCargaResultados();
    }

    @Cuando("el actor ingresa al portal regional según su país de origen e idioma")
    public void ingresarPortalRegional() {
        String pais = testUser.get("country");
        String idioma = testUser.get("language");
        String url = LatamSearchPage.URL;

        // Mapeo dinámico de portales regionales según país y lenguaje
        if ("Peru".equalsIgnoreCase(pais)) {
            url = "https://www.latamairlines.com/pe/es";
        } else if ("Chile".equalsIgnoreCase(pais)) {
            url = "https://www.latamairlines.com/cl/es";
        } else if ("Ecuador".equalsIgnoreCase(pais)) {
            url = "https://www.latamairlines.com/ec/es";
        } else if ("Brasil".equalsIgnoreCase(pais) || "Brazil".equalsIgnoreCase(pais)) {
            url = "https://www.latamairlines.com/br/pt";
        } else if (!"Colombia".equalsIgnoreCase(pais)) {
            url = "en".equalsIgnoreCase(idioma) ? "https://www.latamairlines.com/us/en"
                    : "https://www.latamairlines.com/co/es";
        }

        log.info("Navegando al portal regional para país: {} e idioma: {}. URL: {}", pais, idioma, url);
        theActorInTheSpotlight().attemptsTo(
                Open.url(url));
    }

    @Y("selecciona el tipo de viaje ida y vuelta")
    public void seleccionarIdaYVuelta() {
        // TODO: La tarea BuscarVuelo ya maneja la opción de ida y vuelta, por lo que
        // este step actúa como placeholder del flujo de negocio.
    }

    @Y("busca un vuelo desde su ciudad de residencia hacia {string}")
    public void buscarVueloInternacional(String destino) {
        String origen = testUser.get("city");
        String fechaIda = LocalDate.now().plusDays(7).format(ISO);
        String fechaVuelta = LocalDate.now().plusDays(14).format(ISO);
        theActorInTheSpotlight().attemptsTo(
                BuscarVuelo.conParametrosYFechas(origen, destino, false, fechaIda, fechaVuelta));
    }

    @Entonces("la página de resultados debe cargarse correctamente respetando la moneda y el idioma del usuario")
    public void verificarPortalRegional() {
        // 1. Esperar primero a que la página de resultados cargue por completo
        verificarCargaResultados();

        String pais = testUser.get("country");
        // Normalizar tildes (ej. Perú -> Peru)
        String paisNorm = pais != null ? pais.replace("ú", "u").replace("Ú", "U") : "";

        // 2. Ejecutar las aserciones regionales con el DOM ya cargado
        if ("Peru".equalsIgnoreCase(paisNorm)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("PE")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("USD")));
        } else if ("Chile".equalsIgnoreCase(paisNorm)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("CL")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("CLP")));
        } else if ("Ecuador".equalsIgnoreCase(paisNorm)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("EC")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("USD")));
        } else if ("Brasil".equalsIgnoreCase(paisNorm) || "Brazil".equalsIgnoreCase(paisNorm)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("BR")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("BRL")));
        }
    }

    /**
     * DRY: verificación compartida de que la pantalla de resultados cargó
     * correctamente. Incluye retry si LATAM muestra "La búsqueda está tardando".
     */
    private void verificarCargaResultados() {
        WebDriver driver = BrowseTheWeb.as(theActorInTheSpotlight()).getDriver();
        boolean encontrado = false;
        int intentos = 0;
        final int MAX_INTENTOS = 2;

        while (!encontrado && intentos < MAX_INTENTOS) {
            try {
                theActorInTheSpotlight().attemptsTo(
                        WaitUntil.the(LatamCheckoutPage.CHEAPEST_FLIGHT, isVisible())
                                .forNoMoreThan(30).seconds());
                encontrado = true;
            } catch (Exception e) {
                intentos++;
                log.warn("Intento {} de {}: vuelo no visible. Verificando boton de reintento de LATAM...", intentos, MAX_INTENTOS);
                if (intentos < MAX_INTENTOS) {
                    // Buscar y presionar "Realizar otra búsqueda" si LATAM muestra la pantalla de error
                    try {
                        java.util.List<WebElement> botones = driver.findElements(
                                By.cssSelector("[data-testid='search-again-button'],button[class*='SearchAgain']"));
                        if (!botones.isEmpty()) {
                            log.info("Clic en 'Realizar otra busqueda' de LATAM...");
                            botones.get(0).click();
                            Thread.sleep(8000);
                        }
                    } catch (Exception retryEx) {
                        log.warn("No se pudo hacer reintento: {}", retryEx.getMessage());
                    }
                }
            }
        }

        // Aserción final: falla el test si después de los reintentos no hay resultados
        theActorInTheSpotlight().should(
                seeThat(the(LatamCheckoutPage.CHEAPEST_FLIGHT), isVisible()));
    }

    @Y("busca un vuelo de ida en Latam")
    public void buscarVueloDeIda() {
        String origen = testUser.get("city");
        String destino = "Bogota".equalsIgnoreCase(origen) ? "Medellin" : "Bogota";
        String fechaIda = LocalDate.now().plusDays(7).format(ISO);

        theActorInTheSpotlight().attemptsTo(
                BuscarVuelo.conParametrosYFechasYPasajeros(origen, destino, true, fechaIda, null, true));
    }

    @Y("selecciona la tarifa más económica del vuelo")
    public void seleccionarTarifa() {
        theActorInTheSpotlight().attemptsTo(
                SeleccionarVuelo.deLaLista());
    }

    @Y("selecciona la opcion de continuar con tarifa reembolsable")
    public void seleccionarTarifaReembolsable() {
        theActorInTheSpotlight().attemptsTo(
                SeleccionarTarifaReembolsable.deVuelo());
    }

    @Y("elige los asientos mas tarde y de manera aleatoria")
    public void elegirAsientosMasTarde() {
        theActorInTheSpotlight().attemptsTo(
                ElegirAsientosMasTarde.enElMapa());
    }

    @Y("continua sin servicios adicionales de equipaje y embarque prioritario")
    public void omitirServiciosAdicionales() {
        theActorInTheSpotlight().attemptsTo(
                OmitirServiciosAdicionales.enElCheckout());
    }

    @Y("confirma el carrito a datos de pasajeros")
    public void confirmarCarritoPasajeros() {
        theActorInTheSpotlight().attemptsTo(
                ConfirmarCarrito.enElCheckout());
    }

    @Y("completa los datos del pasajero menor en el Checkout")
    public void completarDatosPasajero() {
        theActorInTheSpotlight().attemptsTo(
                IngresarPasajero.conDatos(testUser));
    }

    @Entonces("el formulario de pasajeros debe aceptar el documento especial del menor")
    public void verificarDocumentoMenor() {
        // Verificar que no se muestren alertas de error visibles en los campos
        theActorInTheSpotlight().should(
                seeThat(the(LatamCheckoutPage.ERROR_MESSAGES), isNotVisible()));
        
        // Registro informativo opcional de la navegación a la siguiente pantalla
        try {
            if (LatamCheckoutPage.PAYMENT_HEADER.resolveFor(theActorInTheSpotlight()).isCurrentlyVisible()) {
                log.info("[Éxito] Navegación confirmada a la pantalla de pago.");
            }
        } catch (Exception ignored) {}

        log.info("Datos de documento del menor validados con exito: {}", testUser.get("documentId"));
    }
}
