package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.config.Paths;
import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.models.Employee;
import co.com.proyecto.automatizacion.pages.pim.AddEmployeePage;
import co.com.proyecto.automatizacion.util.WaitHelper;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Steps para crear un empleado en OrangeHRM (módulo PIM > Add Employee).
 * Responsabilidad única: navegar al formulario, llenarlo y guardar.
 *
 * La búsqueda, verificación y eliminación en la lista están en EmployeeListSteps.
 */
public class AddEmployeeSteps {

    private AddEmployeePage addEmployeePage;

    @Step("navega a la página Add Employee")
    public void navigateToAddEmployee() {
        addEmployeePage.openAt(TestConfig.getBaseUrl() + Paths.ADD_EMPLOYEE);
    }

    /**
     * Llena el formulario completo con los datos del modelo Employee.
     * Incluye datos básicos y sección "Create Login Details".
     */
    @Step("diligencia el formulario con los datos del empleado")
    public void fillEmployeeForm(Employee employee) {
        fillBasicData(employee);
        fillLoginDetails(employee.getUsername(), employee.getPassword());
    }

    @Step("guarda el empleado")
    public void saveEmployee() {
        WaitHelper.pause(1500); // Espera a que el formulario procese los campos antes de guardar
        addEmployeePage.btnSave
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
    }

    /**
     * Verifica que el guardado fue exitoso: espera a que OrangeHRM redirija
     * a la página del perfil del empleado (viewPersonalDetails) y luego
     * que el título "Personal Details" esté visible.
     */
    @Step("verifica que el empleado se guardó correctamente")
    public void verifyEmployeeSaved() {
        WebDriverWait wait = new WebDriverWait(addEmployeePage.getDriver(), Duration.ofSeconds(25));
        wait.until(driver -> driver.getCurrentUrl().contains("viewPersonalDetails"));

        addEmployeePage.tituloCompleteDetails
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible();
    }

    // -------------------------------------------------------------------------
    // Métodos privados — lógica interna del formulario
    // -------------------------------------------------------------------------

    private void fillBasicData(Employee employee) {
        addEmployeePage.inputFirstName
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible()
            .type(employee.getFirstName());

        if (employee.getMiddleName() != null && !employee.getMiddleName().isBlank()) {
            addEmployeePage.inputMiddleName.type(employee.getMiddleName());
        }

        addEmployeePage.inputLastName.type(employee.getLastName());

        if (employee.getEmployeeId() != null && !employee.getEmployeeId().isBlank()) {
            WaitHelper.pause(500);
            addEmployeePage.inputEmployeeId
                .withTimeoutOf(Duration.ofSeconds(10))
                .waitUntilVisible()
                .click();
            addEmployeePage.inputEmployeeId.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            addEmployeePage.inputEmployeeId.type(employee.getEmployeeId());
        }
    }

    private void fillLoginDetails(String username, String password) {
        activateLoginDetailsSwitch();
        addEmployeePage.inputNewUsername
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible()
            .type(username);
        addEmployeePage.inputNewPassword.type(password);
        addEmployeePage.inputConfirmPassword.type(password);
    }

    /**
     * Activa el switch "Create Login Details".
     * OrangeHRM usa un switch personalizado (no un checkbox estándar), por lo que
     * se usa JavaScript para garantizar la activación en todos los entornos (headless/normal).
     */
    private void activateLoginDetailsSwitch() {
        String script =
            "var rows = document.querySelectorAll('[class*=\"oxd-form-row\"]');" +
            "for (var i = 0; i < rows.length; i++) {" +
            "  if (rows[i].textContent.indexOf('Create Login Details') >= 0) {" +
            "    var input = rows[i].querySelector('input[type=\"checkbox\"]');" +
            "    if (input && !input.checked) { input.click(); return; }" +
            "    var label = rows[i].querySelector('.oxd-switch-wrapper label');" +
            "    if (label) { label.click(); return; }" +
            "  }" +
            "}";
        addEmployeePage.evaluateJavascript(script);
        WaitHelper.pause(1000);

        // Si el campo username aún no aparece, intentar con el span del switch
        try {
            addEmployeePage.inputNewUsername
                .withTimeoutOf(Duration.ofSeconds(3))
                .waitUntilVisible();
        } catch (Exception e) {
            addEmployeePage.evaluateJavascript(
                "var s = document.querySelector('span.oxd-switch-input'); if (s) s.click();"
            );
            WaitHelper.pause(1500);
        }
    }
}
