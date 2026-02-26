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
