package co.com.proyecto.automatizacion.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Runner para ejecutar los escenarios de inicio de sesión.
 * 
 * Este runner integra Serenity BDD con Cucumber para ejecutar los escenarios
 * escritos en lenguaje Gherkin y generar reportes detallados con capturas de pantalla.
 * 
 * @RunWith(CucumberWithSerenity.class)
 * - Indica a JUnit que use CucumberWithSerenity como ejecutor de pruebas
 * - Esto permite que Serenity capture información de cada paso para los reportes
 * 
 * @CucumberOptions configura cómo Cucumber ejecutará los escenarios:
 * - features: Ruta donde están los archivos .feature con los escenarios Gherkin
 * - glue: Paquetes donde Cucumber buscará las step definitions
 * - snippets: Formato de los snippets generados cuando falta una step definition
 * - tags: Filtra qué escenarios ejecutar (solo los marcados con @InicioSesionExitoso)
 * - plugin: Formato de salida en consola ("pretty" = formato legible)
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/login",
        glue = {"co.com.proyecto.automatizacion.definitions"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@InicioSesionExitoso",
        plugin = {"pretty", "rerun:target/rerun-login.txt"}
)
public class LoginRunner {
}
