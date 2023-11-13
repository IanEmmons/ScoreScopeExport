plugins {
	application
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.apache.commons:commons-lang3:3.13.0")
	//implementation("org.apache.commons:commons-text:1.11.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.apache.poi:poi-ooxml:5.2.4")
	//implementation("org.apache.logging.log4j:log4j-core:2.21.1")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			useJUnitJupiter("5.9.3")
		}
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

application {
	mainClass.set("org.virginiaso.score_scope_export.App")
}

//task getPortalRoster(type: JavaExec) {
//	dependsOn "classes"
//	mainClass = "org.virginiaso.score_scope_export.PortalRetriever"
//	classpath = sourceSets.main.runtimeClasspath
//	systemProperty "portal.password", "$portalPassword"
//}
