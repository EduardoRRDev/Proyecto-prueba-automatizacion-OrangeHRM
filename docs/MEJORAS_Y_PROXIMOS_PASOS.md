# Qué más le haría falta al proyecto (mejoras sugeridas)

El proyecto ya tiene: POM, SOLID, datos en YAML (testdata), reportes Serenity, CI en GitHub Actions, documentación de datos y de cómo agregar secciones. Aquí se listan mejoras opcionales ordenadas por impacto/prioridad.

---

## 1. Actualizar README (datos y estructura)

- **Qué:** En el README sigue apareciendo `data/employees.properties`. Debería indicar que los datos están en **testdata/** (YAML) y enlazar a `testdata/COMO_FUNCIONAN_LOS_DATOS.md` y a `docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`.
- **Prioridad:** Alta (evita confusión).

---

## 2. Múltiples entornos (dev / qa / prod) ✅ Implementado

- **Qué:** Poder cambiar URL base y credenciales por entorno sin tocar código.
- **Implementación:** Archivos `src/test/resources/env/dev.properties`, `env/qa.properties`, `env/prod.properties` con `app.base.url` y credenciales. `TestConfig` lee `-Denv=dev|qa|prod` y fija `webdriver.base.url`; las Pages usan rutas relativas. Ver [**docs/ENTORNOS.md**](ENTORNOS.md).

---

## 3. Estrategia de tags para filtrar tests

- **Qué:** Tags claros para ejecutar solo un subconjunto de tests (smoke, regresión, wip).
- **Ejemplo en features:** `@smoke`, `@regression`, `@AgregarEmpleado`, `@wip`.
- **Uso:** En local: `./gradlew test -Dcucumber.filter.tags="@smoke"`. En CI: un job que corra solo `@smoke` en cada push y otro que corra `@regression` en horario programado.
- **Prioridad:** Media-alta cuando crezcan los escenarios.

---

## 4. Reintentos en fallos (retry) ✅ Implementado

- **Qué:** Reintentar los escenarios que fallen por inestabilidad (red, tiempo de carga) sin volver a correr toda la suite.
- **Implementación:** Plugin `rerun` de Cucumber en cada runner genera `target/rerun-*.txt` con los escenarios fallidos. `RerunFailedRunner` los vuelve a ejecutar. En CI el paso de retry se activa automáticamente solo si el paso principal falla. Ver [**docs/RETRY.md**](RETRY.md).
- **Prioridad:** Media (útil en CI).

---

## 5. Datos únicos por ejecución (evitar colisiones)

- **Qué:** Si varios jobs o usuarios corren tests a la vez, que username/employeeId no se pisen (ej. `Omar.rincon` ya existe).
- **Cómo:** En testdata o en código, usar un sufijo único por ejecución: timestamp o `UUID.randomUUID().toString().substring(0,8)` en username/employeeId. Opcionalmente guardar ese valor en `ScenarioContext` para reutilizarlo en el mismo escenario.
- **Prioridad:** Media si hay paralelismo o misma base de datos compartida.

---

## 6. Ejecución en paralelo

- **Qué:** Repartir escenarios en varios workers para reducir tiempo total.
- **Cómo:** Gradle (múltiples JVM con `maxParallelForks`) o Cucumber con paralelismo por tag/feature. Cuidado con datos compartidos y limpieza (ScenarioContext/ThreadLocal ya ayudan por hilo).
- **Prioridad:** Media cuando la suite tarde mucho.

---

## 7. Limpieza de datos si el test falla a mitad

- **Qué:** Si el escenario falla después de crear el empleado pero antes de eliminarlo, el empleado queda en el sistema y puede afectar la siguiente ejecución.
- **Cómo:** En `@After` de Hooks (o en un hook que detecte fallo), comprobar si hay un “empleado creado en este escenario” (guardado en ScenarioContext) y, si existe, intentar eliminarlo por API o por UI (opcional, según complejidad).
- **Prioridad:** Media (más importante si no hay limpieza manual o entornos compartidos).

---

## 8. Tests de API para preparar/limpiar datos

- **Qué:** Usar la API de OrangeHRM (si existe) para crear empleado antes del test o borrarlo después, en lugar de depender solo de la UI.
- **Ventaja:** Más rápido y estable para setup/teardown; la UI se prueba solo donde aporta valor.
- **Prioridad:** Baja-media (cuando quieras optimizar tiempo y estabilidad).

---

## 9. SonarCloud (o análisis estático)

- **Qué:** Análisis de código (duplicados, cobertura, bugs potenciales). Ya tienes el bloque comentado en el workflow de GitHub Actions.
- **Cómo:** Descomentar el step de SonarCloud en `.github/workflows/test.yml`, configurar `SONAR_TOKEN` en secrets y, si hace falta, propiedades en `build.gradle`.
- **Prioridad:** Baja (mejora calidad de código a largo plazo).

---

## 10. Docker para ejecutar tests

- **Qué:** Imagen con JDK + Chrome (o Chromium) para ejecutar tests en un contenedor y que todos tengan el mismo entorno.
- **Cómo:** `Dockerfile` que use una imagen con Java y Chrome/Chromium, copie el proyecto y ejecute `./gradlew test`. El workflow de GitHub puede ejecutar ese contenedor en lugar de instalar Chrome a mano.
- **Prioridad:** Baja (útil para homogeneizar CI o ejecuciones locales).

---

## Resumen rápido

| Mejora                         | Prioridad   | Esfuerzo |
|--------------------------------|------------|----------|
| README actualizado (testdata)  | Alta       | Bajo     |
| Múltiples entornos             | Alta*      | Medio    |
| Tags (smoke/regression)        | Media-alta | Bajo     |
| Retry en fallos                | Media*     | Bajo     |
| Datos únicos por ejecución     | Media      | Bajo     |
| Paralelismo                   | Media      | Medio    |
| Limpieza en @After si falla    | Media      | Medio    |
| API para setup/teardown        | Baja-media | Alto     |
| SonarCloud                     | Baja       | Bajo     |
| Docker                         | Baja       | Medio    |

\* Alta si tienes o tendrás dev/qa/prod.

Si quieres, se puede bajar a pasos concretos (por ejemplo “cómo dejar listo múltiples entornos” o “cómo añadir tags y usarlos en CI”).
