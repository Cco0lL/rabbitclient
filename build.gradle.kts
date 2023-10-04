plugins {
    id("java")
    `maven-publish`
}

group = "ru.ccooll.rabbitclient"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

repositories {
    mavenCentral()
}

dependencies {
    // configs
    // https://mvnrepository.com/artifact/com.typesafe/config
    compileOnly("com.typesafe:config:1.4.2")

    //jakson
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.14.1")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.14.1")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-parameter-names
    compileOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:2.14.1")

    //syntax helpful libs
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.jetbrains:annotations:24.0.1")

    //helpful libs
    implementation("com.google.guava:guava:31.1-jre")

    //rabbit
    implementation("com.rabbitmq:amqp-client:5.16.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks {
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
