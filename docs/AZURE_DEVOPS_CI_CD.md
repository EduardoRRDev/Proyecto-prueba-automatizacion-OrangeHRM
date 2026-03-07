# Usar Azure DevOps para repositorio y CI/CD

Esta guía resume qué hacer si quieres **mover o replicar** el proyecto a Azure DevOps (repositorio + pipelines de CI/CD).

---

## 1. Repositorio en Azure DevOps

### Opción A: Crear repo nuevo y subir el código

1. En [Azure DevOps](https://dev.azure.com) crea una **organización** (si no tienes) y un **proyecto**.
2. Dentro del proyecto: **Repos** → **Files** → **Initialize repository** (o **Import** si quieres importar desde GitHub).
3. Si creaste un repo vacío, en tu máquina añade el remoto y sube:

   ```bash
   git remote add azure https://dev.azure.com/TU_ORG/TU_PROYECTO/_git/NOMBRE_REPO
   git push -u azure main
   ```

   La URL te la da Azure DevOps en la pantalla del repo (**Clone**).

### Opción B: Importar desde GitHub

En Azure DevOps: **Repos** → **Import repository** → elige GitHub, autoriza y selecciona el repo. Azure clona el código y el historial. Luego puedes seguir trabajando en GitHub o cambiar el remoto a Azure.

### Opción C: Mantener ambos (GitHub + Azure)

Puedes tener los dos remotos y hacer push a ambos:

```bash
git remote add azure https://dev.azure.com/...
git push origin main
git push azure main
```

---

## 2. Pipeline de CI/CD (Azure Pipelines)

En Azure DevOps el equivalente al workflow de GitHub Actions es un **Pipeline** definido en YAML.

### Crear el pipeline

1. **Pipelines** → **New pipeline** → elige el repo (Azure Repos Git o GitHub si está conectado).
2. **Azure Repos Git** → selecciona el repositorio.
3. Elige **Starter pipeline** (o “Existing Azure Pipelines YAML”) para empezar desde un `azure-pipelines.yml` que añadirás al repo.
4. Crea el archivo **`azure-pipelines.yml`** en la raíz del proyecto (o en una carpeta; luego lo indicas en la configuración del pipeline). El contenido de ejemplo está más abajo.
5. **Run** para la primera ejecución. A partir de ahí el pipeline se dispara en cada push/PR según lo definas en el YAML.

### Archivo de ejemplo: `azure-pipelines.yml`

Puedes crear este archivo en la raíz del proyecto. Equivale a tu workflow actual de GitHub (JDK 17, Chrome, tests, retry, publicación del reporte Serenity):

```yaml
trigger:
  branches:
    include: [main, master]
  batch: true

pr:
  branches:
    include: [main, master]

pool:
  vmImage: 'ubuntu-latest'

variables:
  headlessMode: 'true'

jobs:
  - job: Test
    timeoutInMinutes: 15
    steps:
      - task: UseJavaVersion@1
        inputs:
          versionSpec: '17'
          distribution: 'temurin'

      - script: |
          sudo apt-get update
          sudo apt-get install -y wget unzip
          wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
          sudo apt-get install -y ./google-chrome-stable_current_amd64.deb
          google-chrome --version
        displayName: 'Install Chrome'

      - task: Cache@2
        inputs:
          key: 'gradle | "$(Agent.OS)" | **/gradle-wrapper.properties'
          path: '$(Pipeline.Workspace)/.gradle'
        displayName: 'Cache Gradle'

      - script: chmod +x gradlew
        displayName: 'Grant execute permission gradlew'

      - script: |
          ./gradlew clean test --tests "co.com.proyecto.automatizacion.runners.AddEmployeeRunner" --no-daemon -Dheadless.mode=$(headlessMode)
        displayName: 'Run tests'
        continueOnError: true
        id: run_tests

      - script: |
          if [ -d target/site/serenity ]; then
            cp -r target/site/serenity target/site/serenity-first-attempt
          fi
        condition: failed()
        displayName: 'Save first attempt report'

      - script: |
          ./gradlew test --tests "co.com.proyecto.automatizacion.runners.RerunFailedRunner" --no-daemon -Dheadless.mode=$(headlessMode)
        condition: failed()
        displayName: 'Retry failed scenarios'

      - task: PublishBuildArtifacts@1
        inputs:
          PathtoPublish: 'target/site/serenity'
          ArtifactName: 'serenity-report'
          publishLocation: 'Container'
        condition: always()
        displayName: 'Upload Serenity report'

      - task: PublishBuildArtifacts@1
        inputs:
          PathtoPublish: 'target/site/serenity-first-attempt'
          ArtifactName: 'serenity-report-first-attempt'
          publishLocation: 'Container'
        condition: failed()
        displayName: 'Upload first attempt report'
```

- **trigger / pr:** Se ejecuta en push y en PR a `main`/`master` (equivalente a tu workflow).
- **pool:** `ubuntu-latest` para un entorno estable.
- **Chrome:** Se instala con `apt`; en agentes Microsoft-hosted suele funcionar. Si usas un agente con otra imagen, puede que tengas que ajustar la instalación.
- **Cache:** Opcional; acelera builds siguientes.
- **Retry:** Solo si el paso de tests falla, igual que en GitHub.
- **Artifacts:** El reporte Serenity se publica como artifact del build (descargable desde la ejecución del pipeline).

### Variables y secretos

- **Variables de pipeline:** En **Pipelines** → tu pipeline → **Edit** → **Variables** (por ejemplo `headlessMode`). Para cosas no sensibles.
- **Secretos:** **Pipelines** → **Library** → **Variable groups**, o **Variables** en el pipeline con el icono de candado. Ahí pondrías, por ejemplo, `SONAR_TOKEN` si añades SonarCloud al pipeline.

---

## 3. Resumen de pasos

| Paso | Acción |
|------|--------|
| 1 | Crear organización y proyecto en Azure DevOps (o usar los existentes). |
| 2 | Crear o importar el repositorio (push desde local, import desde GitHub o doble remoto). |
| 3 | Añadir `azure-pipelines.yml` en la raíz del repo (usa el ejemplo de arriba o el que tengas en `.azure/` si lo creas en una carpeta). |
| 4 | Crear el pipeline apuntando a ese YAML (**Pipelines** → **New pipeline** → repo → **Existing Azure Pipelines YAML file**). |
| 5 | Ejecutar el pipeline y revisar que los tests pasen y el reporte Serenity se publique como artifact. |
| 6 | (Opcional) Configurar variables o variable groups para entornos/secretos. |

---

## 4. Dónde ver resultados y reportes

- **Ejecuciones:** **Pipelines** → tu pipeline → cada **Run**.
- **Reporte Serenity:** En el run → pestaña **Artifacts** → descargar **serenity-report** (y **serenity-report-first-attempt** si hubo retry). Abres el `index.html` localmente.

---

## 5. Mantener GitHub y Azure

Si quieres **seguir usando GitHub** como repo principal y además tener CI en Azure:

- Conecta Azure DevOps con GitHub (**Project settings** → **Service connections** → **New service connection** → **GitHub**) y crea el pipeline desde el repo de GitHub. El código sigue en GitHub; Azure solo ejecuta el pipeline.
- O haz push a ambos remotos (`origin` y `azure`); en Azure el pipeline se dispara al hacer push a `azure`.

Si en el futuro quieres **dejar solo Azure** (repo + CI), cambias el remoto por defecto a Azure y dejas de usar GitHub (o lo mantienes como espejo).
