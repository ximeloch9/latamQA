package com.latam.automation.tasks;

import com.latam.automation.ui.LatamCheckoutPage;
import com.latam.automation.util.AutomationConfig;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class SeleccionarVuelo implements Task {

    private static final Target PRIMER_VUELO = Target.the("primer vuelo disponible")
            .locatedBy("//*[@data-testid='wrapper-card-header-0']");

    /**
     * Selector del botón "Realizar otra búsqueda" que muestra LATAM
     * cuando la búsqueda de vuelos falla o tarda demasiado.
     */
    private static final String SELECTOR_ERROR_BUSQUEDA = "[data-testid='search-again-button'], " +
            "button[class*='SearchAgain'], " +
            "a[href*='search']";

    public static SeleccionarVuelo deLaLista() {
        return instrumented(SeleccionarVuelo.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Cambiar foco a la nueva pestaña si se abrió una
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            List<String> tabs = new ArrayList<>(driver.getWindowHandles());
            if (tabs.size() > 1) {
                driver.switchTo().window(tabs.get(tabs.size() - 1));
            }
        } catch (Exception ignored) {
            // Si no hay nueva pestaña, continúa en la misma
        }

        // Intento inicial de encontrar el primer vuelo,
        // con autorecuperación si aparece la pantalla de error de LATAM
        int intentos = 0;
        boolean vuelloEncontrado = false;

        while (intentos <= AutomationConfig.MAX_REINTENTOS_ERROR_BUSQUEDA && !vuelloEncontrado) {
            try {
                actor.attemptsTo(
                        WaitUntil.the(PRIMER_VUELO, isVisible())
                                .forNoMoreThan(AutomationConfig.TIMEOUT_PRIMER_VUELO_SEG).seconds());
                vuelloEncontrado = true;
            } catch (Exception errorEspera) {
                if (intentos < AutomationConfig.MAX_REINTENTOS_ERROR_BUSQUEDA) {
                    // Intentar recuperarse haciendo clic en "Realizar otra búsqueda"
                    if (intentarRecuperarBusqueda(actor)) {
                        intentos++;
                    } else {
                        break; // No hay botón de recuperación, dejar fallar limpiamente
                    }
                } else {
                    break;
                }
            }
        }

        // Si encontró vuelos, hacer clic en el primero
        actor.attemptsTo(JavaScriptClick.on(PRIMER_VUELO));

        // Si aparece el modal de selección de tarifa, seleccionar la primera
        try {
            actor.attemptsTo(
                    WaitUntil.the(LatamCheckoutPage.CABIN_TARIF_SELECT, isVisible()).forNoMoreThan(5).seconds(),
                    JavaScriptClick.on(LatamCheckoutPage.CABIN_TARIF_SELECT)
            );
        } catch (Exception ignored) {
            // El modal de tarifa no siempre aparece
        }

        // Click en el botón de continuar en el carrito/pie de página para ir a asientos
        // o checkout
        Target botonContinuarCarrito = Target.the("Boton continuar del carrito o pie de pagina")
                .locatedBy(
                        "//button[contains(@id,'continue') or contains(@data-testid,'continue') or contains(.,'Continuar') or contains(.,'Ir a') or contains(.,'Siguiente') or contains(.,'Confirmar')]");

        for (int i = 0; i < 3; i++) {
            try {
                esperar(2000);
                if (botonContinuarCarrito.resolveFor(actor).isCurrentlyVisible()) {
                    actor.attemptsTo(JavaScriptClick.on(botonContinuarCarrito));
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

    }

    /**
     * Intenta recuperarse de la pantalla de error de LATAM haciendo clic en
     * el botón "Realizar otra búsqueda". Retorna true si el botón fue encontrado y
     * clickeado.
     * Este método NO vuelve a llenar el formulario; LATAM recarga la búsqueda
     * original
     * automáticamente al hacer clic en ese botón.
     */
    private boolean intentarRecuperarBusqueda(Actor actor) {
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            List<WebElement> botones = driver.findElements(By.cssSelector(SELECTOR_ERROR_BUSQUEDA));

            if (!botones.isEmpty()) {
                esperar(AutomationConfig.DELAY_REINTENTO_BUSQUEDA_MS);
                botones.get(0).click();
                esperar(AutomationConfig.DELAY_NUEVA_PESTANA_MS);
                return true;
            }
        } catch (Exception ignored) {
            // No se pudo recuperar
        }
        return false;
    }

    private void esperar(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}