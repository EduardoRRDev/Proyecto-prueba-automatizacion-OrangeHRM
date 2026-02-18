package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.steps.LoginSteps;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.annotations.Steps;

/**
 * Step Definitions para los escenarios de inicio de sesión.
 * 
 * Las Step Definitions son el "pegamento" entre los escenarios escritos en Gherkin
 * (lenguaje natural) y el código Java que ejecuta las acciones.
 * 
 * Cada método anotado con @Dado, @Cuando o @Entonces corresponde a un paso
 * en los archivos .feature. Cucumber busca estos métodos cuando encuentra
 * un paso que coincide exactamente con el texto de la anotación.
 * 
 * @Steps: Serenity inyecta automáticamente una instancia de LoginSteps.
 *         Esto permite que Serenity rastree y reporte cada paso en los reportes.
 */
public class LoginDefinitions {

    @Steps
    private LoginSteps loginSteps;

    @Dado("que el usuario navega a la página de inicio de sesión")
    public void navegarPaginaInicioSesion() {
        loginSteps.openLoginPage();      // Abre la página de login
    }

    @Cuando("ingresa las credenciales de acceso correctas")
    public void ingresaLasCredencialesDeAccesoCorrectas() {
        loginSteps.enterCredentials(TestConfig.getUsername(), TestConfig.getPassword());
        loginSteps.accessSystem();
    }

    @Entonces("debería ver la página principal")
    public void deberiaVerPaginaPrincipal(){
        loginSteps.validateSuccessfulLogin();  // Valida que el login fue exitoso
    }
}
