# OrangeHRM - Automatización Serenity BDD

Automatización de pruebas para **OrangeHRM Demo** con Serenity BDD, Cucumber y POM.

**URL:** https://opensource-demo.orangehrmlive.com  
**Credenciales:** Admin / admin123

## Estructura del proyecto (patrón POM)

```
src/test/java/co/com/proyecto/automatizacion/
├── config/          # Configuración (TestConfig, Paths)
├── pages/           # Page Objects por módulo
│   ├── login/       # LoginPage
│   ├── pim/         # AddEmployeePage, EmployeeListPage
│   └── common/      # MainPage (Dashboard)
├── steps/           # Lógica de interacción reutilizable
├── definitions/     # Step definitions (Cucumber) + Hooks
├── models/          # Modelos de datos (Employee)
├── data/            # Carga de datos (EmployeeTestData, TestDataLoader)
├── context/         # Datos en ejecución (ScenarioContext, RuntimeCounter)
└── runners/         # Test runners (Login, AddEmployee, RerunFailed)
```

## Estrategia de datos

Los datos de prueba están en **`src/test/resources/testdata/`** (YAML):

- **Bloques base:** `testdata/empleados.yml`, `testdata/login.yml`
- **Casos por flujo:** `testdata/flows/add_employee.yml` (DEFAULT, ADD_EMPLOYEE, etc.)

El código usa **EmployeeTestData** (`getDefaultEmployee()`, `getEmployee(caseId)`); la carga desde YAML la hace `TestDataLoader`. Para datos que se calculan o capturan en ejecución se usa **ScenarioContext** y **RuntimeCounter**.

`TestDataLoader` agrega un **sufijo único por ejecución** al `username` y `employeeId` para evitar colisiones cuando el dato ya existe en el sistema demo.

- Cómo funciona el flujo: [`docs/COMO_FUNCIONAN_LOS_DATOS.md`](docs/COMO_FUNCIONAN_LOS_DATOS.md)
- Explicación TestDataLoader / EmployeeTestData / Definitions: [`docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`](docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md)

## Requisitos

- JDK 17
- Chrome

## Ejecutar

```bash
# Todos los tests
./gradlew clean test

# Solo login
./gradlew clean test --tests "co.com.proyecto.automatizacion.runners.LoginRunner"

# Solo agregar empleado
./gradlew clean test --tests "co.com.proyecto.automatizacion.runners.AddEmployeeRunner"

# Reintentar solo los escenarios que fallaron en la ejecución anterior
./gradlew test --tests "co.com.proyecto.automatizacion.runners.RerunFailedRunner"
```

> **Windows:** usar `.\gradlew` en PowerShell.

## Retry (reintentos en fallos)

Cuando un escenario falla, se genera automáticamente un archivo `target/rerun-*.txt` con su ubicación. El `RerunFailedRunner` lo lee y reintenta **solo ese escenario** sin volver a correr toda la suite.

En CI el retry se activa automáticamente si el paso principal falla. Si todo pasa, el paso de retry se omite.

Detalle: [**docs/RETRY.md**](docs/RETRY.md)

## Reporte

Tras ejecutar, el reporte está en `target/site/serenity/index.html`  
También se guarda una copia histórica en `target/site/serenity-YYYYMMDD-HHmmss/index.html`

## Entornos (dev / qa / prod)

Puedes ejecutar contra distintos entornos con **`-Denv=dev|qa|prod`** (por defecto **qa**). La base URL se lee de `src/test/resources/env/{env}.properties`.

```bash
# QA (por defecto)
./gradlew test

# Desarrollo
./gradlew test -Denv=dev

# Producción
./gradlew test -Denv=prod
```

Detalle: [**docs/ENTORNOS.md**](docs/ENTORNOS.md)

## Configuración

- **Entorno:** `-Denv=dev|qa|prod` y archivos en `src/test/resources/env/`
- **Credenciales:** variables de entorno `ORANGEHRM_USERNAME` / `ORANGEHRM_PASSWORD`, o `-Dorangehrm.username` / `-Dorangehrm.password`, o `serenity.properties`

## CI/CD

El proyecto usa **GitHub Actions** para ejecutar los tests automáticamente.

| Aspecto | Detalle |
|---------|---------|
| **Workflow** | `.github/workflows/test.yml` |
| **Disparadores** | `push` y `pull_request` en ramas `main` y `master` |
| **Entorno** | `ubuntu-latest`, JDK 17 (Temurin), Chrome |
| **Tests** | `AddEmployeeRunner` por defecto. Para todos, editar el workflow. |
| **Retry** | Si el paso principal falla, se ejecuta `RerunFailedRunner` automáticamente |
| **Modo** | Headless (`-Dheadless.mode=true`) |
| **Reporte** | Artifact `serenity-report` (retención 7 días) |
| **Timeout** | 15 minutos |

Para ver los resultados: **Actions** → seleccionar el workflow → descargar el artifact del reporte Serenity.

## Documentación

- **Datos (testdata / TestDataLoader / Definitions):** [`docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`](docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md)
- **Cómo funcionan los datos:** [`docs/COMO_FUNCIONAN_LOS_DATOS.md`](docs/COMO_FUNCIONAN_LOS_DATOS.md)
- **Retry (reintentos en fallos):** [`docs/RETRY.md`](docs/RETRY.md)
- **Múltiples entornos (dev/qa/prod):** [`docs/ENTORNOS.md`](docs/ENTORNOS.md)
- **Cómo agregar otra sección de la web:** [`docs/AGREGAR_NUEVA_SECCION.md`](docs/AGREGAR_NUEVA_SECCION.md)
- **Historia de usuario (Add Employee):** [`docs/historia-usuario-agregar-empleado.md`](docs/historia-usuario-agregar-empleado.md)
- **Mejoras y próximos pasos sugeridos:** [`docs/MEJORAS_Y_PROXIMOS_PASOS.md`](docs/MEJORAS_Y_PROXIMOS_PASOS.md)
