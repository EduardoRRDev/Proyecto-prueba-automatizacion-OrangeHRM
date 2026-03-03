# Reintentos en fallos (Retry)

## Por qué existe esto

En automatización web, un escenario puede fallar por razones ajenas al código:
- La página tardó más de lo esperado en cargar.
- Un elemento apareció con retraso (red lenta, servidor ocupado).
- Un modal bloqueó un clic momentáneamente.

En lugar de marcar el escenario como fallido de inmediato, el mecanismo de retry lo vuelve a ejecutar una vez. Si pasa en el segundo intento, se considera estable. Si vuelve a fallar, el fallo es real.

---

## Cómo funciona (flujo completo)

```
1. AddEmployeeRunner / LoginRunner se ejecutan normalmente.
   │
   ├── Si todos pasan → fin. No se genera nada relevante en rerun-*.txt.
   │
   └── Si alguno falla → Cucumber escribe la ruta + línea del escenario fallido
       en target/rerun-add-employee.txt o target/rerun-login.txt.

2. RerunFailedRunner lee esos archivos y ejecuta SOLO los escenarios fallidos.
   │
   ├── Si ahora pasan → el fallo era intermitente (flaky). Suite OK.
   │
   └── Si vuelven a fallar → fallo real. Se reporta en target/rerun-second-attempt.txt.
```

---

## Archivos involucrados

| Archivo | Rol |
|---------|-----|
| `runners/AddEmployeeRunner.java` | Genera `target/rerun-add-employee.txt` si hay fallos |
| `runners/LoginRunner.java` | Genera `target/rerun-login.txt` si hay fallos |
| `runners/RerunFailedRunner.java` | Lee los `.txt` y reintenta los escenarios fallidos |
| `.github/workflows/test.yml` | Ejecuta el retry automáticamente en CI si el paso principal falla |

---

## Uso local

### Paso 1 — Ejecutar la suite normal

```powershell
./gradlew clean test --tests "co.com.proyecto.automatizacion.runners.AddEmployeeRunner"
```

Si hay fallos, Gradle termina con error y se generan los archivos `target/rerun-*.txt`.

### Paso 2 — Reintentar solo los fallidos

```powershell
./gradlew test --tests "co.com.proyecto.automatizacion.runners.RerunFailedRunner"
```

> **Importante:** No uses `clean` en el paso 2, o se borrarán los archivos `rerun-*.txt`.

---

## Cómo funciona en CI (GitHub Actions)

```yaml
- name: Run tests
  id: run_tests
  run: ./gradlew clean test ...
  continue-on-error: true        # No detiene el pipeline si falla

- name: Retry failed scenarios
  if: steps.run_tests.outcome == 'failure'   # Solo si el paso anterior falló
  run: ./gradlew test --tests "...RerunFailedRunner" ...
```

El paso de retry solo se activa si el paso principal tuvo fallos. Si todo pasó, el retry se omite y no consume tiempo.

---

## Cómo se ven los reintentos en el reporte

**No hay un solo reporte que muestre “falló → se reintentó → pasó”.** Serenity genera un reporte por ejecución, y el retry es una **segunda ejecución** que **sobrescribe** el directorio de salida. Por eso:

| Situación | Qué ves |
|-----------|--------|
| **Reporte final** (artifact `serenity-report`) | Siempre es el resultado de la **última** ejecución. Si hubo retry, ese reporte contiene **solo** los escenarios que se reintentaron y su resultado en el 2.º intento (pasaron o volvieron a fallar). No incluye el primer intento. |
| **Reporte del primer intento** (artifact `serenity-report-first-attempt`) | Solo existe en CI cuando el primer paso falló. Contiene los **fallos originales** (los escenarios que fallaron en la primera ejecución). Se sube como artifact aparte para que puedas comparar. |

**En resumen:**

- **Primer intento con fallos** → se guarda una copia en `serenity-first-attempt` y se sube como `serenity-report-first-attempt`.
- **Retry** → se ejecuta y escribe en `target/site/serenity`, que es lo que se sube como `serenity-report` (reporte final).

Para ver “qué falló la primera vez” y “qué pasó en el reintento” hay que abrir **los dos** artifacts: el del primer intento y el final.

**Local:** Si ejecutas primero AddEmployeeRunner y luego RerunFailedRunner, el reporte en `target/site/serenity/index.html` solo mostrará la ejecución del RerunFailedRunner (los escenarios reintentados). No se guarda automáticamente una copia del primer intento; puedes copiar `target/site/serenity` a otra carpeta antes de lanzar el retry si quieres conservar el reporte del primer intento.

---

## Qué NO hace este mecanismo

- **No oculta fallos reales.** Si el escenario falla dos veces seguidas, sigue apareciendo como fallido en el reporte.
- **No reintenta infinitamente.** Solo hay un reintento. Si necesitas más, puedes encadenar otro runner similar.
- **No es un reemplazo de esperas correctas.** Si un escenario siempre falla por falta de `waitFor`, la solución es mejorar el selector o la espera, no solo reintentar.

---

## Agregar retry a un nuevo runner

Cuando crees un runner nuevo, agrega el plugin `rerun` apuntando a un archivo único:

```java
plugin = {"pretty", "rerun:target/rerun-mi-seccion.txt"}
```

Luego agrega ese archivo a la lista de `features` en `RerunFailedRunner`:

```java
features = {
    "@target/rerun-add-employee.txt",
    "@target/rerun-login.txt",
    "@target/rerun-mi-seccion.txt"   // <-- nuevo
}
```
