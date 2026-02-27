package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.data.EmployeeTestData;
import co.com.proyecto.automatizacion.models.Employee;
import co.com.proyecto.automatizacion.steps.AddEmployeeSteps;
import co.com.proyecto.automatizacion.steps.EmployeeListSteps;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Steps;

/**
 * Step Definitions para los escenarios de agregar empleado.
 *
 * Conecta los pasos Gherkin con dos clases de Steps:
 *   - AddEmployeeSteps  → crear empleado (navegar, llenar formulario, guardar)
 *   - EmployeeListSteps → buscar, verificar y eliminar en la lista
 *
 * Los pasos de login se reutilizan desde LoginDefinitions.
 */
public class AddEmployeeDefinitions {

    @Steps
    private AddEmployeeSteps addEmployeeSteps;

    @Steps
    private EmployeeListSteps employeeListSteps;

    // -------------------------------------------------------------------------
    // Crear empleado
    // -------------------------------------------------------------------------

    @Cuando("navega a la página Add Employee")
    public void navegarAddEmployee() {
        addEmployeeSteps.navigateToAddEmployee();
    }

    @Y("diligencia los datos del formulario")
    public void diligenciarDatosFormulario() {
        Employee employee = EmployeeTestData.getDefaultEmployee();
        addEmployeeSteps.fillEmployeeForm(employee);
    }

    @Y("diligencia los datos del formulario con el caso {string}")
    public void diligenciarDatosFormularioConCaso(String caseId) {
        Employee employee = EmployeeTestData.getEmployee(caseId);
        addEmployeeSteps.fillEmployeeForm(employee);
    }

    @Y("guarda el empleado")
    public void guardarEmpleado() {
        addEmployeeSteps.saveEmployee();
    }

    @Entonces("verifica que el empleado se guardó correctamente")
    public void verificarEmpleadoGuardado() {
        addEmployeeSteps.verifyEmployeeSaved();
    }

    // -------------------------------------------------------------------------
    // Lista de empleados
    // -------------------------------------------------------------------------

    @Y("navega a la lista de empleados")
    public void navegarListaEmpleados() {
        employeeListSteps.navigateToEmployeeList();
    }

    @Y("busca el empleado registrado")
    public void buscarEmpleadoRegistrado() {
        Employee employee = EmployeeTestData.getDefaultEmployee();
        // Se usa firstName (no getFullFirstName) porque el autocomplete busca por primer nombre
        employeeListSteps.searchEmployeeByName(employee.getFirstName());
    }

    @Y("busca el empleado registrado del caso {string}")
    public void buscarEmpleadoRegistradoDelCaso(String caseId) {
        Employee employee = EmployeeTestData.getEmployee(caseId);
        employeeListSteps.searchEmployeeByName(employee.getFirstName());
    }

    @Entonces("verifica que el empleado aparece en la tabla")
    public void verificarEmpleadoEnTabla() {
        Employee employee = EmployeeTestData.getDefaultEmployee();
        employeeListSteps.verifyLastNameInTable(employee.getLastName());
    }

    @Entonces("verifica que el empleado del caso {string} aparece en la tabla")
    public void verificarEmpleadoEnTablaDelCaso(String caseId) {
        Employee employee = EmployeeTestData.getEmployee(caseId);
        employeeListSteps.verifyLastNameInTable(employee.getLastName());
    }

    @Y("elimina el empleado de la lista")
    public void eliminarEmpleado() {
        employeeListSteps.deleteFirstEmployee();
    }
}
