package com.github.felixvolo.ts5ai.model;

import static com.github.felixvolo.ts5ai.TS5AddonInstaller.VERSION;
import static com.github.felixvolo.ts5ai.util.OS.MAC_OS;
import static com.github.felixvolo.ts5ai.util.Util.OBJECT_MAPPER;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.felixvolo.ts5ai.util.IOUtils;
import com.github.felixvolo.ts5ai.util.OS;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.Semver;

public class Installer {
	private static final int SCHEMA_VERSION = 1;
	private static final Pattern ADDON_START_REGEX = Pattern.compile("<!-- ADDON_START v(\\d+) ([A-Za-z_0-9]+) ((?:\\d+\\.|\\d)*\\d) \"(.+)\" -->");
	private static final String ADDON_END_STRING = "<!-- ADDON_END -->";
	private static final String CLIENT_UI_PATH = "/html/client_ui/";
	private static final String ADDON_DEFINITION_FILE_NAME = "addon.json";
	private static final String INDEX = "index.html";
	
	public static Optional<Addon> install(IAddonSource addonSource, String installDir, ConflictHandler conflictHandler) throws Exception {
		Semver ts5version = VersionIndex.findTeamSpeakVersion(installDir, OS.getOrThrow());
		addonSource.open();
		String addonJsonPath = findAddonJsonPath(addonSource);
		Addon addon;
		try {
			String addonJson = addonSource.read(addonJsonPath);
			addon = OBJECT_MAPPER.readValue(addonJson, Addon.class);
		} catch(Exception e) {
			throw new Exception("Addon does not contain a valid " + ADDON_DEFINITION_FILE_NAME, e);
		}
		if(addon.getTeamSpeakVersion().isPresent()) {
			Requirement requirement = addon.getTeamSpeakVersion().get();
			if(!ts5version.satisfies(requirement)) {
				throw new Exception("Addon " + addon.getName() + " requires TeamSpeak version " + requirement);
			}
		}
		if(addon.getInstallerVersion().isPresent()) {
			Requirement requirement = addon.getInstallerVersion().get();
			if(!VERSION.satisfies(requirement)) {
				throw new Exception("Addon " + addon.getName() + " requires installer version " + requirement);
			}
		}
		Patcher.patch(installDir, ts5version);
		try {
			String addonRoot = addonJsonPath.substring(0, addonJsonPath.length() - ADDON_DEFINITION_FILE_NAME.length());
			String injectionString = addonSource.read(addonRoot + addon.getInject());
			File indexPath = new File(installDir, resolveTeamSpeakResources(OS.getOrThrow()) + INDEX);
			String index = IOUtils.readFile(indexPath);
			List<InstalledAddon> installedAddons = findInstalledAddons(index);
			Collections.reverse(installedAddons);
			for(InstalledAddon installedAddon : installedAddons) {
				if(installedAddon.getId().equals(addon.getId())) {
					int comparison = installedAddon.getVersion().compareTo(addon.getVersion());
					if(!conflictHandler.resolve(addon, installedAddon, comparison)) {
						return Optional.empty();
					}
					index = removeAddonFromIndex(index, installedAddon);
				}
			}
			String packedAddon = Packer.pack(addon, injectionString, addonSource, addonRoot);
			String wrappedAddon = wrapAddonInject(packedAddon, addon);
			index = addon.getInjectAt().inject(index, wrappedAddon, addon.getInjectionPoint());
			IOUtils.writeFile(indexPath, index);
			return Optional.of(addon);
		} catch(Exception e) {
			throw new Exception("Error during installation (" + e.getMessage() + ")", e);
		}
	}
	
	public static void uninstall(InstalledAddon addon, String installDir) throws Exception {
		File indexPath = new File(installDir, resolveTeamSpeakResources(OS.getOrThrow()) + INDEX);
		String index = IOUtils.readFile(indexPath);
		index = removeAddonFromIndex(index, addon);
		IOUtils.writeFile(indexPath, index);
	}
	
	private static String resolveTeamSpeakResources(OS os) {
		if(MAC_OS.equals(os)) {
			return "/Contents/Resources/" + CLIENT_UI_PATH;
		}
		return CLIENT_UI_PATH;
	}
	
	private static String findAddonJsonPath(IAddonSource addonSource) throws IOException {
		Iterator<String> entries = addonSource.entries();
		while(entries.hasNext()) {
			String entry = entries.next();
			if(entry.endsWith(ADDON_DEFINITION_FILE_NAME)) {
				return entry;
			}
		}
		throw new IOException("Could not find addon.json");
	}
	
	public static void validateInstallationPath(String path, boolean checkWritePermission) throws Exception {
		File installDir = new File(path);
		if(!installDir.exists() || !installDir.isDirectory()) {
			throw new IOException("The installation directory does not exist");
		}
		File resourcesDir = new File(installDir, resolveTeamSpeakResources(OS.getOrThrow()));
		if(!resourcesDir.exists() || !resourcesDir.isDirectory()) {
			throw new IOException("Could not detect a valid TeamSpeak 5 installation in \"" + installDir.toString() + "\"");
		}
		if(checkWritePermission && !Files.isWritable(installDir.toPath())) {
			throw new IOException("Unable to modify current installation:\nMissing write permissions for \"" + installDir.toString() + "\"");
		}
	}
	
	private static String wrapAddonInject(String injectionString, Addon addon) throws JsonProcessingException {
		return "<!-- ADDON_START v" + SCHEMA_VERSION + " " + addon.getId() + " " + addon.getVersion() + " \"" + addon.getName() + "\" -->" + injectionString + ADDON_END_STRING;
	}
	
	public static List<InstalledAddon> installedAddons(String installDir) throws Exception {
		File indexPath = new File(installDir, resolveTeamSpeakResources(OS.getOrThrow()) + INDEX);
		String index = IOUtils.readFile(indexPath);
		return findInstalledAddons(index);
	}
	
	private static List<InstalledAddon> findInstalledAddons(String index) throws Exception {
		List<InstalledAddon> addons = new ArrayList<InstalledAddon>();
		Matcher matcher = ADDON_START_REGEX.matcher(index);
		while(matcher.find()) {
			try {
				int schemaVersion = Integer.parseInt(matcher.group(1));
				if(schemaVersion == 1) {
					String id = matcher.group(2);
					Semver version = new Semver(matcher.group(3));
					String name = matcher.group(4);
					int startIndex = matcher.start();
					int endIndex = index.indexOf(ADDON_END_STRING, startIndex) + ADDON_END_STRING.length();
					addons.add(new InstalledAddon(id, name, version, startIndex, endIndex));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return addons;
	}
	
	public static List<Entry<Semver, String>> loadVersions(URL versionUrl) throws IOException {
		URLConnection connection = versionUrl.openConnection();
		InputStream inputStream = connection.getInputStream();
		Map<Semver, String> versionMap = OBJECT_MAPPER.readValue(inputStream, new TypeReference<Map<Semver, String>>() {});
		List<Entry<Semver, String>> versions = new ArrayList<Entry<Semver, String>>(versionMap.entrySet());
		versions.sort(Comparator.comparing(Entry::getKey, Comparator.reverseOrder()));
		return versions;
	}
	
	private static String removeAddonFromIndex(String index, InstalledAddon installedAddon) {
		return index.substring(0, installedAddon.getStartIndex()) + index.substring(installedAddon.getEndIndex());
	}
	
	public interface ConflictHandler {
		boolean resolve(Addon addon, InstalledAddon installedAddon, int compareResult);
	}
}
