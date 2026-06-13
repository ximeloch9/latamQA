package com.latam.automation.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ConfirmarCarrito implements Task {

    public static ConfirmarCarrito enElCheckout() {
        return instrumented(ConfirmarCarrito.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 7: Confirmar carrito y pasar a datos de pasajeros (button-cart-confirm--button)
        Target cartConfirmButton = Target.the("Boton Continuar a datos de pasajeros")
                .locatedBy("[data-testid='button-cart-confirm--button'], #button-cart-confirm");
        try {
            esperar(2000);
            if (cartConfirmButton.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = cartConfirmButton.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(cartConfirmButton));
                esperar(2000);
            }
        } catch (Exception ignored) {}
    }

    private void esperar(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
