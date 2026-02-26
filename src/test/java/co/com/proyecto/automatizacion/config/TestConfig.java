package co.com.proyecto.automatizacion.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración de pruebas por entorno (dev / qa / prod).
 * Entorno: -Denv=dev|qa|prod (por defecto qa). Propiedades en src/test/resources/env/{env}.properties.
 * Base URL: se lee de env/{env}.properties (app.base.url). Usada en steps con Paths.
 * Credenciales: variables de entorno → -D → serenity.properties → valor por defecto.
 */
public final class TestConfig {

    private static final String ENV_PROP = "env";
    private static final String DEFAULT_ENV = "qa";

    private static final Properties SERENITY_PROPS = loadFromResource("/serenity.properties");
    private static volatile Properties envProps;
    private static volatile boolean baseUrlSet;

    private TestConfig() {
    }

    /** Entorno activo: valor de -Denv (por defecto "qa"). */
    public static String getEnv() {
        return firstNonNull(System.getProperty(ENV_PROP), DEFAULT_ENV);
    }

    /**
     * Base URL del entorno activo (sin barra final).
     * Prioridad: -Dwebdriver.base.url → env/{env}.properties → hardcoded.
     */
    public static String getBaseUrl() {
        ensureEnvLoaded();
        return firstNonNull(
                System.getProperty("webdriver.base.url"),
                envProps.getProperty("app.base.url"),
                "https://opensource-demo.orangehrmlive.com"
        );
    }

    /**
     * Fija webdriver.base.url en el sistema una sola vez.
     * Llamado desde Hooks @Before para que esté disponible antes del primer page.open().
     */
    public static void ensureBaseUrlSet() {
        if (baseUrlSet) return;
        String base = getBaseUrl();
        if (base != null && !base.isBlank()) {
            System.setProperty("webdriver.base.url", base.endsWith("/") ? base.substring(0, base.length() - 1) : base);
            baseUrlSet = true;
        }
    }

    /**
     * Usuario de login.
     * Prioridad: ORANGEHRM_USERNAME (env SO) → -Dorangehrm.username → serenity.properties → "Admin".
     */
    public static String getUsername() {
        return firstNonNull(
                System.getenv("ORANGEHRM_USERNAME"),
                System.getProperty("orangehrm.username"),
                SERENITY_PROPS.getProperty("orangehrm.username"),
                "Admin"
        );
    }

    /**
     * Contraseña de login.
     * Prioridad: ORANGEHRM_PASSWORD (env SO) → -Dorangehrm.password → serenity.properties → "admin123".
     */
    public static String getPassword() {
        return firstNonNull(
                System.getenv("ORANGEHRM_PASSWORD"),
                System.getProperty("orangehrm.password"),
                SERENITY_PROPS.getProperty("orangehrm.password"),
                "admin123"
        );
    }

    private static void ensureEnvLoaded() {
        if (envProps == null) {
            synchronized (TestConfig.class) {
                if (envProps == null) {
                    envProps = loadFromResource("/env/" + getEnv() + ".properties");
                    if (envProps.isEmpty()) envProps = new Properties();
                }
            }
        }
    }

    private static Properties loadFromResource(String path) {
        Properties p = new Properties();
        try (InputStream is = TestConfig.class.getResourceAsStream(path)) {
            if (is != null) p.load(is);
        } catch (IOException ignored) {
        }
        return p;
    }

    private static String firstNonNull(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
