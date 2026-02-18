package co.com.proyecto.automatizacion.data;

import co.com.proyecto.automatizacion.models.Employee;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Estrategia de datos: carga datos de prueba desde archivos externos.
 * Permite cambiar datos sin modificar código y soporta múltiples entornos.
 */
public final class EmployeeTestData {

    private static final String DATA_FILE = "data/employees.properties";

    private EmployeeTestData() {
    }

    /**
     * Obtiene el empleado de prueba por defecto desde employees.properties.
     */
    public static Employee getDefaultEmployee() {
        Properties props = loadProperties();
        return Employee.builder()
            .firstName(props.getProperty("employee.firstName", "Omar"))
            .middleName(props.getProperty("employee.middleName", "Eduardo"))
            .lastName(props.getProperty("employee.lastName", "Rincon"))
            .employeeId(props.getProperty("employee.employeeId", "123654"))
            .username(props.getProperty("employee.username", "Omar.rincon"))
            .password(props.getProperty("employee.password", "OmarRincon123!"))
            .build();
    }

    /**
     * Obtiene un empleado con datos personalizados (útil para Scenario Outline).
     */
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

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = EmployeeTestData.class.getClassLoader().getResourceAsStream(DATA_FILE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {
            // Usar valores por defecto si no existe el archivo
        }
        return props;
    }
}
