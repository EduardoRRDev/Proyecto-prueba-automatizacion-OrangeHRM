# Métodos genéricos y reutilizables (getText, assertions, cálculos, etc.)

## Implementación actual: paquete `util`

El proyecto **centraliza** helpers reutilizables en el paquete **`co.com.proyecto.automatizacion.util`** con métodos estáticos. Cualquier step o page puede usarlos sin herencia.

| Clase | Métodos | Uso |
|-------|---------|-----|
| **WaitHelper** | `pause(int millis)` | Pausa fija en ms. Usado en AddEmployeeSteps y EmployeeListSteps. |
| **AssertionHelper** | `assertTextEquals(message, actual, expected)` | Comparación exacta de texto (Hamcrest por debajo). Usado en LoginSteps. |
| **AssertionHelper** | `assertContains(message, actual, expected)` | Verifica que `actual` contenga el fragmento `expected`. |

- **getText:** Sigue usándose sobre el elemento de la Page (`element.waitUntilVisible().getText()`); si en el futuro quieres un helper, se puede añadir `WaitHelper.getTextWithWait(element, seconds)`.
- **Esperas explícitas:** Siguen en cada step con la API de Serenity (`withTimeoutOf`, `waitUntilVisible`).
- **Cálculos genéricos:** Cuando hagan falta, se pueden añadir en `util/CalculationHelper` o similar.

---

## Opciones para centralizar métodos genéricos

Si quieres **evitar duplicar** lógica (pausas, assertions de texto, getText con espera, cálculos), puedes seguir una de estas estrategias.

### Opción A: Clase base para Steps (`BaseSteps`)

- Crear `steps/BaseSteps.java` (o `steps/support/BaseSteps.java`) con métodos **protegidos** que usen los steps hijos.
- Los steps de cada módulo extienden `BaseSteps` y reutilizan esos métodos.

**Ventaja:** Todos los steps tienen acceso a helpers sin inyectar nada nuevo.  
**Inconveniente:** Solo sirve para steps; las pages seguirían usando Serenity a pelo.

Ejemplo mínimo:

```java
// steps/BaseSteps.java
public abstract class BaseSteps {

    protected static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected static String getTextWithWait(WebElementFacade element, int seconds) {
        return element.withTimeoutOf(Duration.ofSeconds(seconds)).waitUntilVisible().getText();
    }

    protected static void assertTextEquals(String message, String actual, String expected) {
        assertThat(message, actual, is(equalTo(expected)));
    }
}
```

Luego en `AddEmployeeSteps extends BaseSteps` y `EmployeeListSteps extends BaseSteps` podrías usar `pause(1500)` y eliminar el `pause` duplicado.

---

### Opción B: Paquete `util` con clases estáticas ✅ Implementado

- **Ubicación:** `src/test/java/co/com/proyecto/automatizacion/util/`
- **WaitHelper:** `pause(int millis)` — pausa fija; usar con moderación (preferir esperas explícitas).
- **AssertionHelper:** `assertTextEquals(message, actual, expected)` y `assertContains(message, actual, expected)` — usan Hamcrest por debajo.

Cualquier step o page puede llamar `WaitHelper.pause(500)` o `AssertionHelper.assertTextEquals("...", actual, "esperado")` sin herencia.

---

### Opción C: Solo Serenity + Hamcrest (mantener el enfoque actual)

- **getText / esperas:** Seguir usando los métodos de `WebElementFacade` en cada step.
- **Assertions:** Seguir usando `assertThat` de Hamcrest donde haga falta (como en `LoginSteps`).
- **Pausas:** Extraer **solo** `pause(int ms)` a una clase `util/WaitHelper` o a `BaseSteps` para no duplicar el mismo código en varias clases.

Es el cambio más pequeño y ya mejora la reutilización donde hoy hay duplicación (pause).

---

## Uso en nuevas secciones

Al añadir una **nueva sección** (ver [AGREGAR_NUEVA_SECCION.md](AGREGAR_NUEVA_SECCION.md)), usa los helpers existentes en lugar de repetir lógica:

- **Pausas:** `WaitHelper.pause(millis)`.
- **Validar texto exacto:** `AssertionHelper.assertTextEquals(mensaje, textoObtenido, textoEsperado)`.
- **Validar que un texto contenga un fragmento:** `AssertionHelper.assertContains(mensaje, textoCompleto, fragmento)`.

Si necesitas **cálculos genéricos** (fechas, totales, etc.), crea un **nuevo helper** en el paquete `util` siguiendo la convención de abajo.

---

## Convención: un helper por dominio

Cuando añadas métodos de ayuda nuevos (fechas, cálculos, incrementos, etc.), **crea una clase nueva por tipo de utilidad**, no un solo archivo con todo. Así cada helper tiene una responsabilidad clara y el código escala mejor.

| Dominio | Clase sugerida | Ejemplos de métodos |
|---------|----------------|---------------------|
| Esperas | `WaitHelper` ✅ | `pause(ms)` |
| Aserciones | `AssertionHelper` ✅ | `assertTextEquals`, `assertContains` |
| Fechas | `DateHelper` | `formatDate`, `addDays`, `todayAsString`, `parse` |
| Cálculos / números | `CalculationHelper` | `sum`, `percentage`, `round` |
| Incrementos o IDs | `CalculationHelper` o `StringHelper` | `increment(int)`, `nextId()`, `increment(String base)` |

- **Fechas** → `DateHelper` (usa `java.time` si hace falta).
- **Cálculos numéricos** (totales, porcentajes, redondeos) → `CalculationHelper`.
- **Incrementos** (siguiente número, siguiente código) → pueden ir en `CalculationHelper` si son numéricos, o en un método de `StringHelper` si es “siguiente valor” tipo string.

Cada clase: `public final`, constructor privado, solo métodos estáticos. Ubicación: `src/test/java/co/com/proyecto/automatizacion/util/`.
