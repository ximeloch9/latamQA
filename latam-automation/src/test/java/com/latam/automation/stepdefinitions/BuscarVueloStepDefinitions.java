package com.latam.automation.stepdefinitions;

import com.latam.automation.tasks.BuscarVuelo;
import com.latam.automation.tasks.IngresarPasajero;
import com.latam.automation.tasks.SeleccionarVuelo;
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
import org.openqa.selenium.WebDriver;
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
        String pais = testUser.get("country");

        // Verificación de idioma/moneda regional según el país cargado usando Text.of
        if ("Peru".equalsIgnoreCase(pais)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("PE")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("USD")));
        } else if ("Chile".equalsIgnoreCase(pais)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("CL")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("CLP")));
        } else if ("Ecuador".equalsIgnoreCase(pais)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("EC")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("USD")));
        } else if ("Brasil".equalsIgnoreCase(pais) || "Brazil".equalsIgnoreCase(pais)) {
            theActorInTheSpotlight().should(
                    seeThat(Text.of(LatamSearchPage.REGIONAL_SELECTOR), Matchers.containsString("BR")),
                    seeThat(Text.of(LatamSearchPage.CURRENT_CURRENCY), Matchers.containsString("BRL")));
        }

        verificarCargaResultados();
    }

    /**
     * DRY: verificación compartida de que la pantalla de resultados cargó
     * correctamente.
     */
    private void verificarCargaResultados() {
        theActorInTheSpotlight().should(
                seeThat(the(LatamCheckoutPage.CHEAPEST_FLIGHT), isVisible()));
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        log.info("Datos de documento del menor validados con exito: {}", testUser.get("documentId"));
    }
}
