import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask

plugins {
	id("application")
	id("eclipse")
	id("idea")
	id("edu.sc.seis.launch4j") version "2.5.3"
	id("com.gradleup.shadow") version "8.3.0"
}

project.version = "2.6.0" // TS5AddonInstaller.VERSION

repositories {
	mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

val main = "com.github.felixvolo.ts5ai.TS5AddonInstaller"

application {
	mainClass = main
}

dependencies {
	implementation("commons-cli:commons-cli:1.6.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.+")
	implementation("com.vdurmont:semver4j:3.1.0")
	implementation("org.dom4j:dom4j:2.1.4")
}

tasks.withType<Jar> {
	manifest {
		attributes["Main-Class"] = main
	}
	
	from("LICENSE")
	
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<DefaultLaunch4jTask> {
	mainClassName = mainClassName
	jarTask = tasks.named("shadowJar").get()
	manifest = "$projectDir/${project.name}.manifest"
	copyright = "MIT License"
	companyName = "FelixVolo"
	fileDescription = project.name
	productName = project.name
	internalName = project.name
	version = project.version.toString()
	textVersion = project.version.toString()
	copyConfigurable = emptyList<Any>()
}

tasks.withType<ShadowJar> {
	configurations = listOf(project.configurations.runtimeClasspath.get())
	from(sourceSets.main.get().output)
	from("LICENSE")
	exclude("META-INF/**")
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(JavaLanguageVersion.of(8).asInt())
}
