package co.com.proyecto.automatizacion.data;

import co.com.proyecto.automatizacion.context.ScenarioContext;
import co.com.proyecto.automatizacion.models.Employee;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * Carga datos desde testdata/ (YAML): base + override por caseId.
 * Usado por EmployeeTestData; el resto del proyecto debe usar EmployeeTestData, no esta clase.
 * testdata/empleados.yml = bloques base; testdata/flows/add_employee.yml = datasets por caso.
 *
 * username y employeeId reciben un sufijo único por escenario (UUID) para evitar colisiones
 * cuando varios jobs o usuarios ejecutan tests a la vez sobre la misma base de datos.
 * El sufijo se fija en Hooks y se reutiliza en todo el escenario vía ScenarioContext.
 */
public final class TestDataLoader {

    /** Fallback cuando no hay ScenarioContext (ej. test unitario o ejecución sin Cucumber). */
    private static final String FALLBACK_SUFFIX = String.valueOf(System.currentTimeMillis() % 10000);

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
        String suffixId = ScenarioContext.get("runSuffixShort");
        if (suffixId == null) suffixId = FALLBACK_SUFFIX;
        suffixId = suffixId.length() >= 4 ? suffixId.substring(0, 4) : suffixId;

        String suffixUser = ScenarioContext.get("runSuffix");
        if (suffixUser == null || suffixUser.isEmpty()) suffixUser = FALLBACK_SUFFIX;

        Employee.Builder b = Employee.builder();
        String[] keys = {"firstName", "middleName", "lastName", "employeeId", "username", "password"};
        for (String k : keys) {
            String v = override != null && override.containsKey(k) ? str(override.get(k)) : str(base.get(k));
            if (v != null) {
                switch (k) {
                    case "firstName" -> b.firstName(v);
                    case "middleName" -> b.middleName(v);
                    case "lastName" -> b.lastName(v);
                    case "employeeId" -> b.employeeId(v + suffixId);   // 4 chars para respetar límite 10
                    case "username" -> b.username(v + suffixUser);     // 8 chars, único por escenario/ejecución
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
