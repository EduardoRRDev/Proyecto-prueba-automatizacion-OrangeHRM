package co.com.proyecto.automatizacion.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración de pruebas por entorno (dev / qa / prod).
 * Entorno: -Denv=dev|qa|prod (por defecto dev). Propiedades en src/test/resources/env/{env}.properties.
 * Base URL: Serenity usa webdriver.base.url; se fija en ensureBaseUrlSet() (llamar desde Hooks @Before).
 * Credenciales: env file → variables de entorno → -D → serenity.properties → default.
 */
public final class TestConfig {

    private static final String ENV_PROP = "env";
    private static final String DEFAULT_ENV = "qa";

    private static final Properties SERENITY_PROPS = loadFromResource("/serenity.properties");
    private static volatile Properties envProps;
    private static volatile boolean baseUrlSet;

    private TestConfig() {
    }

    /** Entorno actual (dev, qa, prod). */
    public static String getEnv() {
        return firstNonNull(System.getProperty(ENV_PROP), DEFAULT_ENV);
    }

    /** Base URL del entorno (sin barra final). Fija webdriver.base.url para Serenity si aún no está fijada. */
    public static String getBaseUrl() {
        ensureEnvLoaded();
        return firstNonNull(
                System.getProperty("webdriver.base.url"),
                System.getProperty("app.base.url"),
                envProps.getProperty("app.base.url"),
                "https://opensource-demo.orangehrmlive.com"
        );
    }

    /** Llamar antes del primer page.open() (p. ej. en Hooks @Before). Fija webdriver.base.url una sola vez. */
    public static void ensureBaseUrlSet() {
        if (baseUrlSet) return;
        String base = getBaseUrl();
        if (base != null && !base.isBlank()) {
            System.setProperty("webdriver.base.url", base.endsWith("/") ? base.substring(0, base.length() - 1) : base);
            baseUrlSet = true;
        }
    }

    public static String getUsername() {
        ensureEnvLoaded();
        return firstNonNull(
                System.getenv("ORANGEHRM_USERNAME"),
                System.getProperty("orangehrm.username"),
                envProps.getProperty("orangehrm.username"),
                SERENITY_PROPS.getProperty("orangehrm.username"),
                "Admin"
        );
    }

    public static String getPassword() {
        ensureEnvLoaded();
        return firstNonNull(
                System.getenv("ORANGEHRM_PASSWORD"),
                System.getProperty("orangehrm.password"),
                envProps.getProperty("orangehrm.password"),
                SERENITY_PROPS.getProperty("orangehrm.password"),
                "admin123"
        );
    }

    private static void ensureEnvLoaded() {
        if (envProps == null) {
            synchronized (TestConfig.class) {
                if (envProps == null) {
                    String env = getEnv();
                    envProps = loadFromResource("/env/" + env + ".properties");
                    if (envProps == null || envProps.isEmpty()) {
                        envProps = new Properties();
                    }
                }
            }
        }
    }

    private static Properties loadFromResource(String path) {
        Properties p = new Properties();
        try (InputStream is = TestConfig.class.getResourceAsStream(path)) {
            if (is != null) p.load(is);
        } catch (IOException ignored) {
            // usar valores por defecto
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
