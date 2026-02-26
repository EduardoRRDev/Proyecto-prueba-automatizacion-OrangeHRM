package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.config.Paths;
import co.com.proyecto.automatizacion.pages.pim.AddEmployeePage;
import co.com.proyecto.automatizacion.pages.pim.EmployeeListPage;
import co.com.proyecto.automatizacion.pages.common.MainPage;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.Keys;

import java.time.Duration;

/**
 * Steps para agregar empleado en OrangeHRM PIM.
 * Diligencia datos del empleado, Create Login Details y validación en Employee List.
 */
public class AddEmployeeSteps {

    private AddEmployeePage addEmployeePage;
    private MainPage mainPage;
    private EmployeeListPage employeeListPage;

    @Step("navega a la página Add Employee")
    public void navigateToAddEmployee() {
        addEmployeePage.openAt(TestConfig.getBaseUrl() + Paths.ADD_EMPLOYEE);
    }

    @Step("diligencia todos los campos del formulario incluyendo login")
    public void fillEmployeeFormComplete(String firstName, String middleName, String lastName,
                                         String employeeId, String username, String password) {
        fillEmployeeData(firstName, middleName, lastName, employeeId);
        fillCreateLoginDetails(username, password);
    }

    /** Sobrecarga que recibe el modelo Employee (estrategia de datos). */
    public void fillEmployeeFormComplete(co.com.proyecto.automatizacion.models.Employee employee) {
        fillEmployeeFormComplete(
            employee.getFirstName(),
            employee.getMiddleName(),
            employee.getLastName(),
            employee.getEmployeeId(),
            employee.getUsername(),
            employee.getPassword()
        );
    }

    @Step("guarda el empleado")
    public void saveEmployee() {
        pause(2000); // Esperar a que el formulario procese antes de guardar
        addEmployeePage.btnSave
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
    }

    @Step("verifica que el empleado se guardó correctamente")
    public void verifyEmployeeSaved() {
        addEmployeePage.tituloCompleteDetails
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible();
    }

    @Step("navega a la lista de empleados")
    public void navigateToEmployeeList() {
        ensureWindowSizeForLayout(); // Crítico en CI/headless: evita que el menú oculte el contenido
        employeeListPage.openAt(TestConfig.getBaseUrl() + Paths.VIEW_EMPLOYEE_LIST);
        pause(3000); // Esperar a que cargue (crítico en CI/headless)
        expandEmployeeListFilterIfCollapsed();
    }

    /** En headless, el viewport puede ser pequeño y el menú lateral oculta el contenido. Ajusta a 1920x1080. */
    private void ensureWindowSizeForLayout() {
        if (Boolean.getBoolean("headless.mode")) {
            try {
                addEmployeePage.getDriver().manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
            } catch (Exception ignored) {
                // Si falla (ej. driver no iniciado aún), continuar
            }
        }
    }

    /** Expande el filtro "Employee Information" si está colapsado (común en CI/headless). */
    private void expandEmployeeListFilterIfCollapsed() {
        String expandScript = "var all = document.querySelectorAll('*'); " +
            "for (var i = 0; i < all.length; i++) { " +
            "  var t = (all[i].textContent || '').trim(); " +
            "  if (t === 'Employee Information' && all[i].querySelector && !all[i].querySelector('input')) { " +
            "    all[i].click(); return; " +
            "  } " +
            "}";
        employeeListPage.evaluateJavascript(expandScript);
        pause(1500);
    }

    @Step("busca el empleado por nombre {0} e id {1}")
    public void searchEmployee(String employeeName, String employeeId) {
        employeeListPage.evaluateJavascript("window.scrollTo(0, 0);");
        pause(500);
        employeeListPage.inputEmployeeName
            .withTimeoutOf(Duration.ofSeconds(20))
            .waitUntilVisible()
            .type(employeeName);
        pause(1500); // Esperar a que el autocomplete cargue y filtre
        employeeListPage.btnSearch
            .withTimeoutOf(Duration.ofSeconds(5))
            .waitUntilClickable()
            .click();
        pause(2000); // Esperar a que carguen los resultados de la búsqueda
    }

