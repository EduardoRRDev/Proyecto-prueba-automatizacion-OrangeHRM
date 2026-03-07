package co.com.proyecto.automatizacion.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Utilidades estáticas para aserciones en tests.
 * Cualquier step o page puede usar estos métodos sin herencia.
 */
public final class AssertionHelper {

    private AssertionHelper() {
    }

    /**
     * Verifica que el texto actual sea exactamente igual al esperado.
     *
     * @param message  mensaje mostrado si falla la aserción
     * @param actual   valor obtenido (ej. getText() de un elemento)
     * @param expected valor esperado
     */
    public static void assertTextEquals(String message, String actual, String expected) {
        assertThat(message, actual, is(equalTo(expected)));
    }

    /**
     * Verifica que el texto actual contenga el fragmento esperado.
     *
     * @param message   mensaje mostrado si falla la aserción
     * @param actual    texto completo
     * @param expected  fragmento que debe aparecer
     */
    public static void assertContains(String message, String actual, String expected) {
        assertThat(message, actual, containsString(expected));
    }
}
