package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.util.UUID;

/**
 * Hooks que se ejecutan antes/después de los escenarios.
 * Fija la base URL y genera sufijos únicos por escenario para evitar colisiones de datos.
 */
public class Hooks {

    private static final int SUFFIX_USER_LENGTH = 8;
    private static final int SUFFIX_ID_LENGTH = 4;

    @Before(order = 0)
    public void beforeScenario() {
        TestConfig.ensureBaseUrlSet();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        ScenarioContext.set("runSuffix", uuid.substring(0, SUFFIX_USER_LENGTH));
        ScenarioContext.set("runSuffixShort", uuid.substring(SUFFIX_USER_LENGTH, SUFFIX_USER_LENGTH + SUFFIX_ID_LENGTH));
    }

    @After
    public void afterScenario() {
        ScenarioContext.clear();
    }
}
