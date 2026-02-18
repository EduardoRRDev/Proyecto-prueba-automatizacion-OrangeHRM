# OrangeHRM - Automatización Serenity BDD

Automatización de pruebas para **OrangeHRM Demo** con Serenity BDD, Cucumber y POM.

**URL:** https://opensource-demo.orangehrmlive.com  
**Credenciales:** Admin / admin123

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

## Configuración

- **Credenciales:** `serenity.properties` o variables `ORANGEHRM_USERNAME`, `ORANGEHRM_PASSWORD`

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
