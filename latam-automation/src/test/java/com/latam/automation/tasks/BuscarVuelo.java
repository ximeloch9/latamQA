package com.latam.automation.tasks;

import com.latam.automation.ui.LatamSearchPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class BuscarVuelo implements Task {

    private final String origen;
    private final String destino;
    private final boolean soloIda;

    public BuscarVuelo(String origen, String destino, boolean soloIda) {
        this.origen = origen;
        this.destino = destino;
        this.soloIda = soloIda;
    }

    public static BuscarVuelo conParametros(String origen, String destino, boolean soloIda) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda);
    }

    private String buildDismissScript() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("overlays.properties")) {
            if (in == null) {
                return "/* overlays.properties no encontrado */";
            }
            props.load(in);
        } catch (IOException e) {
            return "/* Error al leer overlays.properties */";
        }
        
        String selectors = props.getProperty("overlay.selectors", "");
        StringBuilder js = new StringBuilder();

        // 1. Ocultamiento de banners pasivos
        js.append("try {");
        if (!selectors.isBlank()) {
            for (String sel : selectors.split(",")) {
                String s = sel.trim();
                if (!s.isEmpty()) {
                    js.append("var el=document.querySelector('").append(s.replace("'", "\\'")).append("');")
                      .append("if(el)el.style.display='none';");
                }
            }
        }
        js.append("} catch(e) {}");

        // 2. Diálogos interactivos con clics independientes en el botón ancestro
        
        // 2a. Selector de región/idioma
        js.append("try {")
          .append("var b=document.querySelector('#country-lang-selector-continue-button');")
          .append("if(b)b.click();")
          .append("} catch(e) {}");

        // 2b. Sugerencia de país/ubicación
        js.append("try {")
          .append("var b=document.querySelector('#country-suggestion--dialog__close-button');")
          .append("if(b)b.click();")
          .append("} catch(e) {}");

        // 2c. Incentivo de inicio de sesión
        js.append("try {")
          .append("var b=document.querySelector('#button-close-login-incentive');")
          .append("if(b)b.click();")
          .append("} catch(e) {}");

        return js.toString();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Descartar cookies y overlays molestos antes de interactuar
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            
            js.executeScript(buildDismissScript());
            
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("#country-lang-selector-continue-button, " +
                                    "#country-suggestion--dialog__close-button, " +
                                    "#button-close-login-incentive")
                ));
            } catch (Exception waitEx) {
                // Silencioso: alguno de los diálogos puede no haber aparecido
            }
        } catch (Exception e) {
            // Silencioso
        }

        if (soloIda) {
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ONE_WAY_RADIO));
        } else {
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ROUND_TRIP_RADIO));
        }

        // Ingresar Origen
        actor.attemptsTo(
                JavaScriptClick.on(LatamSearchPage.ORIGIN_INPUT),
                Enter.theValue(origen).into(LatamSearchPage.ORIGIN_INPUT)
        );
        actor.attemptsTo(
                WaitUntil.the(LatamSearchPage.ORIGIN_AUTOCOMPLETE_OPTION, isVisible()).forNoMoreThan(8).seconds(),
                JavaScriptClick.on(LatamSearchPage.ORIGIN_AUTOCOMPLETE_OPTION)
        );

        // Ingresar Destino
        actor.attemptsTo(
                JavaScriptClick.on(LatamSearchPage.DESTINATION_INPUT),
                Enter.theValue(destino).into(LatamSearchPage.DESTINATION_INPUT)
        );
        actor.attemptsTo(
                WaitUntil.the(LatamSearchPage.DESTINATION_AUTOCOMPLETE_OPTION, isVisible()).forNoMoreThan(8).seconds(),
                JavaScriptClick.on(LatamSearchPage.DESTINATION_AUTOCOMPLETE_OPTION)
        );

        // Enviar busqueda
        actor.attemptsTo(
                JavaScriptClick.on(LatamSearchPage.SEARCH_BUTTON)
        );
    }
}
