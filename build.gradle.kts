plugins {
	application
	id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
	mavenCentral()
}

val javaFxVersion = "21.0.1"

dependencies {
	implementation("org.apache.commons:commons-lang3:3.14.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.apache.poi:poi-ooxml:5.2.5")

	// This forces POI's log4j dependency to the latest 2.x version:
	implementation("org.apache.logging.log4j:log4j-core:2.22.0")

	runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:win")
	runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux")
	runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

tasks.jar {
	manifest.attributes["Main-Class"] = "org.virginiaso.score_scope_export.Launcher"
	from(configurations
		.runtimeClasspath
		.get()
		.map { zipTree(it) })
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

javafx {
	version = "$javaFxVersion"
	modules("javafx.controls")
}

application {
	mainClass.set("org.virginiaso.score_scope_export.ExportApplication")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			useJUnitJupiter("5.10.1")
		}
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}
