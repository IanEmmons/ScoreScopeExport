plugins {
	application
	id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
	mavenCentral()
}

val javaFxVersion = "23.0.1"
val isIntelArchitecture = false

dependencies {
	implementation("org.apache.commons:commons-lang3:3.17.0")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("org.apache.poi:poi-ooxml:5.3.0")

	// This forces POI's log4j dependency to the latest 2.x version:
	implementation("org.apache.logging.log4j:log4j-core:2.24.3")

	if (isIntelArchitecture) {
		runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux")
		runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac")
		runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:win")
	} else {
		runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux-aarch64")
		runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac-aarch64")
	}
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
	//platform = "win"
	//platform = "mac"
	//platform = "mac-aarch64"
	//platform = "linux"
	//platform = "linux-aarch64"
	modules("javafx.controls")
}

application {
	mainClass.set("org.virginiaso.score_scope_export.ExportApplication")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			// org.junit.jupiter:junit-jupiter
			useJUnitJupiter("5.11.3")
		}
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}
