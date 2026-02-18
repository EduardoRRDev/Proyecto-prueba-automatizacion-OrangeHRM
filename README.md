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
- **CI/CD:** GitHub Actions en `.github/workflows/test.yml`
