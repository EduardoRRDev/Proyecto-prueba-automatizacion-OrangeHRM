package co.com.proyecto.automatizacion.data;

import co.com.proyecto.automatizacion.models.Employee;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * Carga datos desde testdata/ (YAML): base + override por caseId.
 * Usado por EmployeeTestData; el resto del proyecto debe usar EmployeeTestData, no esta clase.
 * testdata/empleados.yml = bloques base; testdata/flows/add_employee.yml = datasets por caso.
 *
 * username y employeeId reciben un sufijo único por ejecución (últimos 5 dígitos del timestamp)
 * para evitar colisiones cuando el dato ya existe en el sistema bajo prueba.
 */
public final class TestDataLoader {

    /**
     * Sufijo único para esta ejecución: últimos 4 dígitos del timestamp en ms.
     * Se aplica a username y employeeId para evitar colisiones en ejecuciones consecutivas.
     * OrangeHRM limita employeeId a 10 caracteres; el base "123654" tiene 6, queda espacio para 4.
     */
    private static final String RUN_SUFFIX = String.valueOf(System.currentTimeMillis() % 10000);

    private static final String EMPLEADOS = "testdata/empleados.yml";
    private static final String FLOWS_ADD = "testdata/flows/add_employee.yml";

    private static Map<String, Object> empleadosRoot;
    private static Map<String, Object> flowsAddRoot;

    static {
        empleadosRoot = loadYaml(EMPLEADOS);
        flowsAddRoot = loadYaml(FLOWS_ADD);
    }

    @SuppressWarnings("unchecked")
    public static Employee getEmployee(String caseId) {
        Map<String, Object> datasets = getMap(flowsAddRoot, "datasets");
        if (datasets == null) throw new IllegalStateException("No 'datasets' en " + FLOWS_ADD);
        Map<String, Object> dataset = getMap(datasets, caseId);
        if (dataset == null) throw new IllegalArgumentException("Dataset no encontrado: " + caseId);

        String ref = (String) dataset.get("empleado_ref");
        Map<String, Object> override = getMap(dataset, "empleado_override");
        if (ref == null) throw new IllegalStateException("empleado_ref faltante en dataset: " + caseId);

        Map<String, Object> base = getEmpleadoBlock(ref);
        return mergeToEmployee(base, override);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getEmpleadoBlock(String ref) {
        Map<String, Object> emp = getMap(empleadosRoot, "empleados");
        if (emp == null) throw new IllegalStateException("No 'empleados' en " + EMPLEADOS);
        Map<String, Object> block = getMap(emp, ref);
        if (block == null) throw new IllegalArgumentException("Empleado base no encontrado: " + ref);
        return block;
    }

    private static Employee mergeToEmployee(Map<String, Object> base, Map<String, Object> override) {
        Employee.Builder b = Employee.builder();
        String[] keys = {"firstName", "middleName", "lastName", "employeeId", "username", "password"};
        for (String k : keys) {
            String v = override != null && override.containsKey(k) ? str(override.get(k)) : str(base.get(k));
            if (v != null) {
                switch (k) {
                    case "firstName" -> b.firstName(v);
                    case "middleName" -> b.middleName(v);
                    case "lastName" -> b.lastName(v);
                    // Sufijo único para evitar colisiones "Employee Id already exists" / "Username already exists"
                    case "employeeId" -> b.employeeId(v + RUN_SUFFIX);
                    case "username" -> b.username(v + RUN_SUFFIX);
                    case "password" -> b.password(v);
                }
            }
        }
        return b.build();
    }

    private static String str(Object o) {
        if (o == null) return null;
        return o.toString().trim();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> from, String key) {
        if (from == null) return null;
        Object v = from.get(key);
        return v instanceof Map ? (Map<String, Object>) v : null;
    }

    private static Map<String, Object> loadYaml(String resource) {
        Yaml yaml = new Yaml();
        try (InputStream is = TestDataLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) return Map.of();
            Object o = yaml.load(is);
            return o instanceof Map ? (Map<String, Object>) o : Map.of();
        } catch (Exception e) {
            throw new IllegalStateException("Error cargando " + resource, e);
        }
    }
}
