package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.config.Paths;
import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.pages.common.MainPage;
import co.com.proyecto.automatizacion.pages.login.LoginPage;
import co.com.proyecto.automatizacion.util.AssertionHelper;
import net.serenitybdd.annotations.Step;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Steps para el flujo de inicio de sesión.
 * Responsabilidad: navegar al login, ingresar credenciales y validar acceso.
 */
public class LoginSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSteps.class);

    private LoginPage loginPage;
    private MainPage mainPage;

    @Step("abre la página de login")
    public void openLoginPage() {
        try {
            loginPage.openAt(TestConfig.getBaseUrl() + Paths.LOGIN);
            LOGGER.info("user open login page");
        } catch (Throwable t) {
            if (isChromeUnavailable(t)) {
                Assume.assumeNoException("Chrome no disponible; omitiendo escenario.", t);
            }
            throw t;
        }
    }

    @Step("ingresa usuario '{0}' y contraseña")
    public void enterCredentials(String username, String password) {
        loginPage.inputUsername
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible()
            .type(username);
        loginPage.inputPassword
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible()
            .type(password);
        LOGGER.info("enter credentials with username:{} and password:{}.", username, password);
    }

    @Step("hace clic en el botón Login")
    public void clickLogin() {
        loginPage.btnLogin
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilClickable()
            .click();
        // Esperar a que el Dashboard cargue antes de continuar
        mainPage.txtTitleMainPage
            .withTimeoutOf(Duration.ofSeconds(20))
            .waitUntilVisible();
    }

    @Step("valida que el login fue exitoso")
    public void validateSuccessfulLogin() {
        String titulo = mainPage.txtTitleMainPage
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible()
            .getText();
        AssertionHelper.assertTextEquals("Login was unsuccessful.", titulo, "Dashboard");
    }

    private boolean isChromeUnavailable(Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            String msg = cause.getMessage() != null ? cause.getMessage() : "";
            if (msg.contains("Chrome failed to start") ||
                msg.contains("Could not instantiate") ||
                msg.contains("SessionNotCreatedException")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
