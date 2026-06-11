package com.latam.automation.tasks;

import com.latam.automation.ui.LatamCheckoutPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
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
                WaitUntil.the(LatamCheckoutPage.FIRST_NAME_INPUT, isVisible()).forNoMoreThan(15).seconds(),
                Enter.theValue(datosUsuario.get("name")).into(LatamCheckoutPage.FIRST_NAME_INPUT),
                Enter.theValue(datosUsuario.get("lastName")).into(LatamCheckoutPage.LAST_NAME_INPUT)
        );

        // Completar número de documento
        if (LatamCheckoutPage.DOCUMENT_NUMBER_INPUT.resolveFor(actor).isVisible()) {
            actor.attemptsTo(
                    Enter.theValue(datosUsuario.get("documentId")).into(LatamCheckoutPage.DOCUMENT_NUMBER_INPUT)
            );
        }

        actor.attemptsTo(
                JavaScriptClick.on(LatamCheckoutPage.SUBMIT_PASSENGER_FORM)
        );
    }
}
