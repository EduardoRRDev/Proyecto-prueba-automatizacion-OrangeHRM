# Cómo agregar más casos de prueba o una nueva sección

Hay dos situaciones típicas:

- **Solo añadir escenarios** a un feature ya existente (ej. otro escenario en `agregar_empleado.feature`): suele bastar con escribir el escenario en el `.feature`, reutilizar pasos y, si hace falta, añadir step definitions y steps. No hace falta nuevo runner ni nueva página.
- **Añadir una sección/módulo nuevo** (ej. Leave, Vacaciones): hay que seguir todos los pasos de abajo (Page, Steps, datos si aplica, Definitions, Feature, Runner y registrar el rerun).

Sigue los pasos en orden. Ejemplo: agregar el módulo **Leave** (solicitar vacaciones).

---

## 1. Crear el Page Object (página de la sección)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/pages/<modulo>/`

**Qué hacer:**
- Crear una clase que extienda `PageObject` (Serenity).
- La URL puede ser absoluta con `@DefaultUrl` o relativa; si usas entornos (dev/qa/prod), `TestConfig` ya fija `webdriver.base.url` y las páginas pueden usar rutas relativas.
- Declarar los elementos con `@FindBy` (inputs, botones, tablas). Usar `WebElementFacade` y esperas (`.withTimeoutOf()`, `.waitUntilVisible()`) cuando haga falta.

**Ejemplo:** `pages/leave/LeaveRequestPage.java`

```java
package co.com.proyecto.automatizacion.pages.leave;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("/web/index.php/leave/applyLeave")
public class LeaveRequestPage extends PageObject {

    @FindBy(css = "selector-del-campo-fecha-desde")
    public WebElementFacade inputFromDate;

    @FindBy(css = "selector-del-campo-fecha-hasta")
    public WebElementFacade inputToDate;

    @FindBy(xpath = "//button[@type='submit']")
    public WebElementFacade btnApply;
}
```

Si la sección tiene **varias pantallas** (lista + formulario), crea **un Page por pantalla** (ej. `LeaveListPage`, `LeaveRequestPage`).

---

## 2. Crear los Steps (acciones reutilizables)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/steps/`

**Qué hacer:**
- Crear una clase de steps para esa sección (ej. `LeaveSteps`).
- Declarar las pages que necesites; Serenity las inyecta automáticamente.
- Métodos con `@Step("descripción legible")` que usen la page: esperas, click, type, etc.
- Si la acción necesita **datos**, recibe un modelo o parámetros; no hardcodear datos en el step.
- Para **métodos genéricos** (pausas, assertions de texto) usa el paquete **`util`**: `WaitHelper.pause(millis)`, `AssertionHelper.assertTextEquals(...)` / `assertContains(...)`. Ver [**docs/METODOS_GENERICOS_Y_REUTILIZABLES.md**](METODOS_GENERICOS_Y_REUTILIZABLES.md).

**Ejemplo:** `steps/LeaveSteps.java`

```java
package co.com.proyecto.automatizacion.steps;

import co.com.proyecto.automatizacion.pages.leave.LeaveRequestPage;
import co.com.proyecto.automatizacion.pages.common.MainPage;
import net.serenitybdd.annotations.Step;
import java.time.Duration;

public class LeaveSteps {

    private LeaveRequestPage leaveRequestPage;
    private MainPage mainPage;

    @Step("navega a la página de solicitar vacaciones")
    public void navigateToLeaveRequest() {
        mainPage.open();
        leaveRequestPage.open();
    }

    @Step("solicita vacaciones desde {0} hasta {1}")
    public void requestLeave(String fromDate, String toDate) {
        leaveRequestPage.inputFromDate.withTimeoutOf(Duration.ofSeconds(10)).waitUntilVisible().type(fromDate);
        leaveRequestPage.inputToDate.type(toDate);
        leaveRequestPage.btnApply.click();
    }
}
```

