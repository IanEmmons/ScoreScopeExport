plugins {
	application
	id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.virginiaso.duosmiator"
version = "1.1.0"
val javaFxVersion = "26-ea+17"
val isIntelArchitecture = false

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.apache.commons:commons-csv:1.14.1")
	implementation("org.apache.commons:commons-lang3:3.20.0")
	implementation("com.google.code.gson:gson:2.13.2")
	implementation("org.apache.poi:poi-ooxml:5.5.0")

	// This forces POI's log4j dependency to the latest 2.x version:
	implementation("org.apache.logging.log4j:log4j-core:2.25.2")

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
		languageVersion.set(JavaLanguageVersion.of(25))
	}
	withJavadocJar()
}

tasks.withType<Javadoc>().configureEach {
	(options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
}

tasks.jar {
	manifest {
		attributes(
			"Implementation-Title" to "Duosmiator",
			"Implementation-Vendor" to "Virginia Science Olympiad (VASO)",
			"Implementation-Version" to project.version,
			"Main-Class" to "org.virginiaso.duosmiator.Launcher"
		)
	}
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
	mainClass.set("org.virginiaso.duosmiator.ExportApplication")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			// org.junit.jupiter:junit-jupiter
			useJUnitJupiter("6.0.1")
		}
	}
}