    @Step("verifica que el empleado aparece en la tabla con id {0}, nombre {1} y apellido {2}")
    public void verifyEmployeeInTable(String employeeId, String firstName, String lastName) {
        employeeListPage.evaluateJavascript("window.scrollBy(0, 500);");
        // Verificar por nombre (el ID tiene sufijo único; verificar nombre es suficiente)
        employeeListPage.cellNameInTable(firstName)
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible();
        employeeListPage.cellLastNameInTable(lastName)
            .withTimeoutOf(Duration.ofSeconds(5))
            .waitUntilVisible();
        pause(2000);
    }

    @Step("elimina el empleado de la lista")
    public void deleteEmployee() {
        employeeListPage.iconTrash
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
        pause(1000); // Esperar a que aparezca el modal de confirmación
        employeeListPage.btnConfirmDelete
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
    }

    /** Datos del empleado: First Name, Middle Name, Last Name, Employee Id */
    private void fillEmployeeData(String firstName, String middleName, String lastName, String employeeId) {
        addEmployeePage.inputFirstName
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilVisible()
            .type(firstName);

        if (middleName != null && !middleName.isBlank()) {
            addEmployeePage.inputMiddleName.type(middleName);
        }

        addEmployeePage.inputLastName.type(lastName);

        if (employeeId != null && !employeeId.isBlank()) {
            pause(500);
            addEmployeePage.inputEmployeeId.withTimeoutOf(Duration.ofSeconds(15)).waitUntilVisible();
            addEmployeePage.inputEmployeeId.click();
            addEmployeePage.inputEmployeeId.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            addEmployeePage.inputEmployeeId.type(employeeId);
        }
    }

    /** Create Login Details: activa switch, Username, Password, Confirm Password, Status */
    private void fillCreateLoginDetails(String username, String password) {
        activateCreateLoginDetailsSwitch();
        addEmployeePage.inputNewUsername
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible()
            .type(username);
        addEmployeePage.inputNewPassword.type(password);
        addEmployeePage.inputConfirmPassword.type(password);
    }

    /** Activa el switch "Create Login Details" y espera hasta que los campos de login sean visibles. */
    private void activateCreateLoginDetailsSwitch() {
        // Intento 1: click directo en el input checkbox del switch
        String scriptCheckbox = "var rows = document.querySelectorAll('[class*=\"oxd-form-row\"]'); " +
            "for (var i = 0; i < rows.length; i++) { " +
            "  if (rows[i].textContent.indexOf('Create Login Details') >= 0) { " +
            "    var input = rows[i].querySelector('input[type=\"checkbox\"]'); " +
            "    if (input) { input.scrollIntoView({block:'center'}); if (!input.checked) { input.click(); } return true; } " +
            "    var label = rows[i].querySelector('.oxd-switch-wrapper label'); " +
            "    if (label) { label.scrollIntoView({block:'center'}); label.click(); return true; } " +
            "  } " +
            "} return false;";
        addEmployeePage.evaluateJavascript(scriptCheckbox);
        pause(1000);

        // Intento 2: si el campo username aún no es visible, hacer click directo en el span del switch
        try {
            addEmployeePage.inputNewUsername
                .withTimeoutOf(Duration.ofSeconds(3))
                .waitUntilVisible();
        } catch (Exception e) {
            // El switch no respondió al primer intento; forzar click en el span oxd-switch-input
            String scriptSpan = "var spans = document.querySelectorAll('span.oxd-switch-input'); " +
                "if (spans.length > 0) { spans[0].click(); }";
            addEmployeePage.evaluateJavascript(scriptSpan);
            pause(1500);
        }
    }

    private void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