---

## 3. (Opcional) Datos de prueba en testdata

**Solo si** esa sección usa datos que quieras centralizar (fechas, tipo de leave, etc.):

- **Estructura:** Los datos viven en **YAML** bajo `src/test/resources/testdata/`:
  - Archivo base (ej. `testdata/leave.yml`) con bloques reutilizables.
  - Archivo por flujo: `testdata/flows/<modulo>.yml` con `datasets:` y un `caseId` por caso (ej. `REQUEST_LEAVE_BASIC`), referenciando un bloque base y opcionalmente `override` para pisar campos.
- **Código:** Un modelo (ej. `LeaveRequest`) y en **TestDataLoader** un método que lea el YAML y devuelva el modelo. Para datos que deban ser **únicos por ejecución** (username, IDs), usa los sufijos que ya provee el proyecto: en el loader obtén `ScenarioContext.get("runSuffix")` y `ScenarioContext.get("runSuffixShort")` y concaténalos al valor (ver `TestDataLoader.getEmployee` y [**docs/DATOS_UNICOS_POR_EJECUCION.md**](DATOS_UNICOS_POR_EJECUCION.md)). Expón los datos con una fachada (ej. `LeaveTestData.getRequest(caseId)`) para que definitions/steps no usen `TestDataLoader` directamente.
- **Documentación:** Ver `testdata/COMO_FUNCIONAN_LOS_DATOS.md` y `docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md` para el patrón usado en empleados.

Si la sección es muy simple (solo dos fechas en el feature), puedes **no** usar YAML y pasar las fechas por parámetros en Gherkin.

---

## 4. Crear las Step Definitions (enganche Gherkin ↔ Steps)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/definitions/`

**Qué hacer:**
- Crear una clase (ej. `LeaveDefinitions`).
- Inyectar los steps con `@Steps` (tus nuevos steps y, si hace falta, `LoginSteps` para el Given de login).
- Métodos con `@Dado`, `@Cuando`, `@Entonces`, `@Y` cuyo texto coincida con el .feature.
- Dentro del método: llamar al step correspondiente. Si hay datos, obtenerlos de la fachada de datos (ej. `XxxTestData`) o del parámetro del paso.

**Importante:** El **glue** de todos los runners es `co.com.proyecto.automatizacion.definitions` (ahí están también los Hooks). No hace falta configurar otro paquete.

**Ejemplo:** `definitions/LeaveDefinitions.java`

```java
package co.com.proyecto.automatizacion.definitions;

import co.com.proyecto.automatizacion.steps.LeaveSteps;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Steps;

public class LeaveDefinitions {

    @Steps
    private LeaveSteps leaveSteps;

    @Cuando("navega a la página de solicitar vacaciones")
    public void navegarSolicitarVacaciones() {
        leaveSteps.navigateToLeaveRequest();
    }

    @Cuando("solicita vacaciones desde {string} hasta {string}")
    public void solicitaVacaciones(String desde, String hasta) {
        leaveSteps.requestLeave(desde, hasta);
    }

    @Entonces("ve la solicitud enviada")
    public void veSolicitudEnviada() {
        leaveSteps.verifyRequestSubmitted();
    }
}
```

---

## 5. Crear el archivo Feature (escenarios)

**Dónde:** `src/test/resources/features/<nombre_modulo>/`

**Qué hacer:**
- Crear una carpeta para la sección (ej. `leave`).
- Crear un `.feature` con `# language: es`, la descripción de la funcionalidad y los escenarios en Gherkin usando los pasos que definiste.
- Usar un **tag** por módulo o flujo (ej. `@SolicitarLeave`) para poder filtrar en el runner.

**Ejemplo:** `features/leave/solicitar_vacaciones.feature`

