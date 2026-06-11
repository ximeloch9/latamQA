package com.latam.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LatamCheckoutPage {

    public static final Target CHEAPEST_FLIGHT = Target.the("Tarifa mas economica de la lista de vuelos")
            .located(By.xpath("(//span[contains(text(), 'COP') or contains(text(), 'USD') or contains(text(), '$')])[1] | //button[contains(@class, 'FlightCard')]"));

    public static final Target CABIN_TARIF_SELECT = Target.the("Boton seleccionar tarifa cabina")
            .located(By.xpath("//button[contains(@id, 'tarif') or contains(@id, 'select-flight') or contains(text(), 'Seleccionar')]"));

    public static final Target FIRST_NAME_INPUT = Target.the("Campo Nombre del Pasajero")
            .located(By.cssSelector("[data-testid='passengerDetails-firstName-ADT_1-textfield-input']"));

    public static final Target LAST_NAME_INPUT = Target.the("Campo Apellido del Pasajero")
            .located(By.cssSelector("[data-testid*='passengerDetails-lastName-ADT_1']"));

    public static final Target DOCUMENT_TYPE_SELECT = Target.the("Select de Tipo de Documento")
            .located(By.xpath("//select[contains(@name, 'documentType') or contains(@id, 'document-type')]"));

    public static final Target DOCUMENT_NUMBER_INPUT = Target.the("Campo Numero de Documento")
            .located(By.xpath("//input[contains(@name, 'documentNumber') or contains(@id, 'document-number') or contains(@name, 'dni')]"));

    public static final Target SUBMIT_PASSENGER_FORM = Target.the("Boton guardar o continuar en checkout")
            .located(By.cssSelector("[data-testid='passengerFormSubmitButtonADT_1']"));
}
