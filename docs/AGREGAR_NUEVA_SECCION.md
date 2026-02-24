# Cómo agregar otra sección de la página web al proyecto

Sigue estos pasos en orden. Ejemplo: agregar el módulo **Leave** (solicitar vacaciones).

---

## 1. Crear el Page Object (página de la sección)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/pages/<modulo>/`

**Qué hacer:**
- Crear una clase que extienda `PageObject` (Serenity).
- Anotar con `@DefaultUrl` la URL de esa pantalla (si tiene URL fija).
- Declarar los elementos que vas a usar: inputs, botones, tablas, con `@FindBy`.

**Ejemplo:** `pages/leave/LeaveRequestPage.java`

```java
package co.com.proyecto.automatizacion.pages.leave;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.annotations.DefaultUrl;

@DefaultUrl("https://opensource-demo.orangehrmlive.com/web/index.php/leave/applyLeave")
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
- Declarar las pages que necesites (la nueva + `MainPage` si navegas desde el menú). Serenity las inyecta.
- Métodos con `@Step("descripción legible")` que usen la page: esperas, click, type, etc.
- Si la acción necesita **datos**, recibe un modelo o parámetros; no hardcodear datos en el step.

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
        mainPage.open();  // o un menú específico si lo tienes mapeado
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

- **Crear** `testdata/leave.yml` o un bloque dentro de un YAML existente.
- **Crear** `testdata/flows/leave.yml` con datasets por caso (ej. `REQUEST_LEAVE_BASIC`, `REQUEST_LEAVE_LARGE`).
- **En código:** un modelo (ej. `LeaveRequest`) y en `TestDataLoader` (o un loader específico) un método `getLeaveRequest(caseId)`. Si quieres mantener el mismo patrón que empleado, una fachada tipo `LeaveTestData.getRequest(caseId)` que use el loader.

Si la sección es muy simple (solo dos fechas en el feature), puedes **no** usar YAML y pasar las fechas por parámetros en Gherkin.

---

## 4. Crear las Step Definitions (enganche Gherkin ↔ Steps)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/definitions/`

**Qué hacer:**
- Crear una clase (ej. `LeaveDefinitions`).
- Inyectar los steps con `@Steps` (tus nuevos steps y, si hace falta, LoginSteps para el Given de login).
- Métodos con `@Dado`, `@Cuando`, `@Entonces`, `@Y` cuyo texto coincida con el .feature.
- Dentro del método: llamar al step correspondiente. Si hay datos, obtenerlos de `XxxTestData` o del parámetro del paso.

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
- Crear un `.feature` con lenguaje `# language: es`, la descripción de la funcionalidad y los escenarios en Gherkin usando los pasos que definiste.

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

## 6. Crear el Runner (o reutilizar uno)

**Dónde:** `src/test/java/co/com/proyecto/automatizacion/runners/`

**Qué hacer:**
- Crear una clase con `@RunWith(CucumberWithSerenity.class)` y `@CucumberOptions`.
- Apuntar `features` a la carpeta de esa sección.
- Apuntar `glue` a `co.com.proyecto.automatizacion.definitions` (donde están las definitions; si usas hooks, incluye el paquete donde estén).
- Poner el `tag` que usaste en el feature (ej. `@SolicitarLeave`).

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
        plugin = {"pretty"}
)
public class LeaveRunner {
}
```

---

## Resumen rápido (checklist)

| Paso | Qué creas | Dónde |
|------|-----------|--------|
| 1 | Page Object(s) de la sección | `pages/<modulo>/` |
| 2 | Clase de Steps | `steps/XxxSteps.java` |
| 3 | (Opcional) YAML + modelo + loader/fachada de datos | `testdata/`, `models/`, `data/` |
| 4 | Step Definitions | `definitions/XxxDefinitions.java` |
| 5 | Feature | `features/<modulo>/xxx.feature` |
| 6 | Runner | `runners/XxxRunner.java` |

**Orden recomendado:** 1 → 2 → 4 → 5 → 6. El paso 3 solo si necesitas datos centralizados para esa sección.

**Reutilizar login:** En el feature, empieza con los mismos pasos que ya tienes:  
`Dado que el usuario navega a la página de inicio de sesión` y `Y ingresa las credenciales de acceso correctas`. No hace falta tocar `LoginDefinitions` ni `LoginSteps`.
