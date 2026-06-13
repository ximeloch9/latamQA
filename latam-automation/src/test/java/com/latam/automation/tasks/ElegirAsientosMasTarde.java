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

public class ElegirAsientosMasTarde implements Task {

    public static ElegirAsientosMasTarde enElMapa() {
        return instrumented(ElegirAsientosMasTarde.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 3: Click en "Quiero elegir asientos despues" (button-seatmap-leave)
        Target leaveSeatmapButton = Target.the("Boton Quiero elegir asientos despues")
                .locatedBy("[data-testid='button-seatmap-leave'], #btnSeatMapLeave");
        try {
            esperar(2000);
            if (leaveSeatmapButton.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = leaveSeatmapButton.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(leaveSeatmapButton));
                esperar(2000);
            }
        } catch (Exception ignored) {}

        // 4: Elegir asiento aleatorio en la ventana/modal emergente (buttonChooseLater--button)
        Target chooseLaterButton = Target.the("Boton Prefiero uno Aleatorio")
                .locatedBy("[data-testid='buttonChooseLater--button'], #buttonChooseLater");
        try {
            esperar(2000);
            if (chooseLaterButton.resolveFor(actor).isCurrentlyVisible()) {
                actor.attemptsTo(JavaScriptClick.on(chooseLaterButton));
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
