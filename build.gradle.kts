plugins {
	application
	id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.apache.commons:commons-lang3:3.14.0")
	//implementation("org.apache.commons:commons-text:1.11.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.apache.poi:poi-ooxml:5.2.4")

	// This forces POI's log4j dependency to the latest 2.x version:
	implementation("org.apache.logging.log4j:log4j-core:2.22.0")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

application {
	//mainClass.set("org.virginiaso.score_scope_export.App")
	//mainClass.set("org.virginiaso.score_scope_export.gui.GuiApp")
	mainClass.set("org.virginiaso.score_scope_export.gui.ExportApplication")
	//mainClass.set("org.virginiaso.score_scope_export.gui.Survey")
}

javafx {
	version = "21.0.1"
	modules("javafx.controls" /*, "javafx.fxml" */)
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			useJUnitJupiter("5.9.3")
		}
	}
}

task<JavaExec>("createDuosmiumUpload") {
	dependsOn("classes")
	mainClass = "org.virginiaso.score_scope_export.App"
	classpath = java.sourceSets["main"].runtimeClasspath
}

task<JavaExec>("getPortalData") {
	dependsOn("classes")
	mainClass = "org.virginiaso.score_scope_export.PortalRetriever"
	classpath = java.sourceSets["main"].runtimeClasspath
	systemProperty("knack.password", "${project.properties["knackPassword"]}")
}