```gherkin
# language: es

Característica: Solicitar vacaciones
  Como empleado quiero solicitar días de leave para que queden registrados.

  @SolicitarLeave
  Escenario: Solicitar vacaciones básico
    Dado que el usuario navega a la página de inicio de sesión
    Y ingresa las credenciales de acceso correctas
    Cuando navega a la página de solicitar vacaciones
    Y solicita vacaciones desde "2025-03-01" hasta "2025-03-05"
    Entonces ve la solicitud enviada
```

---

## 6. Crear el Runner y registrar el rerun

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/runners/`

**Qué hacer:**
- Crear una clase con `@RunWith(CucumberWithSerenity.class)` y `@CucumberOptions`.
- **features:** ruta a la carpeta de esa sección, ej. `"src/test/resources/features/leave"`.
- **glue:** `{"co.com.proyecto.automatizacion.definitions"}`.
- **tags:** el mismo que usaste en el feature (ej. `@SolicitarLeave`).
- **plugin:** incluir `"rerun:target/rerun-<nombre>.txt"` (ej. `rerun:target/rerun-leave.txt`) para que los escenarios fallidos participen en el retry automático (ver [**docs/RETRY.md**](RETRY.md)).
- Copiar el **bloque static** que usan `AddEmployeeRunner` y `LoginRunner` para que, en ejecución paralela, cada worker escriba en su propio directorio de Serenity (ver [**docs/EJECUCION_PARALELA.md**](EJECUCION_PARALELA.md)).

**Después de crear el runner:** Añadir el archivo rerun al **RerunFailedRunner** en la opción `features`, para que los fallos de tu nuevo módulo se reintenten junto con el resto:

```java
features = {
        "@target/rerun-add-employee.txt",
        "@target/rerun-login.txt",
        "@target/rerun-leave.txt"   // nuevo
},
```

**Ejemplo:** `runners/LeaveRunner.java`

```java
package co.com.proyecto.automatizacion.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/leave",
        glue = {"co.com.proyecto.automatizacion.definitions"},
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        tags = "@SolicitarLeave",
        plugin = {"pretty", "rerun:target/rerun-leave.txt"}
)
public class LeaveRunner {

    static {
        String worker = System.getProperty("org.gradle.test.worker");
        if (worker != null && !worker.isEmpty()) {
            String base = System.getProperty("serenity.outputDirectory");
            if (base != null) {
                System.setProperty("serenity.outputDirectory", base + "-" + worker.replaceAll("[^a-zA-Z0-9]", "_"));
            }
        }
    }
}
```

---

## Resumen rápido (checklist)

| Paso | Qué creas | Dónde |
|------|-----------|--------|
| 1 | Page Object(s) de la sección | `pages/<modulo>/` |
| 2 | Clase de Steps | `steps/XxxSteps.java` |
| 3 | (Opcional) YAML + modelo + método en TestDataLoader + fachada de datos | `testdata/`, `testdata/flows/`, `models/`, `data/` |
| 4 | Step Definitions | `definitions/XxxDefinitions.java` |
| 5 | Feature | `features/<modulo>/xxx.feature` |
| 6 | Runner con plugin `rerun` y static block | `runners/XxxRunner.java` |
| 6b | Añadir `@target/rerun-<nombre>.txt` en RerunFailedRunner | `runners/RerunFailedRunner.java` |

**Orden recomendado:** 1 → 2 → 4 → 5 → 6 → 6b. El paso 3 solo si necesitas datos centralizados para esa sección.

**Reutilizar login:** En el feature, empieza con los mismos pasos que ya tienes:  
`Dado que el usuario navega a la página de inicio de sesión` y `Y ingresa las credenciales de acceso correctas`. No hace falta tocar `LoginDefinitions` ni `LoginSteps`.

**Solo añadir escenarios a un módulo existente:** Escribe el escenario en el `.feature`; si los pasos ya existen, no necesitas nuevo runner. Si añades pasos nuevos, crea las step definitions (y steps si hace falta) en las clases existentes del módulo.
