package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.config.TestConfig;
import io.cucumber.java.Before;

/**
 * Hooks que se ejecutan antes/después de los escenarios.
 * Fija la base URL del entorno (dev/qa/prod) antes del primer page.open().
 */
public class Hooks {

    @Before(order = 0)
    public void beforeScenario() {
        TestConfig.ensureBaseUrlSet();
    }
}
