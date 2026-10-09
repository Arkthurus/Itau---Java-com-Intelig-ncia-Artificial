plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotation"))
    // compileOnly(project(":annotation"))
    implementation(project(":processor"))
    // annotationProcessor(project(":processor"))
}
