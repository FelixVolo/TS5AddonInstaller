package com.github.felixvolo.ts5ai;

import com.vdurmont.semver4j.Semver;

import javax.swing.*;

import org.apache.commons.cli.*;
import com.github.felixvolo.ts5ai.cli.CLIController;
import com.github.felixvolo.ts5ai.controller.MainController;
import com.github.felixvolo.ts5ai.view.Window;

public class TS5AddonInstaller {

	private static final Semver VERSION = new Semver("2.2.0");

	public static void main(String... args) {
		Options options = createOptions();
		CommandLine cmd = parseCommandLine(options, args);

		if (cmd.hasOption("help")) {
			printHelp(options);
		} else if (cmd.hasOption("version")) {
			System.out.println("Version: " + VERSION);
		} else if (cmd.hasOption("install")) {
			checkArgListSize(cmd, 1);
			CLIController.install(cmd.getArgList().get(0), cmd.getOptionValue("install"), cmd.hasOption("yes"));
		} else if (cmd.hasOption("uninstall")) {
			checkArgListSize(cmd, 1);
			CLIController.uninstall(cmd.getArgList().get(0), cmd.getOptionValue("uninstall"));
		} else if (cmd.hasOption("list-installed")) {
			checkArgListSize(cmd, 1);
			CLIController.listInstalledAddons(cmd.getArgList().get(0));
		} else if (cmd.hasOption("patch")) {
			checkArgListSize(cmd, 1);
			CLIController.patch(cmd.getArgList().get(0));
		} else {
			Window.setupNativeLook();
			MainController controller = new MainController();
			SwingUtilities.invokeLater(controller);
		}
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(Option.builder("h")
				.longOpt("help")
				.desc("Displays this help text.")
				.build());
		options.addOption(Option.builder("v")
				.longOpt("version")
				.desc("Displays the version of the installer.")
				.build());
		options.addOption(Option.builder("y")
				.longOpt("yes")
				.desc("Automatically answers all prompts with 'yes'.")
				.build());
		options.addOption(Option.builder()
				.longOpt("patch")
				.desc("Patches the TeamSpeak installation to enable addon support.")
				.build());
		options.addOption(Option.builder()
				.longOpt("install")
				.desc("Installs an addon from zip, folder or url, and applies patches to the TeamSpeak installation if required.")
				.hasArg(true)
				.numberOfArgs(1)
				.argName("addon")
				.build());
		options.addOption(Option.builder()
				.longOpt("uninstall")
				.desc("Uninstalls an addon by id or name.")
				.hasArg(true)
				.numberOfArgs(1)
				.argName("addon")
				.build());
		options.addOption(Option.builder()
				.longOpt("list-installed")
				.desc("Displays installed addons.")
				.build());
		return options;
	}

	private static CommandLine parseCommandLine(Options options, String... args) {
		CommandLineParser parser = new DefaultParser();
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error: " + e.getMessage());
			printHelp(options);
			System.exit(1);
		}
		return null;
	}

	private static void checkArgListSize(CommandLine cmd, int size) {
		if (cmd.getArgList().size() != size) {
			throw new IllegalArgumentException("Invalid installation path");
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
		formatter.printHelp("TS5AddonInstaller [OPTIONS] <TEAMSPEAK_PATH>", options);
	}

}
