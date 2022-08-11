package com.github.felixvolo.ts5ai.controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;

import com.github.felixvolo.ts5ai.model.InstalledAddon;
import com.github.felixvolo.ts5ai.util.SimpleDocumentListener;
import com.github.felixvolo.ts5ai.util.Util;
import com.github.felixvolo.ts5ai.view.InstallDirPane;
import com.github.felixvolo.ts5ai.view.Window;

public class MainController implements Runnable {
	protected static final int SCHEMA_VERSION = 1;
	protected static final Pattern ADDON_START_REGEX = Pattern.compile("<!-- ADDON_START v(\\d+) ([A-Za-z_0-9]+) ((?:\\d+\\.|\\d)*\\d) \"(.+)\" -->");
	protected static final String ADDON_END_STRING = "<!-- ADDON_END -->";
	protected static final String CLIENT_UI_PATH = "/html/client_ui/";
	protected static final String INDEX_PATH = CLIENT_UI_PATH + "index.html";
	
	private String installDir = Util.findTeamSpeakInstallDir().map(File::toString).orElseGet(() -> {
		JOptionPane.showMessageDialog(null, "Unsupported Operating System", Window.TITLE, JOptionPane.ERROR_MESSAGE);
		System.exit(1);
		return null;
	});
	private final Window window = new Window();
	private final InstallController installController = new InstallController(this);
	private final UninstallController uninstallController = new UninstallController(this);
	
	public MainController() {
		this.window.getTabbedPane().addChangeListener(this::onTabChanged);
		this.installController.getInstallPane().getInstallDirTextField().setText(this.installDir);
		this.installController.getInstallPane().getInstallDirTextField().getDocument().addDocumentListener(new SimpleDocumentListener(this::onInstallDirChanged));
		this.installController.getInstallPane().getSelectInstallDirButton().addActionListener(this::selectInstallDir);
		this.uninstallController.getUninstallPane().getInstallDirTextField().setText(this.installDir);
		this.uninstallController.getUninstallPane().getInstallDirTextField().getDocument().addDocumentListener(new SimpleDocumentListener(this::onInstallDirChanged));
		this.uninstallController.getUninstallPane().getSelectInstallDirButton().addActionListener(this::selectInstallDir);
	}
	
	@Override
	public void run() {
		this.window.showWindow();
	}
	
	private void onTabChanged(ChangeEvent event) {
		this.getInstallDirPane().getInstallDirTextField().setText(this.installDir);
	}
	
	private void onInstallDirChanged(DocumentEvent event) {
		this.installDir = this.getInstallDirPane().getInstallDirTextField().getText();
	}
	
	private void selectInstallDir(ActionEvent event) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(this.window);
		if(result == JFileChooser.APPROVE_OPTION) {
			this.getInstallDirPane().getInstallDirTextField().setText(chooser.getSelectedFile().getAbsolutePath());
		}
		this.installController.updateInstallButton();
	}
	
	private InstallDirPane getInstallDirPane() {
		return (InstallDirPane) this.window.getTabbedPane().getSelectedComponent();
	}
	
	public Window getWindow() {
		return this.window;
	}
	
	public String getInstallDir() {
		return this.installDir;
	}
	
	public static boolean isValidInstallationPath(String path, boolean checkWritePermission) {
		File installDir = new File(path);
		if(!installDir.exists() || !installDir.isDirectory()) {
			JOptionPane.showMessageDialog(null, "The installation directory does not exist", Window.TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		File clientUiDir = new File(installDir, CLIENT_UI_PATH);
		if(!clientUiDir.exists() || !clientUiDir.isDirectory()) {
			JOptionPane.showMessageDialog(null, "Could not detect a valid TeamSpeak 5 installation in \"" + installDir.toString() + "\"", Window.TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(checkWritePermission && !Files.isWritable(installDir.toPath())) {
			JOptionPane.showMessageDialog(null, "Unable to modify current installation:\nMissing write permissions for \"" + installDir.toString() + "\"", Window.TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	public static List<InstalledAddon> findInstalledAddons(String index) {
		List<InstalledAddon> addons = new ArrayList<InstalledAddon>();
		Matcher matcher = MainController.ADDON_START_REGEX.matcher(index);
		while(matcher.find()) {
			try {
				int schemaVersion = Integer.parseInt(matcher.group(1));
				if(schemaVersion == 1) {
					String id = matcher.group(2);
					String version = matcher.group(3);
					String name = matcher.group(4);
					int startIndex = matcher.start();
					int endIndex = index.indexOf(MainController.ADDON_END_STRING, startIndex) + MainController.ADDON_END_STRING.length();
					addons.add(new InstalledAddon(id, name, version, startIndex, endIndex));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return addons;
	}
	
	public static String removeAddonFromIndex(String index, InstalledAddon installedAddon) {
		return index.substring(0, installedAddon.getStartIndex()) + index.substring(installedAddon.getEndIndex());
	}
	
	public static void deleteAddonFolder(File addonPath) {
		File[] files = addonPath.listFiles();
		if(files != null) {
			for(File file : files) {
				deleteAddonFolder(file);
			}
		}
		addonPath.delete();
	}
}
