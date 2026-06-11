package com.latam.automation.tasks;

import com.latam.automation.ui.LatamCheckoutPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class SeleccionarVuelo implements Task {

    public static SeleccionarVuelo deLaLista() {
        return instrumented(SeleccionarVuelo.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(LatamCheckoutPage.CHEAPEST_FLIGHT, isVisible()).forNoMoreThan(15).seconds(),
                JavaScriptClick.on(LatamCheckoutPage.CHEAPEST_FLIGHT)
        );
        
        // Si hay una ventana emergente o modal de tarifas, seleccionarla
        try {
            if (LatamCheckoutPage.CABIN_TARIF_SELECT.resolveFor(actor).isVisible()) {
                actor.attemptsTo(
                        JavaScriptClick.on(LatamCheckoutPage.CABIN_TARIF_SELECT)
                );
            }
        } catch (Exception ignored) {
            // El modal de tarifa no siempre se renderiza o puede no estar disponible
        }
    }
}
