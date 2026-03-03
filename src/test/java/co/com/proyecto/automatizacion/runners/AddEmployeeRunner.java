package co.com.proyecto.automatizacion.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/add_employee",
        glue = {"co.com.proyecto.automatizacion.definitions"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@AgregarEmpleado",
        plugin = {"pretty", "rerun:target/rerun-add-employee.txt"}
)
public class AddEmployeeRunner {

    static {
        String worker = System.getProperty("org.gradle.test.worker");
        if (worker != null && !worker.isEmpty()) {
            String base = System.getProperty("serenity.outputDirectory");
            if (base != null) {
                System.setProperty("serenity.outputDirectory", base + "-" + worker.replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }
    }
}
