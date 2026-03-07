package co.com.proyecto.automatizacion.util;

/**
 * Utilidades estáticas para esperas.
 * Cualquier step o page puede usar estos métodos sin herencia.
 */
public final class WaitHelper {

    private WaitHelper() {
    }

    /**
     * Pausa fija en milisegundos. Usar con moderación; preferir esperas explícitas
     * (waitUntilVisible, WebDriverWait) cuando sea posible.
     *
     * @param millis milisegundos a esperar
     */
    public static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
