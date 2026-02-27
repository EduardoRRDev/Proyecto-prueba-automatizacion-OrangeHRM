package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.steps.LoginSteps;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.annotations.Steps;

/**
 * Step Definitions para los escenarios de inicio de sesión.
 * Conecta los pasos Gherkin con LoginSteps.
 */
public class LoginDefinitions {

    @Steps
    private LoginSteps loginSteps;

    @Dado("que el usuario navega a la página de inicio de sesión")
    public void navegarPaginaInicioSesion() {
        loginSteps.openLoginPage();
    }

    @Cuando("ingresa las credenciales de acceso correctas")
    public void ingresaLasCredencialesDeAccesoCorrectas() {
        loginSteps.enterCredentials(TestConfig.getUsername(), TestConfig.getPassword());
        loginSteps.clickLogin();
    }

    @Entonces("debería ver la página principal")
    public void deberiaVerPaginaPrincipal() {
        loginSteps.validateSuccessfulLogin();
    }
}
