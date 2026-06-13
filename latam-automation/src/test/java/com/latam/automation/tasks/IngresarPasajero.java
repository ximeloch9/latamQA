package com.latam.automation.tasks;

import com.latam.automation.ui.LatamCheckoutPage;
import com.latam.automation.util.AutomationConfig;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.JavaScriptClick;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

public class IngresarPasajero implements Task {

    private static final Logger log = LoggerFactory.getLogger(IngresarPasajero.class);
    private final Map<String, String> datosUsuario;

    public IngresarPasajero(Map<String, String> datosUsuario) {
        this.datosUsuario = datosUsuario;
    }

    public static IngresarPasajero conDatos(Map<String, String> datosUsuario) {
        return instrumented(IngresarPasajero.class, datosUsuario);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // 1. Diligenciar Pasajero 1 (Adulto Acompañante - ADT_1)
        try {
            actor.attemptsTo(
                    WaitUntil.the(LatamCheckoutPage.firstNameInput("ADT_1"), isVisible()).forNoMoreThan(15).seconds()
            );
            esperar(1000);

            escribirComoHumano(actor, LatamCheckoutPage.firstNameInput("ADT_1"), "Juan");
            escribirComoHumano(actor, LatamCheckoutPage.lastNameInput("ADT_1"), "Perez");

            if (LatamCheckoutPage.documentTypeSelect("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                try {
                    actor.attemptsTo(
                            SelectFromOptions.byValue("CC").from(LatamCheckoutPage.documentTypeSelect("ADT_1"))
                    );
                    esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
                } catch (Exception ignored) {}
            }

            if (LatamCheckoutPage.documentNumberInput("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                escribirComoHumano(actor, LatamCheckoutPage.documentNumberInput("ADT_1"), "10203040");
            }

            if (LatamCheckoutPage.birthDateInput("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                escribirComoHumano(actor, LatamCheckoutPage.birthDateInput("ADT_1"), "15061990");
            }

            if (LatamCheckoutPage.emailInput("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                escribirComoHumano(actor, LatamCheckoutPage.emailInput("ADT_1"), "qatest@gmail.com");
            }

            if (LatamCheckoutPage.phoneInput("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                escribirComoHumano(actor, LatamCheckoutPage.phoneInput("ADT_1"), "3112223344");
            }

            esperar(1000);
            if (LatamCheckoutPage.submitPassengerForm("ADT_1").resolveFor(actor).isCurrentlyVisible()) {
                actor.attemptsTo(JavaScriptClick.on(LatamCheckoutPage.submitPassengerForm("ADT_1")));
                esperar(2000);
            }
        } catch (Exception e) {
            log.info("Formulario ADT_1 no disponible o ya pre-completado. Omitiendo.");
        }

        // 2. Diligenciar Pasajero 2 (Menor de Edad de la BD - CHD_1)
        try {
            // Intentar expandir el acordeón del Pasajero 2 si sus campos no están visibles
            if (!LatamCheckoutPage.firstNameInput("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
                if (LatamCheckoutPage.passengerHeader("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
                    actor.attemptsTo(JavaScriptClick.on(LatamCheckoutPage.passengerHeader("CHD_1")));
                    esperar(1500);
                }
            }

            actor.attemptsTo(
                    WaitUntil.the(LatamCheckoutPage.firstNameInput("CHD_1"), isVisible()).forNoMoreThan(15).seconds(),
                    WaitUntil.the(LatamCheckoutPage.firstNameInput("CHD_1"), net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isEnabled()).forNoMoreThan(10).seconds()
            );
        } catch (Exception ignored) {}

        String name = normalizarTexto(datosUsuario.get("name"));
        String lastName = normalizarTexto(datosUsuario.get("lastName"));
        String docId = datosUsuario.get("documentId");

        escribirComoHumano(actor, LatamCheckoutPage.firstNameInput("CHD_1"), name);
        escribirComoHumano(actor, LatamCheckoutPage.lastNameInput("CHD_1"), lastName);

        if (LatamCheckoutPage.documentTypeSelect("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
            try {
                actor.attemptsTo(
                        SelectFromOptions.byValue("TI").from(LatamCheckoutPage.documentTypeSelect("CHD_1"))
                );
                esperar(AutomationConfig.DELAY_HUMANO_CAMPO_MS);
            } catch (Exception ignored) {}
        }

        if (LatamCheckoutPage.documentNumberInput("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
            escribirComoHumano(actor, LatamCheckoutPage.documentNumberInput("CHD_1"), docId);
        }

        if (LatamCheckoutPage.birthDateInput("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
            // Enviamos la fecha como números. Si hay problemas de máscara, enviarla con guiones
            escribirComoHumano(actor, LatamCheckoutPage.birthDateInput("CHD_1"), "15062020");
        }

        if (LatamCheckoutPage.emailInput("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
            escribirComoHumano(actor, LatamCheckoutPage.emailInput("CHD_1"), "qatest@gmail.com");
        }

        if (LatamCheckoutPage.phoneInput("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
            escribirComoHumano(actor, LatamCheckoutPage.phoneInput("CHD_1"), "3112223344");
        }

        esperar(1000);
        try {
            if (LatamCheckoutPage.submitPassengerForm("CHD_1").resolveFor(actor).isCurrentlyVisible()) {
                actor.attemptsTo(JavaScriptClick.on(LatamCheckoutPage.submitPassengerForm("CHD_1")));
                esperar(2000);
            }
        } catch (Exception ignored) {}

        // 3. Clic final en el botón de Continuar general
        try {
            esperar(2000);
            if (LatamCheckoutPage.CONTINUAR_BUTTON.resolveFor(actor).isCurrentlyVisible()) {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                WebElement element = LatamCheckoutPage.CONTINUAR_BUTTON.resolveFor(actor);
                
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                    esperar(1000);
                } catch (Exception ignored) {}

                log.info("Haciendo clic en el botón Continuar general (data-testid='undefined-button')");
                actor.attemptsTo(JavaScriptClick.on(LatamCheckoutPage.CONTINUAR_BUTTON));
                
                // Continuar sin asistencia si se presenta la oferta
                try {
                    esperar(3000);
                    Target botonSinAsistencia = Target.the("Boton Continuar sin asistencia")
                            .locatedBy("[data-testid='forced-choice-not-add-offer-button--button']");
                    if (botonSinAsistencia.resolveFor(actor).isCurrentlyVisible()) {
                        log.info("Modal de oferta de asistencia detectado. Haciendo clic en 'Continuar sin asistencia'...");
                        actor.attemptsTo(JavaScriptClick.on(botonSinAsistencia));
                    }
                } catch (Exception ignored) {}
                
                // Esperar a que se abra la pantalla de pago y realizar scroll final
                esperar(6000);
                try {
                    log.info("Realizando scroll en la pantalla de pago...");
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo({top: 500, behavior: 'smooth'});");
                    esperar(2000);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            log.warn("Error al intentar proceder al pago o realizar el scroll final: {}", e.getMessage());
        }
    }

    private void escribirComoHumano(Actor actor, Target target, String texto) {
        try {
            WebElementFacade element = target.resolveFor(actor);
            
            // Scroll suave del elemento al centro antes de interactuar
            try {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                esperar(500);
            } catch (Exception scrollEx) {
                // Silencioso
            }

            // Validar si es editable o de solo lectura
            String readonlyAttr = element.getAttribute("readonly");
            String disabledAttr = element.getAttribute("disabled");
            if ((readonlyAttr != null && !readonlyAttr.isEmpty() && !readonlyAttr.equals("false")) ||
                (disabledAttr != null && !disabledAttr.isEmpty() && !disabledAttr.equals("false"))) {
                log.info("Campo {} es de solo lectura. Omitiendo.", target.getName());
                return;
            }

            try {
                element.click();
            } catch (Exception clickEx) {
                actor.attemptsTo(JavaScriptClick.on(target));
            }
            esperar(500);
            
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
            log.warn("No se pudo escribir como humano en {}: {}", target.getName(), e.getMessage());
        }
    }

    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9 ]", "");
    }

    private void schedulerWait(long milis) {
        esperar(milis);
    }

    private void esperar(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
