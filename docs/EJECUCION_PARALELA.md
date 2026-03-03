# Ejecución en paralelo

Para reducir el tiempo total cuando se ejecutan **todos** los runners (Add Employee + Login), puedes repartir la suite en varios workers con Gradle.

## Cómo activarlo

```bash
# 2 workers en paralelo (recomendado para 2 runners)
./gradlew clean test -Pparallel=2

# 4 workers (útil si añades más runners o muchas features)
./gradlew clean test -Pparallel=4
```

Sin `-Pparallel`, se usa **1 worker** (comportamiento por defecto, igual que antes).

## Qué hace el proyecto

1. **Gradle (`maxParallelForks`):** Con `-Pparallel=N`, se lanzan N JVM; cada una ejecuta uno o más test classes (runners). Con 2 runners y `-Pparallel=2`, AddEmployeeRunner y LoginRunner se ejecutan a la vez.

2. **Directorio de salida por worker:** Cada JVM escribe el reporte Serenity en un directorio distinto (`target/site/serenity-1`, `target/site/serenity-2`, …) para evitar que se pisen. Los runners tienen un `static` block que usa la propiedad `org.gradle.test.worker` que Gradle inyecta en cada proceso.

3. **Merge y aggregate:** Después de `test`, la tarea `mergeSerenityWorkerOutputs` copia el contenido del primer worker a `target/site/serenity`. Luego `aggregate` genera el reporte final desde ese directorio. El reporte principal puede contener solo los resultados de uno de los workers; los demás quedan en `target/site/serenity-2`, etc., por si quieres revisarlos.

4. **Datos y contexto:** ScenarioContext y ThreadLocal son por hilo, así que cada worker tiene su propio contexto y no hay conflicto entre workers. Los datos únicos por escenario (UUID en Hooks) siguen siendo por escenario dentro de cada JVM.

## Cuándo usarlo

- **Suite que tarda mucho** y quieres acortar el tiempo total.
- **Varios runners/features** y misma base de datos: cada worker usa datos con sufijo único (UUID por escenario), lo que reduce colisiones.

## Cuándo no usarlo

- En **CI** suele ejecutarse un solo runner (por ejemplo solo AddEmployeeRunner). Con un solo runner, `-Pparallel=2` no aporta y puede dejar reportes repartidos; es mejor no pasar `-Pparallel` en ese caso.
- Si necesitas **un único reporte** con todos los escenarios de todos los runners, la opción actual (copiar un worker al reporte principal) no une todos; para eso habría que migrar a JUnit 5 y al paralelismo nativo de Cucumber (ver Serenity docs).

## Resumen

| Comando | Workers | Uso típico |
|--------|---------|------------|
| `./gradlew test` | 1 | Por defecto, CI, un solo runner |
| `./gradlew test -Pparallel=2` | 2 | Local, suite completa más rápida |
