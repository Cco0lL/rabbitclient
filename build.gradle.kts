plugins {
    id("java")
    `maven-publish`
}

group = "ru.ccooll.rabbitclient"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.rabbitmq:amqp-client:5.16.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
    compileJava {
        options.compilerArgs.add("--enable-preview")
    }
    getByName<Test>("test") {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.ccooll"
            artifactId = "rabbitclient"
            version = "1.0"
            from(components["java"])
        }
    }
}
