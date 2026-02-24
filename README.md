# OrangeHRM - Automatización Serenity BDD

Automatización de pruebas para **OrangeHRM Demo** con Serenity BDD, Cucumber y POM.

**URL:** https://opensource-demo.orangehrmlive.com  
**Credenciales:** Admin / admin123

## Estructura del proyecto (patrón POM)

```
src/test/java/co/com/proyecto/automatizacion/
├── config/          # Configuración (credenciales, propiedades)
├── pages/           # Page Objects por módulo
│   ├── login/       # LoginPage
│   ├── pim/         # AddEmployeePage, EmployeeListPage
│   └── common/      # MainPage (Dashboard)
├── steps/           # Lógica de interacción reutilizable
├── definitions/     # Step definitions (Cucumber)
├── models/          # Modelos de datos (Employee)
├── data/            # Carga de datos (EmployeeTestData, TestDataLoader)
├── context/         # Datos en ejecución (ScenarioContext, RuntimeCounter)
└── runners/        # Test runners
```

## Estrategia de datos

Los datos de prueba están en **`src/test/resources/testdata/`** (YAML):

- **Bloques base:** `testdata/empleados.yml`, `testdata/login.yml`
- **Casos por flujo:** `testdata/flows/add_employee.yml` (DEFAULT, ADD_EMPLOYEE, etc.)

El código usa **EmployeeTestData** (getDefaultEmployee(), getEmployee(caseId)); la carga desde YAML la hace TestDataLoader. Para datos que se calculan o capturan en ejecución se usa **ScenarioContext** y **RuntimeCounter**.

- Cómo funciona el flujo: [`src/test/resources/testdata/COMO_FUNCIONAN_LOS_DATOS.md`](src/test/resources/testdata/COMO_FUNCIONAN_LOS_DATOS.md)
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
```

> **Windows:**  `./gradlew`

**Nota (Add Employee):** El escenario incluye el paso `elimina el empleado de la lista` al final. Si no se elimina el empleado, al volver a ejecutar el test falla porque el usuario ya está registrado en el sistema.

## Reporte

Tras ejecutar, el reporte está en `target/site/serenity-YYYYMMDD-HHmmss/index.html`

## Documentación

- **Historia de usuario (Add Employee):** [`docs/historia-usuario-agregar-empleado.md`](docs/historia-usuario-agregar-empleado.md) | [Word](docs/historia-usuario-agregar-empleado.docx)
- **Datos (testdata / TestDataLoader / Definitions):** [`docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`](docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md)
- **Cómo agregar otra sección de la web:** [`docs/AGREGAR_NUEVA_SECCION.md`](docs/AGREGAR_NUEVA_SECCION.md)
- **Múltiples entornos (dev/qa/prod):** [`docs/ENTORNOS.md`](docs/ENTORNOS.md)
- **Mejoras y próximos pasos sugeridos:** [`docs/MEJORAS_Y_PROXIMOS_PASOS.md`](docs/MEJORAS_Y_PROXIMOS_PASOS.md)

## Entornos (dev / qa / prod)

Puedes ejecutar contra distintos entornos con **`-Denv=dev|qa|prod`** (por defecto `dev`). La base URL y las credenciales se leen de `src/test/resources/env/{env}.properties`.

```bash
./gradlew test -Denv=qa
```

Detalle: [**docs/ENTORNOS.md**](docs/ENTORNOS.md)

## Configuración

- **Entorno:** `-Denv=dev|qa|prod` y archivos en `src/test/resources/env/`.
- **Credenciales:** env file, `serenity.properties` o variables `ORANGEHRM_USERNAME`, `ORANGEHRM_PASSWORD`

## CI/CD

El proyecto usa **GitHub Actions** para ejecutar los tests automáticamente.

| Aspecto | Detalle                                                                                                               |
|---------|-----------------------------------------------------------------------------------------------------------------------|
| **Workflow** | `.github/workflows/test.yml`                                                                                          |
| **Disparadores** | `push` y `pull_request` en ramas `main` y `master`                                                                    |
| **Entorno** | `ubuntu-latest`, JDK 17 (Temurin), Chrome                                                                             |
| **Tests** | Solo `AddEmployeeRunner` por defecto. Para ejecutar todos, editar el workflow y descomentar la línea correspondiente. |
| **Modo** | Headless (`-Dheadless.mode=true`)                                                                                     |
| **Reporte** | Se sube como artifact `serenity-report` (retención 7 días). Descargable desde la ejecución del workflow..             |
| **Timeout** | 15 minutos                                                                                                            |

Para ver los resultados: **Actions** → seleccionar el workflow → descargar el artifact del reporte Serenity.
