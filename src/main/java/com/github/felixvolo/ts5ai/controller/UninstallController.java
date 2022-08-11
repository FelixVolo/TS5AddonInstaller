package com.github.felixvolo.ts5ai.controller;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.github.felixvolo.ts5ai.model.InstalledAddon;
import com.github.felixvolo.ts5ai.util.Util;
import com.github.felixvolo.ts5ai.view.UninstallPane;
import com.github.felixvolo.ts5ai.view.Window;

public class UninstallController {
	private final MainController mainController;
	private final UninstallPane uninstallPane;
	
	public UninstallController(MainController mainController) {
		this.mainController = mainController;
		this.uninstallPane = this.mainController.getWindow().getUninstallPane();
		this.uninstallPane.getUninstallButton().addActionListener(this::uninstall);
		this.uninstallPane.getLoadAddonsButton().addActionListener(this::loadAddons);
	}
	
	private void loadAddons(ActionEvent action) {
		this.loadAddons(true);
	}
	
	private void loadAddons(boolean showEmptyWarningMessage) {
		String installDir = this.mainController.getInstallDir();
		if(!MainController.isValidInstallationPath(installDir, false)) {
			return;
		}
		String indexPath = installDir + MainController.INDEX_PATH;
		try {
			String index = Util.readFile(indexPath);
			List<InstalledAddon> installedAddons = MainController.findInstalledAddons(index);
			installedAddons.sort((a, b) -> a.getName().compareTo(b.getName()));
			this.uninstallPane.getAddonComboBox().removeAllItems();
			for(InstalledAddon installedAddon : installedAddons) {
				this.uninstallPane.getAddonComboBox().addItem(installedAddon);
			}
			this.uninstallPane.getUninstallButton().setEnabled(!installedAddons.isEmpty());
			this.uninstallPane.getAddonComboBox().setEnabled(!installedAddons.isEmpty());
			if(showEmptyWarningMessage && installedAddons.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Could not find any installed addons", Window.TITLE, JOptionPane.WARNING_MESSAGE);
			}
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not find installed addons:\n" + e.getMessage(), Window.TITLE, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void uninstall(ActionEvent action) {
		String installDir = this.mainController.getInstallDir();
		if(!MainController.isValidInstallationPath(installDir, true)) {
			return;
		}
		InstalledAddon addon = (InstalledAddon) this.uninstallPane.getAddonComboBox().getSelectedItem();
		assert addon != null;
		try {
			String indexPath = installDir + MainController.INDEX_PATH;
			String index = Util.readFile(indexPath);
			index = MainController.removeAddonFromIndex(index, addon);
			Util.writeFile(indexPath, index);
			String addonPath = installDir + MainController.CLIENT_UI_PATH + addon.getId();
			MainController.deleteAddonFolder(new File(addonPath));
			JOptionPane.showMessageDialog(null, "The addon " + addon.getName() + " has been successfully uninstalled", Window.TITLE, JOptionPane.INFORMATION_MESSAGE);
			this.loadAddons(false);
		} catch(IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error uninstalling addon " + addon.getName() + ":\n" + e.getMessage(), Window.TITLE, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public UninstallPane getUninstallPane() {
		return this.uninstallPane;
	}
}
