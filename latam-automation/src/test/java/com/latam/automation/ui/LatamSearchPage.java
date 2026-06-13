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
            .located(By.cssSelector("[data-testid*='origin--autocomplete'] [data-testid*='autocomplete__listitem']"));

    public static final Target DESTINATION_AUTOCOMPLETE_OPTION = Target.the("Primera opcion de autocompletado de destino")
            .located(By.cssSelector("[data-testid*='destination--autocomplete'] [data-testid*='autocomplete__listitem']"));


    public static final Target CLOSE_LOGIN_INCENTIVE = Target.the("Boton cerrar incentivo de inicio de sesion")
            .located(By.id("button-close-login-incentive"));

    public static final Target REGIONAL_CONTINUE_BUTTON = Target.the("Boton continuar selector regional de idioma/pais")
            .located(By.id("country-lang-selector-continue-button"));

    public static final Target REGIONAL_CANCEL_BUTTON = Target.the("Boton cancelar selector regional de idioma/pais")
            .located(By.id("country-lang-selector-cancel-button"));

    public static final Target COUNTRY_SUGGESTION_CLOSE = Target.the("Boton cerrar sugerencia de pais/ubicacion")
            .located(By.id("country-suggestion--dialog__close-button"));

    public static final Target GENERIC_DIALOG_CLOSE_BUTTON = Target.the("Boton generico de cierre de dialogo o modal")
            .located(By.cssSelector("button[class*='close'], [class*='CloseButton'], [data-testid*='close'], [aria-label='Close'], [aria-label*='cerrar'], [aria-label*='Cerrar'], .dialog__close-button"));


    public static final Target DEPARTURE_DATE_INPUT = Target.the("Campo fecha de ida")
            .located(By.id("fsb-departure--text-field"));

    public static final Target RETURN_DATE_INPUT = Target.the("Campo fecha de vuelta")
            .located(By.id("fsb-return--text-field"));

    public static final Target NEXT_MONTH_BUTTON = Target.the("Boton siguiente mes del calendario")
            .located(By.xpath("//button[contains(@data-testid, 'calendar-next-month-button') or contains(@class, 'next-month') or contains(@aria-label, 'Siguiente mes') or @data-testid='calendar-navigation-button-next']"));

    public static Target botonFechaCalendario(String fechaISO) {
        return Target.the("Boton de fecha " + fechaISO)
                .located(By.cssSelector("[data-testid='date-" + fechaISO + "']"));
    }

    public static final Target PASSENGERS_TRIGGER = Target.the("Selector cantidad de pasajeros")
            .located(By.id("fsb-passengers--text-field"));

    public static final Target ADULT_PASSENGERS_ADD = Target.the("Boton agregar adulto")
            .located(By.xpath("//button[contains(@data-testid, 'add-passenger-adult') or contains(@id, 'add-passenger-adult')]"));

    public static final Target CHILDREN_ADD_BUTTON = Target.the("Boton agregar niño")
            .located(By.cssSelector("[data-testid='fsb-children-selector-add']"));

    public static final Target CABIN_SELECT = Target.the("Selector tipo cabina")
            .located(By.id("fsb-cabin--select"));

    public static final Target SEARCH_BUTTON = Target.the("Boton Buscar Vuelos")
            .located(By.cssSelector("[data-testid='fsb-search-flights--button']"));

    public static final Target CURRENT_CURRENCY = Target.the("Moneda actual en el header")
            .located(By.id("header__currentCurrency"));

    public static final Target REGIONAL_SELECTOR = Target.the("Selector de ciudad/region en el header")
            .located(By.id("cityselector--selector"));
}
