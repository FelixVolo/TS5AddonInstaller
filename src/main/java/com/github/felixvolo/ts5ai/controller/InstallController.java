package com.github.felixvolo.ts5ai.controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.felixvolo.ts5ai.model.Addon;
import com.github.felixvolo.ts5ai.model.IAddonSource;
import com.github.felixvolo.ts5ai.model.InstalledAddon;
import com.github.felixvolo.ts5ai.model.LocalAddonSource;
import com.github.felixvolo.ts5ai.model.RemoteAddonSource;
import com.github.felixvolo.ts5ai.util.SimpleDocumentListener;
import com.github.felixvolo.ts5ai.util.Util;
import com.github.felixvolo.ts5ai.view.InstallPane;
import com.github.felixvolo.ts5ai.view.Window;

public class InstallController {
	private static final FileNameExtensionFilter ZIP_FILTER = new FileNameExtensionFilter("Zip Files", "zip");
	private static final String ADDON_DEFINITION_FILE_NAME = "addon.json";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private final MainController mainController;
	private final InstallPane installPane;
	
	public InstallController(MainController mainController) {
		this.mainController = mainController;
		this.installPane = this.mainController.getWindow().getInstallPane();
		this.installPane.getSelectAddonFileButton().addActionListener(this::selectAddonFile);
		this.installPane.getSelectAddonFileTextField().getDocument().addDocumentListener(new SimpleDocumentListener(this::updateInstallButton));
		this.installPane.getAddonComboBox().addActionListener(this::selectAddon);
		try {
			JsonNode node = new ObjectMapper().readTree(Thread.currentThread().getContextClassLoader().getResourceAsStream("addons.json"));
			Iterator<Entry<String, JsonNode>> iterator = node.fields();
			List<RemoteAddonSource> addons = new ArrayList<RemoteAddonSource>();
			while(iterator.hasNext()) {
				Entry<String, JsonNode> entry = iterator.next();
				if(entry.getValue().isTextual()) {
					addons.add(new RemoteAddonSource(entry.getKey(), URI.create(entry.getValue().asText())));
				}
			}
			addons.sort((a, b) -> a.getName().compareTo(b.getName()));
			addons.forEach(this.installPane.getAddonComboBox()::addItem);
		} catch(Exception e) {
			e.printStackTrace();
		}
		this.installPane.getAddonComboBox().addItem(new LocalAddonSource("Zip File"));
		this.installPane.getAddonComboBox().setSelectedIndex(0);
		this.installPane.getInstallButton().addActionListener(this::install);
	}
	
