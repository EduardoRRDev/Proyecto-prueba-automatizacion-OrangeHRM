package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.pages.login.LoginPage;
import co.com.proyecto.automatizacion.pages.common.MainPage;
import net.serenitybdd.annotations.Step;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Steps reutilizables para el flujo de inicio de sesión.
 * 
 * Los Steps son métodos marcados con @Step que contienen la lógica reutilizable
 * de interacción con la aplicación. Estos métodos pueden ser llamados desde
 * múltiples Step Definitions.
 * 
 * Ventajas de usar @Step:
 * - Serenity los registra automáticamente en los reportes
 * - Aparecen como pasos individuales con capturas de pantalla
 * - Son reutilizables entre diferentes Step Definitions
 * - Permiten composición de pasos más complejos
 * 
 * Serenity inyecta automáticamente las Page Objects (loginPage, mainPage, etc.)
 * cuando las declaras como campos privados.
 */

/**
 * Page Object para la página de login.
 * Serenity crea automáticamente una instancia cuando se necesita.
 * Contiene los elementos web mapeados (inputUsername, inputPassword, btnLogin).
 */
/**
 * Page Object para la página principal después del login.
 * Serenity crea automáticamente una instancia cuando se necesita.
 */
/**
 * Clase de interacción para la página principal.
 * Contiene métodos de alto nivel para interactuar con la página principal.
 */
/**
 * Clase de interacción para la página principal.
 * Contiene métodos de alto nivel para interactuar con la página principal.
 */

public class LoginSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSteps.class);
    private LoginPage loginPage;
    private MainPage mainPage;

    @Step("user open login page")
    public void openLoginPage() {
        try {
            // Abre la URL configurada en LoginPage (@DefaultUrl)
            loginPage.open();
            LOGGER.info("user open login page");
        } catch (Throwable t) {
            // Si el error es porque Chrome no está disponible, omite el escenario
            if (isDriverUnavailable(t)) {
                Assume.assumeNoException("Chrome no disponible; omitiendo escenario.", t);
            }
            // Si es otro error, lo relanza para que el test falle normalmente
            throw t;
        }
    }

    private static boolean isDriverUnavailable(Throwable t) {
        String msg = t.getMessage() != null ? t.getMessage() : "";
        Throwable cause = t;
        
        // Revisa la cadena de causas (cause.getCause()) para encontrar el error real
        while (cause != null) {
            if (cause.getMessage() != null && (
                cause.getMessage().contains("Chrome failed to start") ||
                cause.getMessage().contains("Could not instantiate") ||
                cause.getMessage().contains("SessionNotCreatedException")
            )) {
                return true;
            }
            cause = cause.getCause();
        }
        
        // También revisa el mensaje principal
        return msg.contains("Chrome failed to start") ||
               msg.contains("Could not instantiate") ||
               msg.contains("SessionNotCreated");
    }

    @Step("clear user and password fields")
    public void clearFieldsLogin() {
        // Espera a que los campos estén visibles antes de limpiar
        loginPage.inputUsername.withTimeoutOf(Duration.ofSeconds(15)).waitUntilVisible().type("");
        loginPage.inputPassword.withTimeoutOf(Duration.ofSeconds(10)).waitUntilVisible().type("");
    }

    @Step("enter credentials")
    public void enterCredentials(String username, String password) {
        loginPage.inputUsername.withTimeoutOf(Duration.ofSeconds(15)).waitUntilVisible().type(username);
        loginPage.inputPassword.withTimeoutOf(Duration.ofSeconds(10)).waitUntilVisible().type(password);
        
        // Registra las credenciales en el log (formato estructurado)
        LOGGER.atInfo()
              .setMessage("enter credentials with username:{} and password:{}.")
              .addArgument(username)
              .addArgument(password)
              .log();
    }

    @Step("access the system")
    public void accessSystem(){
        // Espera hasta 15 segundos a que el botón sea clickeable, luego hace clic
        loginPage.btnLogin
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilClickable()
            .click();
    }

    @Step("validate successful login")
    public void validateSuccessfulLogin() {
        final String mensajeError = "Login was unsuccessful.";
        
        // Espera y obtiene el título de la página principal (Dashboard de OrangeHRM)
        String titulo = mainPage.txtTitleMainPage
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible()
            .getText();
        
        // Valida que el título sea "Dashboard" (página principal de OrangeHRM)
        assertThat(mensajeError, titulo, is(equalTo("Dashboard")));
    }

}
