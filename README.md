# PBS Portal

## Development setup

* Requires Java 14 and Docker
* MiData credentials in `src/main/resources/application-local.properties`
   ```properties
  spring.security.oauth2.client.registration.midata.client-id=MIDATA_CLIENT_ID
  spring.security.oauth2.client.registration.midata.client-secret=MIDATA_CLIENT_SECRET
   ```
* run manually `./gradlew bootRun`
* Open in webbrowser: http://localhost:8080/

### Settings for IntelliJ IDEA

* Install Intellij Lombok plugin
* Enable `Build project automatically` in `Settings > Compiler`
* Open `Registry`, enable `compiler.automake.allow.when.app.running`
* Build using Gradle:
  * Set `Build and Run using` to Gradle in `Build, Execution, Deployment > Build Tools > Gradle`
* Enable `Enable annotation processing` in `Build, Execution, Deployment > Compiler > Annotation Processors`
* Configure Run/Debug Configuration for PortalApplication (available in `.run` folder)
    * Enable launch optimizations
    * Set `On 'Update' action` to `Hot swap classes and update trigger file if failed`
    * Set `On frame deactivation` to `Update classes and resources`
    * Before launch: Add gradle task after Build: `composeUp` from project `PBSPortal`

> **Caution/Black-Magick:** If the initial startup as Spring application does not work in IntelliJ, starting the application executing the gradle task `bootRun` might solve the issue by generation the required things.

## API

### Documentation

* Local: http://localhost:8080/swagger-ui.html

### Create a message in development environment (local)

```
curl -i -X PUT -H 'Content-Type: application/json' -d '{"title":{"de":"Hallo"},"content":{"de":"Inhalt..."}}' -H 'X-Tile-Authorization: DEMO-KEY' http://localhost:8080/api/v1/message
```