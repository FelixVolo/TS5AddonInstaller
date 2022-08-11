package com.github.felixvolo.ts5ai;

import com.github.felixvolo.ts5ai.controller.MainController;
import com.github.felixvolo.ts5ai.view.Window;

import javax.swing.SwingUtilities;

public class TS5AddonInstaller {
	public static void main(String... args) {
		Window.setupNativeLook();
		MainController controller = new MainController();
		SwingUtilities.invokeLater(controller);
	}
}
