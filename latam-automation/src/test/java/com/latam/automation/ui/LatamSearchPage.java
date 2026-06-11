package com.latam.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LatamSearchPage {

    public static final String URL = "https://www.latamairlines.com/co/es";

    public static final Target ONE_WAY_RADIO = Target.the("Radio button Solo Ida")
            .located(By.id("fsb-one-way"));

    public static final Target ROUND_TRIP_RADIO = Target.the("Radio button Ida y Vuelta")
            .located(By.id("fsb-round-trip"));

    public static final Target ORIGIN_INPUT = Target.the("Campo Ciudad Origen")
            .located(By.id("fsb-origin--text-field"));

    public static final Target DESTINATION_INPUT = Target.the("Campo Ciudad Destino")
            .located(By.id("fsb-destination--text-field"));

    public static final Target ORIGIN_AUTOCOMPLETE_OPTION = Target.the("Primera opcion de autocompletado de origen")
            .located(By.xpath("//ul[@id='fsb-origin--autocomplete__list']/li | //li[contains(@id, 'autocomplete__listitem')] | //*[contains(@class, 'autocomplete__listitem')]"));

    public static final Target DESTINATION_AUTOCOMPLETE_OPTION = Target.the("Primera opcion de autocompletado de destino")
            .located(By.xpath("//ul[@id='fsb-destination--autocomplete__list']/li | //li[contains(@id, 'autocomplete__listitem')] | //*[contains(@class, 'autocomplete__listitem')]"));

    public static final Target DEPARTURE_DATE_INPUT = Target.the("Campo fecha de ida")
            .located(By.id("fsb-departure--text-field"));

    public static final Target RETURN_DATE_INPUT = Target.the("Campo fecha de vuelta")
            .located(By.id("fsb-return--text-field"));

    public static final Target PASSENGERS_TRIGGER = Target.the("Selector cantidad de pasajeros")
            .located(By.id("fsb-passengers--text-field"));

    public static final Target ADULT_PASSENGERS_ADD = Target.the("Boton agregar adulto")
            .located(By.xpath("//button[contains(@data-testid, 'add-passenger-adult') or contains(@id, 'add-passenger-adult')]"));

    public static final Target CABIN_SELECT = Target.the("Selector tipo cabina")
            .located(By.id("fsb-cabin--select"));

    public static final Target SEARCH_BUTTON = Target.the("Boton Buscar Vuelos")
            .located(By.cssSelector("[data-testid='fsb-search-flights--button']"));
}