	private void selectAddonFile(ActionEvent event) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(ZIP_FILTER);
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = chooser.showSaveDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			this.installPane.getSelectAddonFileTextField().setText(chooser.getSelectedFile().getAbsolutePath());
		}
		this.updateInstallButton();
	}
	
	private void selectAddon(ActionEvent event) {
		Object selected = this.installPane.getAddonComboBox().getSelectedItem();
		if(selected instanceof RemoteAddonSource) {
			this.installPane.getAddonFileLabel().setEnabled(false);
			this.installPane.getSelectAddonFileTextField().setEnabled(false);
			this.installPane.getSelectAddonFileButton().setEnabled(false);
		} else if(selected instanceof LocalAddonSource) {
			this.installPane.getAddonFileLabel().setEnabled(true);
			this.installPane.getSelectAddonFileTextField().setEnabled(true);
			this.installPane.getSelectAddonFileButton().setEnabled(true);
		}
		this.updateInstallButton();
	}
	
	private void install(ActionEvent event) {
		String installDir = this.mainController.getInstallDir();
		if(!MainController.isValidInstallationPath(installDir, true)) {
			return;
		}
		IAddonSource source = (IAddonSource) this.installPane.getAddonComboBox().getSelectedItem();
		assert source != null;
		Path zipFilePath;
		try {
			zipFilePath = source.setupZipPath(this.installPane.getSelectAddonFileTextField().getText());
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), Window.TITLE, JOptionPane.ERROR_MESSAGE);
			return;
		}
		try(ZipFile zipFile = source.openZipFile(zipFilePath)) {
			ZipEntry addonJson = findAddonJson(zipFile);
			Addon addon;
			try {
				addon = OBJECT_MAPPER.readValue(zipFile.getInputStream(addonJson), Addon.class);
			} catch(Exception e) {
				throw new IOException("Addon file does not contain a valid " + ADDON_DEFINITION_FILE_NAME, e);
			}
			try {
				String addonRoot = addonJson.getName().substring(0, addonJson.getName().length() - ADDON_DEFINITION_FILE_NAME.length());
				String injectionString = Util.readFileFromZip(zipFile, addonRoot + addon.getInject());
				String indexPath = installDir + MainController.INDEX_PATH;
				String index = Util.readFile(indexPath);
				String addonPath = installDir + MainController.CLIENT_UI_PATH + addon.getId() + "/";
				List<InstalledAddon> installedAddons = MainController.findInstalledAddons(index);
				Collections.reverse(installedAddons);
				for(InstalledAddon installedAddon : installedAddons) {
					if(installedAddon.getId().equals(addon.getId())) {
						int comparison = Util.compareVersions(installedAddon.getVersion(), addon.getVersion());
						String message;
						if(comparison < 0) {
							message = "An older version of " + addon.getName() + " is already installed. Do you want to update?\n" + installedAddon.getVersion() + " -> " + addon.getVersion();
						} else if(comparison > 0) {
							message = "A newer version of " + addon.getName() + " is already installed. Do you want to downgrade?\n" + installedAddon.getVersion() + " -> " + addon.getVersion();
						} else {
							message = "The target version of " + addon.getName() + " is already installed. Do you want to install anyway?";
						}
						int dialogResult = JOptionPane.showConfirmDialog(null, message, Window.TITLE, JOptionPane.YES_NO_OPTION);
						if(dialogResult != JOptionPane.YES_OPTION) {
							return;
						}
						index = MainController.removeAddonFromIndex(index, installedAddon);
						MainController.deleteAddonFolder(new File(addonPath));
					}
				}
				String wrappedAddon = wrapAddonInject(injectionString, addon);
				index = addon.getInjectAt().inject(index, wrappedAddon, addon.getInjectionPoint());
				Util.writeFile(indexPath, index);
				Files.createDirectories(Paths.get(addonPath));
				unzipAddonSources(zipFile, addon, addonPath, addonRoot);
				JOptionPane.showMessageDialog(null, addon.getName() + " has successfully been installed!", Window.TITLE, JOptionPane.INFORMATION_MESSAGE);
			} catch(Exception e) {
				throw new IOException("Error during installation (" + e.getMessage() + ")", e);
			}
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), Window.TITLE, JOptionPane.ERROR_MESSAGE);
		} finally {
			source.cleanupZipPath(zipFilePath);
		}
	}
	
	public void updateInstallButton() {
		boolean enabled = !this.mainController.getInstallDir().isEmpty();
		if(this.installPane.getAddonComboBox().getSelectedItem() instanceof LocalAddonSource) {
			enabled = enabled && !this.installPane.getSelectAddonFileTextField().getText().isEmpty();
		}
		this.installPane.getInstallButton().setEnabled(enabled);
	}
	
	public InstallPane getInstallPane() {
		return this.installPane;
	}
	
	private static ZipEntry findAddonJson(ZipFile zipFile) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if(entry.getName().endsWith(ADDON_DEFINITION_FILE_NAME)) {
				return entry;
			}
		}
		throw new IOException("Could not find addon.json inside addon file");
	}
	
	private static void unzipAddonSources(ZipFile zipFile, Addon addon, String installDir, String addonRoot) throws IOException {
		String sourcesDir = addonRoot + addon.getSources();
		ZipEntry sourceRootEntry = zipFile.getEntry(sourcesDir);
		if(sourceRootEntry == null || !sourceRootEntry.isDirectory()) {
			throw new IOException("The sources directory for addon " + addon.getName() + " does not exist");
		}
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if(entry.getName().startsWith(sourcesDir) && !entry.getName().equals(sourcesDir)) {
				Path filePath = Paths.get(installDir + entry.getName().substring(sourcesDir.length()));
				if(entry.isDirectory()) {
					Files.createDirectories(filePath);
				} else {
					Files.copy(zipFile.getInputStream(entry), filePath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}
	
	private static String wrapAddonInject(String injectionString, Addon addon) throws JsonProcessingException {
		return "<!-- ADDON_START v" + MainController.SCHEMA_VERSION + " " + addon.getId() + " " + addon.getVersion() + " \"" + addon.getName() + "\" -->" + injectionString + MainController.ADDON_END_STRING;
	}
}
