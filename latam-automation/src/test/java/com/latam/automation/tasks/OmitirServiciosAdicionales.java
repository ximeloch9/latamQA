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

public class OmitirServiciosAdicionales implements Task {

    public static OmitirServiciosAdicionales enElCheckout() {
        return instrumented(OmitirServiciosAdicionales.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 5: Continuar al servicio de equipajes (BAGS-continue-button)
        Target bagsButton = Target.the("Boton Ir al siguiente servicio (Bags)")
                .locatedBy("[data-testid='BAGS-continue-button'], #BAGS-continue-button");
        try {
            esperar(2000);
            if (bagsButton.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = bagsButton.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(bagsButton));
                esperar(2000);
            }
        } catch (Exception ignored) {}

        // 6: Continuar al servicio de embarque (PRIORITY_BOARDING-continue-button)
        Target priorityButton = Target.the("Boton Ir al siguiente servicio (Priority)")
                .locatedBy("[data-testid='PRIORITY_BOARDING-continue-button'], #PRIORITY_BOARDING-continue-button");
        try {
            esperar(2000);
            if (priorityButton.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = priorityButton.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(priorityButton));
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
