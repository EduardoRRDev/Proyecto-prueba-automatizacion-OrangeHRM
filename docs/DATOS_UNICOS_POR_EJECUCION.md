# Datos únicos por ejecución (evitar colisiones)

Cuando varios jobs de CI o varios usuarios ejecutan tests a la vez contra la misma base de datos (por ejemplo la demo de OrangeHRM), valores fijos como `Omar.rincon` o `123654` pueden provocar errores del tipo **"Username already exists"** o **"Employee Id already exists"**.

Para evitarlo, el proyecto aplica un **sufijo único** a `username` y `employeeId` en cada escenario.

## Cómo funciona

1. **Al iniciar cada escenario** (`Hooks` `@Before`):
   - Se genera un UUID y se guardan en **ScenarioContext**:
     - **`runSuffix`**: 8 caracteres (para `username`).
     - **`runSuffixShort`**: 4 caracteres (para `employeeId`, respetando el límite de 10 caracteres de OrangeHRM).

2. **Al cargar datos** (`TestDataLoader.mergeToEmployee`):
   - Se leen esos sufijos de ScenarioContext.
   - Se concatenan al valor base del YAML:
     - `username` → base + `runSuffix` (ej. `Omar.rincon` + `a1b2c3d4`).
     - `employeeId` → base + `runSuffixShort` (ej. `123654` + `e5f6` → 10 caracteres).

3. **Mismo escenario, mismo sufijo:** Todas las llamadas a `EmployeeTestData.getDefaultEmployee()` o `getEmployee(caseId)` dentro del mismo escenario usan el mismo sufijo, porque se fijó una sola vez al inicio.

4. **Al terminar el escenario** (`Hooks` `@After`): Se limpia `ScenarioContext`, así el siguiente escenario obtiene un UUID nuevo.

## Ventajas

| Situación | Comportamiento |
|-----------|----------------|
| Dos escenarios en la misma ejecución | Sufijos distintos (UUID por escenario). |
| Dos jobs en paralelo en CI | Muy baja probabilidad de colisión (UUID). |
| Mismo escenario, varias llamadas a `getEmployee()` | Mismo sufijo (reutilización en el escenario). |

## Fallback

Si no hay ScenarioContext (por ejemplo, test unitario o ejecución sin Cucumber), `TestDataLoader` usa un sufijo basado en `System.currentTimeMillis() % 10000` para no fallar.

## Dónde está el código

- **Generación del sufijo:** `definitions/Hooks.java` (`@Before`).
- **Uso del sufijo:** `data/TestDataLoader.java` (`mergeToEmployee`).
- **Almacén por escenario:** `context/ScenarioContext.java`.
