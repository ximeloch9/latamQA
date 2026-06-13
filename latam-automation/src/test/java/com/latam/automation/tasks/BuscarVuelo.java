package com.latam.automation.tasks;

import com.latam.automation.ui.LatamSearchPage;
import com.latam.automation.util.AutomationConfig;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.core.pages.WebElementFacade;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

public class BuscarVuelo implements Task {

    private static final Target ORIGIN_AUTOCOMPLETE_OPTION = Target.the("primera opción origen")
            .locatedBy("//*[contains(@data-testid,'--autocomplete__listitem--menuitem__label-content')][1]");

    private static final Target DESTINATION_AUTOCOMPLETE_OPTION = Target.the("primera opción destino")
            .locatedBy("//*[contains(@data-testid,'--autocomplete__listitem--menuitem__label-content')][1]");

    private final String origen;
    private final String destino;
    private final boolean soloIda;
    private final String fechaIda;
    private final String fechaVuelta;
    private final boolean incluirNino;

    public BuscarVuelo(String origen, String destino, boolean soloIda, String fechaIda, String fechaVuelta, boolean incluirNino) {
        this.origen = origen;
        this.destino = destino;
        this.soloIda = soloIda;
        this.fechaIda = fechaIda;
        this.fechaVuelta = fechaVuelta;
        this.incluirNino = incluirNino;
    }

    public BuscarVuelo(String origen, String destino, boolean soloIda, String fechaIda, String fechaVuelta) {
        this(origen, destino, soloIda, fechaIda, fechaVuelta, false);
    }

    public BuscarVuelo(String origen, String destino, boolean soloIda) {
        this(origen, destino, soloIda, null, null, false);
    }

