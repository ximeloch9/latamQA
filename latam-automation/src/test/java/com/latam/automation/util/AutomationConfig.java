package com.latam.automation.util;

/**
 * Configuración centralizada de tiempos de espera y constantes de
 * automatización.
 * Modificar aquí para ajustar comportamiento en todos los scripts sin buscar
 * valores dispersos.
 */
public final class AutomationConfig {

    private AutomationConfig() {
    }

    // ── Esperas de formulario de búsqueda (simulan comportamiento humano)
    // ──────────
    /** Pausa tras seleccionar el tipo de viaje antes de continuar al origen */
    public static final long DELAY_TIPO_VIAJE_MS = 800;

    /** Pausa tras seleccionar la opción del autocompletar de origen o destino */
    public static final long DELAY_AUTOCOMPLETE_MS = 1000;

    /** Pausa tras seleccionar cada fecha antes de continuar */
    public static final long DELAY_FECHA_MS = 1200;

    /** Rango mínimo de retardo entre pulsaciones de teclas (en ms) para simular escritura humana */
    public static final long MIN_DELAY_TECLA_MS = 80;

    /** Rango máximo de retardo entre pulsaciones de teclas (en ms) para simular escritura humana */
    public static final long MAX_DELAY_TECLA_MS = 180;

    /** Retardo de pausa reflexiva/decisión (en ms) después de completar un campo o interacción */
    public static final long DELAY_HUMANO_CAMPO_MS = 1200;

    // ── Esperas de carga de página
    // ────────────────────────────────────────────────
    /** Segundos máximos para que se abra la nueva pestaña de resultados */
    public static final int TIMEOUT_NUEVA_PESTANA_SEG = 25;

    /** Pausa inicial tras cambiar a la nueva pestaña (carga del DOM base) */
    public static final long DELAY_NUEVA_PESTANA_MS = 3000;

    /** Segundos máximos para que aparezca el primer vuelo en resultados */
    public static final int TIMEOUT_PRIMER_VUELO_SEG = 30;

    // ── Reintentos por pantalla de error de LATAM
    // ─────────────────────────────────
    /**
     * Número máximo de reintentos al detectar "La búsqueda está tardando más de lo
     * normal"
     */
    public static final int MAX_REINTENTOS_ERROR_BUSQUEDA = 1;

    /** Segundos a esperar antes de realizar el reintento */
    public static final long DELAY_REINTENTO_BUSQUEDA_MS = 5000;
}
