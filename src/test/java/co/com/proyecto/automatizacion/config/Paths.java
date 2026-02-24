package co.com.proyecto.automatizacion.config;

/**
 * Rutas de la aplicación (paths relativos). Se usan con TestConfig.getBaseUrl() + path
 * para abrir páginas según el entorno (dev/qa/prod). Un solo lugar para mantener las rutas.
 */
public final class Paths {

    private Paths() {
    }

    public static final String LOGIN = "/web/index.php/auth/login";
    public static final String ADD_EMPLOYEE = "/web/index.php/pim/addEmployee";
    public static final String VIEW_EMPLOYEE_LIST = "/web/index.php/pim/viewEmployeeList";
}
