# E2E Automation — Serenity BDD & Cucumber

## Sobre el proyecto

Framework de **automatización E2E** para una aplicación web de gestión (demo). Desarrollado con **Serenity BDD**, **Cucumber** y **Java 17** siguiendo el patrón **Page Object Model (POM)**.

Incluye flujos de **login** y **alta de empleados** (formulario, lista, búsqueda y eliminación), datos de prueba en **YAML**, **reintentos** ante fallos, **ejecución paralela**, **múltiples entornos** (dev/qa/prod) y reportes **Serenity**. Los tests se ejecutan en **GitHub Actions** y el reporte se publica como artifact.

**Aplicación bajo prueba (demo):** https://opensource-demo.orangehrmlive.com  
**Credenciales:** Admin / admin123

## Estructura del proyecto (patrón POM)

**Código (Java):**

```
src/test/java/co/com/proyecto/automatizacion/
├── config/          # Configuración (TestConfig, Paths)
├── pages/           # Page Objects por módulo
│   ├── login/       # LoginPage
│   ├── pim/         # AddEmployeePage, EmployeeListPage
│   └── common/      # MainPage (Dashboard)
├── steps/           # Lógica de interacción (LoginSteps, AddEmployeeSteps, EmployeeListSteps)
├── definitions/     # Step definitions (Cucumber) + Hooks
├── models/          # Modelos de datos (Employee)
├── data/            # Carga de datos (EmployeeTestData, TestDataLoader) — lee desde testdata/
├── context/         # Datos en ejecución (ScenarioContext)
├── util/            # Helpers reutilizables (WaitHelper, AssertionHelper)
└── runners/         # Runners (AddEmployeeRunner, LoginRunner, RerunFailedRunner)
```

**Recursos (test):**

```
src/test/resources/
├── features/        # Escenarios Gherkin
│   ├── add_employee/
│   └── login/
├── testdata/        # Datos en YAML (empleados.yml, login.yml, flows/)
├── env/             # Entornos (dev, qa, prod).properties
├── serenity.properties
└── logback-test.xml
```

**Datos de prueba:** en **`src/test/resources/testdata/`** (YAML). No se usa `data/employees.properties`; todo está en **testdata/**.

## Estrategia de datos

Los datos de prueba están en **`src/test/resources/testdata/`** (YAML), no en properties ni en `data/`:

- **Bloques base:** `testdata/empleados.yml`, `testdata/login.yml`
- **Casos por flujo:** `testdata/flows/add_employee.yml` (DEFAULT, ADD_EMPLOYEE, etc.)

El código usa **EmployeeTestData** (`getDefaultEmployee()`, `getEmployee(caseId)`); la carga desde YAML la hace **TestDataLoader**. Para datos que se calculan o capturan en ejecución se usa **ScenarioContext** y **RuntimeCounter**.

`TestDataLoader` agrega un **sufijo único por escenario** (UUID) al `username` y `employeeId` para evitar colisiones cuando varios jobs o usuarios ejecutan tests a la vez sobre la misma base. El sufijo se fija en `Hooks` y se reutiliza en todo el escenario vía **ScenarioContext**. Detalle: [**Datos únicos por ejecución**](docs/DATOS_UNICOS_POR_EJECUCION.md).

**Documentación:**

- [**Cómo funcionan los datos**](docs/COMO_FUNCIONAN_LOS_DATOS.md) — flujo desde YAML hasta los steps
- [**Datos, testdata y Employee**](docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md) — TestDataLoader, EmployeeTestData y su uso en Definitions

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

## Ejecución en paralelo

Para reducir tiempo cuando ejecutas **todos** los tests (varios runners), usa **`-Pparallel=N`** (ej. `-Pparallel=2`). Gradle lanzará N workers en paralelo; cada uno escribe en su propio directorio y luego se une un reporte. Por defecto no se usa paralelo (1 worker). Detalle: [**docs/EJECUCION_PARALELA.md**](docs/EJECUCION_PARALELA.md).

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

- **Datos únicos por ejecución (evitar colisiones):** [`docs/DATOS_UNICOS_POR_EJECUCION.md`](docs/DATOS_UNICOS_POR_EJECUCION.md)
- **Datos (testdata / TestDataLoader / Definitions):** [`docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`](docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md)
- **Cómo funcionan los datos:** [`docs/COMO_FUNCIONAN_LOS_DATOS.md`](docs/COMO_FUNCIONAN_LOS_DATOS.md)
- **Retry (reintentos en fallos):** [`docs/RETRY.md`](docs/RETRY.md)
- **Ejecución en paralelo:** [`docs/EJECUCION_PARALELA.md`](docs/EJECUCION_PARALELA.md)
- **Múltiples entornos (dev/qa/prod):** [`docs/ENTORNOS.md`](docs/ENTORNOS.md)
- **Cómo agregar otra sección de la web:** [`docs/AGREGAR_NUEVA_SECCION.md`](docs/AGREGAR_NUEVA_SECCION.md)
- **Historia de usuario (Add Employee):** [`docs/historia-usuario-agregar-empleado.md`](docs/historia-usuario-agregar-empleado.md)
- **Mejoras y próximos pasos sugeridos:** [`docs/MEJORAS_Y_PROXIMOS_PASOS.md`](docs/MEJORAS_Y_PROXIMOS_PASOS.md)

---

**Descripción sugerida para el About del repositorio (GitHub):**  
*Framework de automatización E2E con Serenity BDD, Cucumber y Java. POM, datos en YAML, CI en GitHub Actions, reportes Serenity.*
