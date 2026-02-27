package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.config.Paths;
import co.com.proyecto.automatizacion.config.TestConfig;
import co.com.proyecto.automatizacion.pages.pim.EmployeeListPage;
import net.serenitybdd.annotations.Step;

import java.time.Duration;

/**
 * Steps para la lista de empleados (módulo PIM).
 * Responsabilidad: navegar a la lista, buscar, verificar y eliminar empleados.
 *
 * Separado de AddEmployeeSteps para respetar el principio de Responsabilidad Única (SRP).
 */
public class EmployeeListSteps {

    private EmployeeListPage employeeListPage;

    @Step("navega a la lista de empleados")
    public void navigateToEmployeeList() {
        employeeListPage.openAt(TestConfig.getBaseUrl() + Paths.VIEW_EMPLOYEE_LIST);
        waitForPageLoad();
    }

    /**
     * Busca un empleado por su primer nombre usando el autocomplete.
     * Si el autocomplete no muestra opciones (empleado recién indexado), ejecuta Search de todos modos.
     */
    @Step("busca el empleado con nombre '{0}'")
    public void searchEmployeeByName(String firstName) {
        employeeListPage.inputEmployeeName
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible()
            .type(firstName);

        pause(2500); // Tiempo para que el autocomplete cargue opciones del servidor

        employeeListPage.selectAutocompleteOption(firstName);

        employeeListPage.btnSearch
            .withTimeoutOf(Duration.ofSeconds(5))
            .waitUntilClickable()
            .click();

        pause(2500); // Tiempo para que la tabla cargue resultados
    }

    /**
     * Verifica que el apellido del empleado aparece en la tabla de resultados.
     * Se usa el apellido porque es una columna separada y no se combina con el middle name.
     */
    @Step("verifica que '{0}' aparece en la tabla")
    public void verifyLastNameInTable(String lastName) {
        employeeListPage.cellByText(lastName)
            .withTimeoutOf(Duration.ofSeconds(15))
            .waitUntilVisible();
    }

    @Step("elimina el primer empleado de la lista")
    public void deleteFirstEmployee() {
        employeeListPage.iconTrash
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
        pause(800);
        employeeListPage.btnConfirmDelete
            .withTimeoutOf(Duration.ofSeconds(10))
            .waitUntilClickable()
            .click();
    }

    private void waitForPageLoad() {
        pause(2500);
    }

    private void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
