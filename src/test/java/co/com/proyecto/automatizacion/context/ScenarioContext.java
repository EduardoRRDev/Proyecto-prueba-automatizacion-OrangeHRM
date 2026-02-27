package co.com.proyecto.automatizacion.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Almacén de datos de escenario por hilo (thread-local).
 * Permite compartir datos calculados en tiempo de ejecución entre steps del mismo escenario.
 * Se limpia en el @After del hook para evitar contaminación entre escenarios.
 */
public final class ScenarioContext {

    private static final ThreadLocal<Map<String, String>> CONTEXT =
        ThreadLocal.withInitial(HashMap::new);

    private ScenarioContext() {}

    public static void set(String key, String value) {
        CONTEXT.get().put(key, value);
    }

    public static String get(String key) {
        return CONTEXT.get().get(key);
    }

    public static void clear() {
        CONTEXT.get().clear();
    }
}
