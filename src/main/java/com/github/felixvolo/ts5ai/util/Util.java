package com.github.felixvolo.ts5ai.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class Util {
	public static String readFile(String file) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String contents = bufferedReader.lines().collect(Collectors.joining("\n"));
		bufferedReader.close();
		return contents;
	}
	
	public static String readFileFromZip(ZipFile zipFile, String path) throws IOException {
		ZipEntry zipEntry = zipFile.getEntry(path);
		if(zipEntry == null || zipEntry.isDirectory()) {
			throw new IOException("Zip file does not contain expected file \"" + path + "\"");
		}
		InputStream inputStream = zipFile.getInputStream(zipEntry);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String contents = bufferedReader.lines().collect(Collectors.joining("\n"));
		bufferedReader.close();
		inputStreamReader.close();
		return contents;
	}
	
	public static int compareVersions(String current, String target) {
		DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(current);
		DefaultArtifactVersion targetVersion = new DefaultArtifactVersion(target);
		return currentVersion.compareTo(targetVersion);
	}
	
	public static Optional<File> findTeamSpeakInstallDir() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")) {
			File file = new File(System.getenv("ProgramFiles") + "/TeamSpeak/");
			if(file.exists()) {
				return Optional.of(file);
			}
			return Optional.of(new File(System.getenv("LOCALAPPDATA") + "/Programs/TeamSpeak/"));
		} else if(os.contains("mac")) {
			return Optional.of(new File("/Applications/TeamSpeak.app/Contents/Resources/"));
		} else if(os.contains("nux")) {
			File file = new File(System.getenv("user.dir") + "/.local/share/TeamSpeak/");
			if(file.exists()) {
				return Optional.of(file);
			}
			file = new File("/opt/TeamSpeak/");
			if(file.exists()) {
				return Optional.of(file);
			}
			return Optional.of(new File(System.getenv("user.dir") + "/Programs/TeamSpeak/"));
		} else {
			return Optional.empty();
		}
	}
	
	public static void writeFile(String file, String contents) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(contents);
		writer.close();
	}
	
	public static String validate(String input, String regex) throws Exception {
		if(!input.matches(regex)) {
			throw new Exception(input + " does not match pattern " + regex);
		}
		return input;
	}
}
