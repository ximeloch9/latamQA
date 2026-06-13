package com.latam.automation.tasks;

import com.latam.automation.ui.LatamCheckoutPage;
import com.latam.automation.util.AutomationConfig;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class IngresarPasajero implements Task {

    private final Map<String, String> datosUsuario;

    public IngresarPasajero(Map<String, String> datosUsuario) {
        this.datosUsuario = datosUsuario;
    }

    public static IngresarPasajero conDatos(Map<String, String> datosUsuario) {
        return instrumented(IngresarPasajero.class, datosUsuario);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(LatamCheckoutPage.FIRST_NAME_INPUT, isVisible()).forNoMoreThan(15).seconds()
        );

        escribirComoHumano(actor, LatamCheckoutPage.FIRST_NAME_INPUT, datosUsuario.get("name"));
        escribirComoHumano(actor, LatamCheckoutPage.LAST_NAME_INPUT, datosUsuario.get("lastName"));

        // Seleccionar tipo de documento si está visible
        if (LatamCheckoutPage.DOCUMENT_TYPE_SELECT.resolveFor(actor).isVisible()) {
            String tipoDoc = "NATURAL_MINOR".equalsIgnoreCase(datosUsuario.get("userType")) ? "TI" : "CC";
            try {
                actor.attemptsTo(
                        SelectFromOptions.byValue(tipoDoc).from(LatamCheckoutPage.DOCUMENT_TYPE_SELECT)
                );
                esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
            } catch (Exception ignored) {
                // Silencioso si la opción de valor no existe en el DOM de la aerolínea
            }
        }

        // Completar número de documento
        if (LatamCheckoutPage.DOCUMENT_NUMBER_INPUT.resolveFor(actor).isVisible()) {
            escribirComoHumano(actor, LatamCheckoutPage.DOCUMENT_NUMBER_INPUT, datosUsuario.get("documentId"));
        }

        esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
        actor.attemptsTo(
                JavaScriptClick.on(LatamCheckoutPage.SUBMIT_PASSENGER_FORM)
        );
    }

    private void escribirComoHumano(Actor actor, Target target, String texto) {
        try {
            WebElementFacade element = target.resolveFor(actor);
            element.clear();
            for (char c : texto.toCharArray()) {
                element.sendKeys(String.valueOf(c));
                long range = AutomationConfig.MAX_DELAY_TECLA_MS - AutomationConfig.MIN_DELAY_TECLA_MS;
                long delay = AutomationConfig.MIN_DELAY_TECLA_MS + (long) (Math.random() * range);
                esperar(delay);
            }
            esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
        } catch (Exception e) {
            actor.attemptsTo(Enter.theValue(texto).into(target));
        }
    }

    private void esperar(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
