# Cómo implementar SonarCloud (análisis estático)

Este documento describe los pasos para habilitar SonarCloud en el proyecto cuando decidas usarlo. Hoy el análisis está **desactivado** (plugin y step en CI comentados).

## Qué aporta SonarCloud

- **Cobertura de código:** Porcentaje de líneas/branches cubiertos por tests.
- **Duplicados:** Código repetido que se puede extraer.
- **Bugs y vulnerabilidades:** Posibles fallos (nulls, recursos no cerrados, etc.).
- **Code smells y deuda técnica:** Complejidad, convenciones, mantenibilidad.

---

## Requisitos previos

- Cuenta en [SonarCloud](https://sonarcloud.io) (gratuita para repos públicos).
- El repositorio de GitHub ya creado y vinculado a tu cuenta/organización en SonarCloud.

---

## Paso 1: Crear el proyecto en SonarCloud

1. Entra en [sonarcloud.io](https://sonarcloud.io) e inicia sesión (con GitHub).
2. **Add new project** → elige tu organización o usuario → selecciona este repositorio.
3. SonarCloud te mostrará un **Project Key** (ej. `eduardorrdev_proyectobaseorange`) y la **Organization** (ej. `eduardorrdev`). Anota ambos; los usarás en `build.gradle`.
4. En la configuración del proyecto, SonarCloud te pedirá un **token**. Puedes generarlo ahí o en el paso 2.

---

## Paso 2: Token y secret en GitHub

1. En SonarCloud: **My Account** → **Security** → **Generate Tokens**. Crea un token con nombre tipo `github-actions` (permiso “Analyze”).
2. Copia el token (solo se muestra una vez).
3. En GitHub: repositorio → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**.
4. Nombre: `SONAR_TOKEN`. Valor: el token copiado. Guarda.

---

## Paso 3: Habilitar el plugin y la configuración en Gradle

En **`build.gradle`**:

1. **Descomentar el plugin** (al inicio del archivo):

```groovy
plugins {
    id 'java'
    id "net.serenity-bdd.serenity-gradle-plugin" version "4.2.1"
    id "org.sonarqube" version "4.4.1.3373"   // <-- descomentar esta línea
}
```

2. **Descomentar el bloque `sonarqube`** y sustituir `projectKey` y `organization` por los de tu proyecto en SonarCloud:

```groovy
sonarqube {
    properties {
        property "sonar.projectKey", "TU_ORGANIZACION_TU_PROYECTO"   // el que te dio SonarCloud
        property "sonar.organization", "TU_ORGANIZACION"            // tu org o usuario
        property "sonar.host.url", "https://sonarcloud.io"
        property "sonar.java.binaries", "build/classes"
        property "sonar.junit.reportPaths", "build/test-results/test"
    }
}
```

- **projectKey:** Suele ser `organizacion_repositorio` (ej. `eduardorrdev_java-serenity-cucumber-pom--main`).
- **organization:** La slug de tu organización o usuario en SonarCloud.

Si el proyecto **solo tiene código de test** (sin `src/main/java`), puedes añadir para que analice las fuentes de test:

```groovy
property "sonar.sources", "src/test/java"
property "sonar.tests", "src/test/java"
property "sonar.java.binaries", "build/classes/java/test"
```

(Opcional) Para **cobertura**, necesitas generar reportes (ej. JaCoCo). Si más adelante añades JaCoCo, agrega algo como:

```groovy
property "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml"
```

---

## Paso 4: Habilitar el step en GitHub Actions

En **`.github/workflows/test.yml`**, descomenta el bloque de SonarCloud:

```yaml
      - name: SonarCloud Scan
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: ./gradlew sonar --no-daemon
```

Colócalo **después** de los steps de tests (y retry) y **antes** de subir los artefactos del reporte Serenity, para que el análisis se ejecute tras los tests y pueda usar los resultados (junit reports).

Si quieres que el análisis **solo se ejecute en la rama principal** (y no en cada PR), puedes envolver el step en:

```yaml
      - name: SonarCloud Scan
        if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/master'
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: ./gradlew sonar --no-daemon
```

---

## Resumen de archivos a tocar

| Archivo | Cambio |
|---------|--------|
| `build.gradle` | Descomentar `id "org.sonarqube"` y el bloque `sonarqube { ... }`; poner tu `projectKey` y `organization`. |
| `.github/workflows/test.yml` | Descomentar el step "SonarCloud Scan" y dejar `SONAR_TOKEN` como secret. |
| GitHub repo | Añadir secret `SONAR_TOKEN` con el token de SonarCloud. |

---

## Comprobar en local (opcional)

Con el plugin y el bloque `sonarqube` ya configurados y `SONAR_TOKEN` definido como variable de entorno:

```bash
export SONAR_TOKEN=tu_token_aqui
./gradlew sonar --no-daemon
```

En Windows (PowerShell):

```powershell
$env:SONAR_TOKEN = "tu_token_aqui"
.\gradlew sonar --no-daemon
```

---

## Referencias

- [SonarCloud – Analysis Parameters](https://docs.sonarcloud.io/advanced-setup/analysis-parameters/)
- [SonarScanner for Gradle](https://docs.sonarcloud.io/analyzing-source-code/languages/java/)
- [GitHub Actions – SonarCloud](https://docs.sonarcloud.io/analysis/scan/sonarscanner-for-github-actions/)
