package co.com.proyecto.automatizacion.data;

import co.com.proyecto.automatizacion.models.Employee;

/**
 * Punto de entrada a los datos de empleado. Usado por definitions/steps.
 * Toda la data está en testdata/ (YAML); la carga la hace TestDataLoader (no usar TestDataLoader desde fuera de este paquete).
 * - getDefaultEmployee() → dataset DEFAULT.
 * - getEmployee(caseId) → dataset por caso (ADD_EMPLOYEE, ADD_EMPLOYEE_VALIDAR_LIQUIDACION, etc.).
 * - from(...) → datos sueltos (Scenario Outline / tests programáticos).
 */
public final class EmployeeTestData {

    private EmployeeTestData() {
    }

    public static Employee getDefaultEmployee() {
        return TestDataLoader.getEmployee("DEFAULT");
    }

    public static Employee getEmployee(String caseId) {
        return TestDataLoader.getEmployee(caseId);
    }

    /** Empleado con datos personalizados (Scenario Outline o tests programáticos). */
    public static Employee from(String firstName, String middleName, String lastName,
                               String employeeId, String username, String password) {
        return Employee.builder()
            .firstName(firstName)
            .middleName(middleName)
            .lastName(lastName)
            .employeeId(employeeId)
            .username(username)
            .password(password)
            .build();
    }
}
