package com.latam.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LatamCheckoutPage {

    public static final Target CHEAPEST_FLIGHT = Target.the("Tarifa mas economica de la lista de vuelos")
            .locatedBy("//*[@data-testid='wrapper-card-header-0']");

    public static final Target CABIN_TARIF_SELECT = Target.the("seleccionar tarifa")
            .locatedBy("//*[@data-testid='bundle-detail-0-flight-select']");

    // Selectores dinámicos basados en el sufijo del pasajero (ej. ADT_1, CHD_1)
    public static Target firstNameInput(String suffix) {
        return Target.the("Campo Nombre del Pasajero " + suffix)
                .located(By.id("passengerDetails-firstName-" + suffix));
    }

    public static Target lastNameInput(String suffix) {
        return Target.the("Campo Apellido del Pasajero " + suffix)
                .located(By.id("passengerDetails-lastName-" + suffix));
    }

    public static Target documentTypeSelect(String suffix) {
        return Target.the("Select de Tipo de Documento del Pasajero " + suffix)
                .located(By.xpath("//select[contains(@id, 'documentType') or contains(@id, 'document-type') or contains(@id, 'document')] [contains(@id, '" + suffix + "')]"));
    }

    public static Target documentNumberInput(String suffix) {
        return Target.the("Campo Numero de Documento del Pasajero " + suffix)
                .located(By.id("documentInfo-documentNumber-" + suffix));
    }

    public static Target birthDateInput(String suffix) {
        return Target.the("Campo Fecha de Nacimiento del Pasajero " + suffix)
                .located(By.id("passengerInfo-dateOfBirth-" + suffix));
    }

    public static Target emailInput(String suffix) {
        return Target.the("Campo Email del Pasajero " + suffix)
                .located(By.id("passengerInfo-emails-" + suffix));
    }

    public static Target phoneInput(String suffix) {
        return Target.the("Campo Telefono del Pasajero " + suffix)
                .located(By.id("passengerInfo-phones0-number-" + suffix));
    }

    public static Target submitPassengerForm(String suffix) {
        return Target.the("Boton guardar o continuar del Pasajero " + suffix)
                .located(By.id("passengerFormSubmitButton" + suffix));
    }

    public static Target passengerHeader(String suffix) {
        return Target.the("Acordeon o Cabecera del Pasajero " + suffix)
                .located(By.xpath("//*[contains(@id, 'passenger-') or contains(@id, 'accordion') or contains(@id, 'header') or contains(@class, 'header')][contains(@id, '" + suffix + "') or contains(@class, '" + suffix + "')]"));
    }

    // Botón general de "Continuar" para avanzar a la pantalla de pago (data-testid='undefined-button')
    public static final Target CONTINUAR_BUTTON = Target.the("Boton general Continuar a Pago")
            .located(By.cssSelector("[data-testid='undefined-button'], button[id*='continue'], button[class*='Continue']"));

    public static final Target ERROR_MESSAGES = Target.the("Mensajes de error en el formulario")
            .located(By.cssSelector(".error-message, [class*='ErrorMessage'], .invalid-feedback, [class*='invalid']"));

    public static final Target PAYMENT_HEADER = Target.the("Cabecera o titulo de Pago")
            .locatedBy("//h1[contains(.,'pago') or contains(.,'Pago') or contains(.,'Paga') or contains(.,'paga') or contains(.,'Pagar') or contains(.,'confirmar') or contains(.,'Resumen') or contains(@id,'payment')]");
}
