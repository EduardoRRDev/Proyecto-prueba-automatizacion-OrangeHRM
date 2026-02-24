# Múltiples entornos (dev / qa / prod)

El proyecto puede ejecutarse contra distintos entornos cambiando la **base URL** y las **credenciales** sin tocar código.

**Diseño:** Base URL en `TestConfig` + rutas en `Paths` + `openAt(base + path)` en steps. Las rutas están centralizadas en una sola clase; la lógica de entorno no se duplica.

## Cómo elegir el entorno

- **Por defecto:** se usa el entorno **dev**.
- **Cambiar entorno:** propiedad de sistema `-Denv=dev|qa|prod`.

Ejemplos:

```bash
# Entorno desarrollo (por defecto)
./gradlew test

# Entorno QA
./gradlew test -Denv=qa

# Entorno producción
./gradlew test -Denv=prod
```

En Windows (PowerShell), si pasas `-D` con Gradle, usa comillas cuando sea necesario:

```powershell
.\gradlew test "-Denv=qa"
```

## Dónde se configura cada entorno

Archivos en **`src/test/resources/env/`**:

| Archivo              | Uso        |
|----------------------|------------|
| `env/dev.properties`  | Entorno desarrollo (por defecto) |
| `env/qa.properties`   | Entorno QA |
| `env/prod.properties` | Entorno producción |

Cada archivo puede definir:

| Propiedad            | Descripción                          | Ejemplo |
|----------------------|--------------------------------------|--------|
| `app.base.url`        | URL base de la aplicación (sin barra final) | `https://opensource-demo.orangehrmlive.com` |
| `orangehrm.username`  | Usuario de login                     | `Admin` |
| `orangehrm.password`  | Contraseña                           | `admin123` |

## Prioridad de configuración

Para **base URL** y **credenciales**:

1. Variables de entorno: `ORANGEHRM_USERNAME`, `ORANGEHRM_PASSWORD`
2. Propiedades del sistema: `-Dorangehrm.username=...`, `-Dwebdriver.base.url=...`
3. Archivo del entorno: `env/{env}.properties` (según `-Denv`)
4. `serenity.properties`
5. Valor por defecto en código

Así puedes sobrescribir solo lo que necesites (por ejemplo en CI: `-Denv=qa` y secretos en variables de entorno).

## Cómo funciona en el código

- **TestConfig** lee `-Denv` (por defecto `dev`), carga `env/{env}.properties` y expone `getBaseUrl()`, `getUsername()`, `getPassword()`.
- **Paths** (en `config/Paths.java`) centraliza las rutas: `LOGIN`, `ADD_EMPLOYEE`, `VIEW_EMPLOYEE_LIST`. Así no se repiten strings en varios steps.
- Los **Steps** que abren una página usan `page.openAt(TestConfig.getBaseUrl() + Paths.XXX)` para que la URL dependa del entorno.
- Las **Pages** mantienen `@DefaultUrl` completo (fallback para el IDE). En Serenity, `open()` es `final`, por eso no se puede sobrescribir en una BasePage para aplicar la base URL; por ello se usa `openAt(base + path)` en los steps.
- **Hooks** ejecuta `TestConfig.ensureBaseUrlSet()` antes de cada escenario (opcional; `getBaseUrl()` ya carga el env a demanda).

## Añadir un nuevo entorno

1. Crear `src/test/resources/env/miambiente.properties` con `app.base.url`, `orangehrm.username`, `orangehrm.password`.
2. Ejecutar con `-Denv=miambiente`.

## CI (GitHub Actions)

Para ejecutar contra QA en cada push, en el workflow usa:

```yaml
- name: Run tests
  run: ./gradlew test ... "-Denv=qa"
```

Para no guardar credenciales en el repo, define `ORANGEHRM_USERNAME` y `ORANGEHRM_PASSWORD` como secrets y pásalos como variables de entorno en el job.
