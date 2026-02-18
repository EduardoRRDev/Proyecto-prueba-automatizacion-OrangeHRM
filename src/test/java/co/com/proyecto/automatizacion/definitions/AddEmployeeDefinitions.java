package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.steps.AddEmployeeSteps;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Steps;

/**
 * Step Definitions para los escenarios de agregar empleado.
 * Los pasos de login se reutilizan de LoginDefinitions.
 */
public class AddEmployeeDefinitions {

    @Steps
    private AddEmployeeSteps addEmployeeSteps;

    @Cuando("navega a la página Add Employee")
    public void navegarAddEmployee() {
        addEmployeeSteps.navigateToAddEmployee();
    }

    @Y("diligencia los datos del formulario")
    public void diligenciarDatosFormulario() {
        // Nombre, segundo nombre, apellido, employeeId, username, password
        addEmployeeSteps.fillEmployeeFormComplete("Omar", "Eduardo", "Rincon", "123654", "Omar.rincon", "OmarRincon123!");
    }

    @Y("guarda el empleado")
    public void guardarEmpleado() {
        addEmployeeSteps.saveEmployee();
    }

    @Entonces("verifica que el empleado se guardó correctamente")
    public void verificarEmpleadoGuardado() {
        addEmployeeSteps.verifyEmployeeSaved();
    }

    @Y("navega a la lista de empleados")
    public void navegarListaEmpleados() {
        addEmployeeSteps.navigateToEmployeeList();
    }

    @Y("busca el empleado por nombre {string} e id {string}")
    public void buscarEmpleado(String nombre, String id) {
        addEmployeeSteps.searchEmployee(nombre, id);
    }

    @Entonces("verifica que el empleado aparece en la tabla con id {string}, nombre {string} y apellido {string}")
    public void verificarEmpleadoEnTabla(String id, String nombre, String apellido) {
        addEmployeeSteps.verifyEmployeeInTable(id, nombre, apellido);
    }

    @Y("elimina el empleado de la lista")
    public void eliminarEmpleado() {
        addEmployeeSteps.deleteEmployee();
    }
}