    public static BuscarVuelo conParametros(String origen, String destino, boolean soloIda) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda, null, null, false);
    }

    public static BuscarVuelo conParametrosYFechas(String origen, String destino, boolean soloIda, String fechaIda,
            String fechaVuelta) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda, fechaIda, fechaVuelta, false);
    }

    public static BuscarVuelo conParametrosYFechasYPasajeros(String origen, String destino, boolean soloIda, String fechaIda,
            String fechaVuelta, boolean incluirNino) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda, fechaIda, fechaVuelta, incluirNino);
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

        // Intentar hacer CLICK real en el botón de aceptar cookies para que se guarden las cookies de consentimiento en el navegador
        js.append("try {");
        js.append("var acceptBtn=document.querySelector('#onetrust-accept-btn-handler, [id^=\"onetrust-accept\"], #btn-cookie-accept');");
        js.append("if(acceptBtn){acceptBtn.click();}");
        js.append("} catch(e) {}");

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

        js.append("try {")
                .append("var b=document.querySelector('#country-lang-selector-continue-button');")
                .append("if(b)b.click();")
                .append("} catch(e) {}");

        js.append("try {")
                .append("var b=document.querySelector('#country-suggestion--dialog__close-button');")
                .append("if(b)b.click();")
                .append("} catch(e) {}");

        js.append("try {")
                .append("var b=document.querySelector('#button-close-login-incentive');")
                .append("if(b)b.click();")
                .append("} catch(e) {}");

        return js.toString();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;

            js.executeScript(buildDismissScript());

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("#country-lang-selector-continue-button, " +
                                "#country-suggestion--dialog__close-button, " +
                                "#button-close-login-incentive")));
            } catch (Exception waitEx) {
                // Silencioso
            }
        } catch (Exception e) {
            // Silencioso
        }

        if (soloIda) {
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ONE_WAY_RADIO));
        } else {
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ROUND_TRIP_RADIO));
        }
        esperar(AutomationConfig.DELAY_TIPO_VIAJE_MS);

        // Ingresar Origen
        actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ORIGIN_INPUT));
        escribirComoHumano(actor, LatamSearchPage.ORIGIN_INPUT, origen);
        
        // Espera condicional: continúa tan pronto como el autocompletar sea visible
        actor.attemptsTo(
                WaitUntil.the(ORIGIN_AUTOCOMPLETE_OPTION, isVisible()).forNoMoreThan(8).seconds());
        actor.attemptsTo(
                JavaScriptClick.on(ORIGIN_AUTOCOMPLETE_OPTION));
        // Espera condicional: continúa tan pronto como el autocompletar desaparezca (opción seleccionada)
        actor.attemptsTo(
                WaitUntil.the(ORIGIN_AUTOCOMPLETE_OPTION, isNotVisible()).forNoMoreThan(5).seconds());
        esperar(AutomationConfig.DELAY_AUTOCOMPLETE_MS);

        // Ingresar Destino
        actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.DESTINATION_INPUT));
        escribirComoHumano(actor, LatamSearchPage.DESTINATION_INPUT, destino);
        
        actor.attemptsTo(
                WaitUntil.the(DESTINATION_AUTOCOMPLETE_OPTION, isVisible()).forNoMoreThan(8).seconds());
        actor.attemptsTo(
                JavaScriptClick.on(DESTINATION_AUTOCOMPLETE_OPTION));
        actor.attemptsTo(
                WaitUntil.the(DESTINATION_AUTOCOMPLETE_OPTION, isNotVisible()).forNoMoreThan(5).seconds());
        esperar(AutomationConfig.DELAY_AUTOCOMPLETE_MS);

        // Fechas
        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
        String fIda = (this.fechaIda != null) ? this.fechaIda : LocalDate.now().plusDays(7).format(iso);
        String fVuelta = (this.fechaVuelta != null) ? this.fechaVuelta : LocalDate.now().plusDays(14).format(iso);

        seleccionarFechaEnCalendario(actor, fIda, LatamSearchPage.DEPARTURE_DATE_INPUT);
        esperar(AutomationConfig.DELAY_FECHA_MS);
        if (!soloIda) {
            seleccionarFechaEnCalendario(actor, fVuelta, LatamSearchPage.RETURN_DATE_INPUT);
            esperar(AutomationConfig.DELAY_FECHA_MS);
        }

        if (incluirNino) {
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.PASSENGERS_TRIGGER));
            esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
            try {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElementFacade element = LatamSearchPage.CHILDREN_ADD_BUTTON.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(1000);
                actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.CHILDREN_ADD_BUTTON));
                esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
            } catch (Exception e) {
                actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.CHILDREN_ADD_BUTTON));
                esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
            }
            actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.PASSENGERS_TRIGGER));
            esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
        }

        actor.attemptsTo(
                JavaScriptClick.on(LatamSearchPage.SEARCH_BUTTON));

        // Esperar a que abra la nueva pestaña y cambiar foco
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(AutomationConfig.TIMEOUT_NUEVA_PESTANA_SEG));
            wait.until(d -> d.getWindowHandles().size() > 1);

            String nuevaPestana = driver.getWindowHandles()
                    .stream()
                    .filter(h -> !h.equals(driver.getWindowHandle()))
                    .findFirst()
                    .orElseThrow();

            driver.switchTo().window(nuevaPestana);

            // Forzar a Serenity a reconocer la nueva pestaña
            ((JavascriptExecutor) driver).executeScript("return document.readyState");
            esperar(AutomationConfig.DELAY_NUEVA_PESTANA_MS);

        } catch (Exception ignored) {
            // Silencioso
        }
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

    private void seleccionarFechaEnCalendario(Actor actor, String fechaISO, Target inputCampo) {
        actor.attemptsTo(Click.on(inputCampo));
        esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);

        Target botonFecha = LatamSearchPage.botonFechaCalendario(fechaISO);

        int intentosMaximos = 12;
        while (!botonFecha.resolveFor(actor).isCurrentlyVisible() && intentosMaximos > 0) {
            if (LatamSearchPage.NEXT_MONTH_BUTTON.resolveFor(actor).isCurrentlyVisible()) {
                actor.attemptsTo(Click.on(LatamSearchPage.NEXT_MONTH_BUTTON));
                esperar(500);
            } else {
                break;
            }
            intentosMaximos--;
        }

        actor.attemptsTo(Click.on(botonFecha));
    }
}