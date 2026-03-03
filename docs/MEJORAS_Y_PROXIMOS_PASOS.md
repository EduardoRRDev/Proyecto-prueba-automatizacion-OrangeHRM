# Qué más le haría falta al proyecto (mejoras sugeridas)

El proyecto ya tiene: POM, SOLID, datos en YAML (testdata), reportes Serenity, CI en GitHub Actions, documentación de datos y de cómo agregar secciones. Aquí se listan mejoras opcionales ordenadas por impacto/prioridad.

---

## 1. Actualizar README (datos y estructura) ✅ Implementado

- **Qué:** En el README indicar que los datos están en **testdata/** (YAML) y enlazar a la documentación de datos.
- **Implementación:** README actualizado: datos en `testdata/` (YAML), enlaces a `testdata/COMO_FUNCIONAN_LOS_DATOS.md` y a `docs/DATOS_TESTDATA_EMPLOYEE_EXPLICACION.md`. Ya no se referencia `data/employees.properties` para datos de pruebas.

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

## 5. Datos únicos por ejecución (evitar colisiones) ✅ Implementado

- **Qué:** Si varios jobs o usuarios corren tests a la vez, que username/employeeId no se pisen (ej. `Omar.rincon` ya existe).
- **Implementación:** Sufijo único por escenario en Hooks (`runSuffix` / `runSuffixShort`). `TestDataLoader` aplica el sufijo a username y employeeId desde `ScenarioContext`; el mismo valor se reutiliza en todo el escenario. Ver [**docs/DATOS_UNICOS_POR_EJECUCION.md**](DATOS_UNICOS_POR_EJECUCION.md).

---

## 6. Ejecución en paralelo ✅ Implementado

- **Qué:** Repartir escenarios en varios workers para reducir tiempo total.
- **Implementación:** `maxParallelForks` en Gradle con `-Pparallel=N`. Runners con directorio de salida Serenity por worker (`org.gradle.test.worker`); tarea `mergeSerenityWorkerOutputs` copia el primer worker a `target/site/serenity` para agregar el reporte. Ver [**docs/EJECUCION_PARALELA.md**](EJECUCION_PARALELA.md) y sección en README.

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

| Mejora                         | Estado      | Prioridad   | Esfuerzo |
|--------------------------------|-------------|------------|----------|
| README actualizado (testdata)  | ✅ Hecho    | Alta       | Bajo     |
| Múltiples entornos             | ✅ Hecho    | Alta*      | Medio    |
| Tags (smoke/regression)        | Pendiente   | Media-alta | Bajo     |
| Retry en fallos                | ✅ Hecho    | Media*     | Bajo     |
| Datos únicos por ejecución     | ✅ Hecho    | Media      | Bajo     |
| Paralelismo                    | ✅ Hecho    | Media      | Medio    |
| Limpieza en @After si falla    | Pendiente   | Media      | Medio    |
| API para setup/teardown        | Pendiente   | Baja-media | Alto     |
| SonarCloud                     | Pendiente   | Baja       | Bajo     |
| Docker                         | Pendiente   | Baja       | Medio    |

\* Alta si tienes o tendrás dev/qa/prod.

Si quieres, se puede bajar a pasos concretos (por ejemplo “cómo dejar listo múltiples entornos” o “cómo añadir tags y usarlos en CI”).
