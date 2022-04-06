rootProject.name = "opendc"

include(":web-api")
include(":web-ui")
include(":web-ui-quarkus:deployment")
include(":web-ui-quarkus:runtime")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
