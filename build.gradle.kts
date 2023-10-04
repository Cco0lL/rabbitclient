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
    implementation("com.typesafe:config:1.4.2")

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
