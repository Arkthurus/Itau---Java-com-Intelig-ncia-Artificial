plugins {
    id("java")
}

group = "br.com.dio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotation"))
    compileOnly(project(":annotation"))
    implementation(project(":processor"))
    annotationProcessor(project(":processor"))
}
