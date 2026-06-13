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

public class SeleccionarTarifaReembolsable implements Task {

    public static SeleccionarTarifaReembolsable deVuelo() {
        return instrumented(SeleccionarTarifaReembolsable.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 1: Elegir siguiente botón de tarifa (next-brand-button)
        Target nextBrandButton = Target.the("Boton Siguiente Tarifa")
                .locatedBy("[data-testid='next-brand-button']");
        try {
            esperar(2000);
            if (nextBrandButton.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = nextBrandButton.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(nextBrandButton));
                esperar(2000);
            }
        } catch (Exception ignored) {}

        // 2: Scroll y click en "Continuar con opción reembolsable" (button10--button)
        Target button10 = Target.the("Boton Continuar con opcion reembolsable")
                .locatedBy("[data-testid='button10--button']");
        try {
            esperar(2000);
            if (button10.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = button10.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(button10));
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
