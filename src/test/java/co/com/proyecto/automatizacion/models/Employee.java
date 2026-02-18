package co.com.proyecto.automatizacion.models;

/**
 * Modelo de datos para empleado.
 * Patrón Builder para construcción flexible y datos desacoplados de los steps.
 */
public class Employee {

    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String employeeId;
    private final String username;
    private final String password;

    private Employee(Builder builder) {
        this.firstName = builder.firstName;
        this.middleName = builder.middleName;
        this.lastName = builder.lastName;
        this.employeeId = builder.employeeId;
        this.username = builder.username;
        this.password = builder.password;
    }

    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getEmployeeId() { return employeeId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    /** Nombre completo para búsqueda (First + Middle) */
    public String getFullFirstName() {
        return (middleName != null && !middleName.isBlank())
            ? firstName + " " + middleName
            : firstName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;
        private String middleName;
        private String lastName;
        private String employeeId;
        private String username;
        private String password;

        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder middleName(String middleName) { this.middleName = middleName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }

        public Employee build() {
            return new Employee(this);
        }
    }
}
