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

public class BuscarVuelo implements Task {

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

    public static BuscarVuelo conParametrosYFechas(String origen, String destino, boolean soloIda, String fechaIda, String fechaVuelta) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda, fechaIda, fechaVuelta, false);
    }

    public static BuscarVuelo conParametrosYFechasYPasajeros(String origen, String destino, boolean soloIda, String fechaIda, String fechaVuelta, boolean incluirNino) {
        return instrumented(BuscarVuelo.class, origen, destino, soloIda, fechaIda, fechaVuelta, incluirNino);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        descartarOverlaysIniciales(actor);
        seleccionarTipoViaje(actor);
        ingresarRutaViaje(actor);
        seleccionarFechas(actor);
        
        if (incluirNino) {
            agregarPasajeroNino(actor);
        }
        
        ejecutarBusquedaYCambiarFoco(actor);
    }

    private void descartarOverlaysIniciales(Actor actor) {
        esperar(1000);
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(buildDismissScript());
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("#country-lang-selector-continue-button, " +
                            "#country-suggestion--dialog__close-button, " +
                            "#button-close-login-incentive")));
        } catch (Exception ignored) {
            // Manejo silencioso de overlays opcionales
        }
    }

    private void seleccionarTipoViaje(Actor actor) {
        Target radioTipo = soloIda ? LatamSearchPage.ONE_WAY_RADIO : LatamSearchPage.ROUND_TRIP_RADIO;
        actor.attemptsTo(JavaScriptClick.on(radioTipo));
        esperar(AutomationConfig.DELAY_TIPO_VIAJE_MS);
    }

    private void ingresarRutaViaje(Actor actor) {
        // Ingresar Origen
        actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.ORIGIN_INPUT));
        escribirComoHumano(actor, LatamSearchPage.ORIGIN_INPUT, origen);
        seleccionarOpcionAutocompletar(actor, LatamSearchPage.ORIGIN_AUTOCOMPLETE_OPTION);

        // Ingresar Destino
        actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.DESTINATION_INPUT));
        escribirComoHumano(actor, LatamSearchPage.DESTINATION_INPUT, destino);
        seleccionarOpcionAutocompletar(actor, LatamSearchPage.DESTINATION_AUTOCOMPLETE_OPTION);
    }

    private void seleccionarOpcionAutocompletar(Actor actor, Target targetOption) {
        actor.attemptsTo(
                WaitUntil.the(targetOption, isVisible()).forNoMoreThan(8).seconds(),
                Click.on(targetOption)
        );
        esperar(AutomationConfig.DELAY_AUTOCOMPLETE_MS);
    }

    private void seleccionarFechas(Actor actor) {
        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
        String fIda = (this.fechaIda != null) ? this.fechaIda : LocalDate.now().plusDays(7).format(iso);
        String fVuelta = (this.fechaVuelta != null) ? this.fechaVuelta : LocalDate.now().plusDays(14).format(iso);

        seleccionarFechaEnCalendario(actor, fIda, LatamSearchPage.DEPARTURE_DATE_INPUT);
        esperar(AutomationConfig.DELAY_FECHA_MS);
        
        if (!soloIda) {
            seleccionarFechaEnCalendario(actor, fVuelta, LatamSearchPage.RETURN_DATE_INPUT);
            esperar(AutomationConfig.DELAY_FECHA_MS);
        }
    }

    private void agregarPasajeroNino(Actor actor) {
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

    private void ejecutarBusquedaYCambiarFoco(Actor actor) {
        actor.attemptsTo(JavaScriptClick.on(LatamSearchPage.SEARCH_BUTTON));
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
            ((JavascriptExecutor) driver).executeScript("return document.readyState");
            esperar(AutomationConfig.DELAY_NUEVA_PESTANA_MS);
            dismissarOverlaysEnResultados(driver);
        } catch (Exception ignored) {
            // Manejo silencioso de fallo al cambiar pestaña
        }
    }

    private void dismissarOverlaysEnResultados(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            esperar(2000);
            js.executeScript(
                    "try { var c=document.querySelector('#onetrust-accept-btn-handler,[id^=onetrust-accept]'); if(c)c.click(); } catch(e) {}" +
                            "try { var b=document.querySelector('[data-testid*=currency-dialog] button,[data-testid*=keep-currency],[data-testid*=continue-currency],[id*=currency-continue],[class*=CurrencyModal] button'); if(b)b.click(); } catch(e) {}" +
                            "try { var b=document.querySelector('#country-lang-selector-continue-button'); if(b)b.click(); } catch(e) {}" +
                            "try { var b=document.querySelector('#country-suggestion--dialog__close-button'); if(b)b.click(); } catch(e) {}" +
                            "try { var b=document.querySelector('#button-close-login-incentive'); if(b)b.click(); } catch(e) {}" +
                            "try { var b=document.querySelector('[aria-label=Close],[aria-label*=cerrar],[aria-label*=Cerrar]'); if(b)b.click(); } catch(e) {}"
            );
        } catch (Exception ignored) {
            // Silencioso
        }
    }

    private void escribirComoHumano(Actor actor, Target target, String texto) {
        try {
            WebElementFacade element = target.resolveFor(actor);
            
            try {
                element.click();
            } catch (Exception clickEx) {
                actor.attemptsTo(JavaScriptClick.on(target));
            }
            esperar(500);
            
            // Limpiar el campo usando acordes de teclado en vez de .clear(), para que React detecte el cambio de estado.
            // Se envían comandos de selección para Windows/Linux (CONTROL) y macOS (COMMAND).
            element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
            element.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.COMMAND, "a"));
            element.sendKeys(org.openqa.selenium.Keys.BACK_SPACE);
            esperar(500);

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
}