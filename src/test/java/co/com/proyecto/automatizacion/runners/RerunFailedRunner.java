package co.com.proyecto.automatizacion.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Runner que reintenta SOLO los escenarios que fallaron en la ejecución anterior.
 *
 * Cómo funciona:
 * 1. AddEmployeeRunner y LoginRunner generan target/rerun-*.txt con los escenarios fallidos.
 * 2. Este runner los lee y los vuelve a ejecutar.
 *
 * Uso local (solo si hubo fallos en el paso anterior):
 *   ./gradlew test --tests "co.com.proyecto.automatizacion.runners.RerunFailedRunner"
 *
 * En CI se ejecuta automáticamente después del paso principal (ver .github/workflows/test.yml).
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = {
                "@target/rerun-add-employee.txt",
                "@target/rerun-login.txt"
        },
        glue = {"co.com.proyecto.automatizacion.definitions"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        plugin = {"pretty", "rerun:target/rerun-second-attempt.txt"}
)
public class RerunFailedRunner {
}
