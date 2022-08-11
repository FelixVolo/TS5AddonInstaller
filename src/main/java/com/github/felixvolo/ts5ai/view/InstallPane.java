package com.github.felixvolo.ts5ai.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.felixvolo.ts5ai.model.IAddonSource;

@SuppressWarnings("serial")
public class InstallPane extends JPanel implements InstallDirPane {
	private final JLabel installDirLabel = new JLabel("Installation Directory");
	private final JTextField installDirTextField = new JTextField();
	private final JButton selectInstallDirButton = new JButton("...");
	private final JLabel addonLabel = new JLabel("Addon");
	private final JComboBox<IAddonSource> addonComboBox = new JComboBox<>();
	private final JLabel addonFileLabel = new JLabel("Addon File");
	private final JButton selectAddonFileButton = new JButton("...");
	private final JButton installButton = new JButton("Install");
	private final JTextField selectAddonFileTextField = new JTextField();
	
	public InstallPane() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 275, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gridBagLayout);
		
		GridBagConstraints installDirLabelGbc = new GridBagConstraints();
		installDirLabelGbc.insets = new Insets(10, 10, 10, 10);
		installDirLabelGbc.anchor = GridBagConstraints.EAST;
		installDirLabelGbc.gridx = 0;
		installDirLabelGbc.gridy = 0;
		this.add(this.installDirLabel, installDirLabelGbc);
		
		GridBagConstraints installDirTextFieldGbc = new GridBagConstraints();
		installDirTextFieldGbc.insets = new Insets(10, 0, 10, 0);
		installDirTextFieldGbc.fill = GridBagConstraints.HORIZONTAL;
		installDirTextFieldGbc.gridx = 1;
		installDirTextFieldGbc.gridy = 0;
		this.add(this.installDirTextField, installDirTextFieldGbc);
		
		GridBagConstraints selectInstallDirButtonGbc = new GridBagConstraints();
		selectInstallDirButtonGbc.insets = new Insets(10, 0, 10, 10);
		selectInstallDirButtonGbc.fill = GridBagConstraints.HORIZONTAL;
		selectInstallDirButtonGbc.gridx = 2;
		selectInstallDirButtonGbc.gridy = 0;
		this.add(this.selectInstallDirButton, selectInstallDirButtonGbc);
		
		GridBagConstraints addonLabelGbc = new GridBagConstraints();
		addonLabelGbc.anchor = GridBagConstraints.EAST;
		addonLabelGbc.insets = new Insets(0, 10, 10, 10);
		addonLabelGbc.gridx = 0;
		addonLabelGbc.gridy = 1;
		this.add(this.addonLabel, addonLabelGbc);
		
		GridBagConstraints addonComboBoxGbc = new GridBagConstraints();
		addonComboBoxGbc.gridwidth = 2;
		addonComboBoxGbc.insets = new Insets(0, 0, 10, 10);
		addonComboBoxGbc.fill = GridBagConstraints.HORIZONTAL;
		addonComboBoxGbc.gridx = 1;
		addonComboBoxGbc.gridy = 1;
		this.add(this.addonComboBox, addonComboBoxGbc);
		
		GridBagConstraints addonFileLabelGbc = new GridBagConstraints();
		addonFileLabelGbc.anchor = GridBagConstraints.EAST;
		addonFileLabelGbc.insets = new Insets(0, 10, 10, 10);
		addonFileLabelGbc.gridx = 0;
		addonFileLabelGbc.gridy = 2;
		this.add(this.addonFileLabel, addonFileLabelGbc);
		
		this.selectAddonFileTextField.setColumns(1);
		GridBagConstraints selectAddonFileTextFieldGbc = new GridBagConstraints();
		selectAddonFileTextFieldGbc.insets = new Insets(0, 0, 10, 0);
		selectAddonFileTextFieldGbc.fill = GridBagConstraints.HORIZONTAL;
		selectAddonFileTextFieldGbc.gridx = 1;
		selectAddonFileTextFieldGbc.gridy = 2;
		this.add(this.selectAddonFileTextField, selectAddonFileTextFieldGbc);
		
		GridBagConstraints selectAddonFileButtonGbc = new GridBagConstraints();
		selectAddonFileButtonGbc.insets = new Insets(0, 0, 10, 10);
		selectAddonFileButtonGbc.gridx = 2;
		selectAddonFileButtonGbc.gridy = 2;
		this.add(this.selectAddonFileButton, selectAddonFileButtonGbc);
		
		GridBagConstraints installButtonGbc = new GridBagConstraints();
		installButtonGbc.gridwidth = 3;
		installButtonGbc.insets = new Insets(0, 0, 10, 10);
		installButtonGbc.gridx = 0;
		installButtonGbc.gridy = 3;
		this.add(this.installButton, installButtonGbc);
	}
	
	@Override
	public JTextField getInstallDirTextField() {
		return this.installDirTextField;
	}
	
	public JLabel getInstallDirLabel() {
		return this.installDirLabel;
	}
	
	public JButton getSelectInstallDirButton() {
		return this.selectInstallDirButton;
	}
	
	public JLabel getAddonLabel() {
		return this.addonLabel;
	}
	
	public JComboBox<IAddonSource> getAddonComboBox() {
		return this.addonComboBox;
	}
	
	public JLabel getAddonFileLabel() {
		return this.addonFileLabel;
	}
	
	public JButton getSelectAddonFileButton() {
		return this.selectAddonFileButton;
	}
	
	public JButton getInstallButton() {
		return this.installButton;
	}
	
	public JTextField getSelectAddonFileTextField() {
		return this.selectAddonFileTextField;
	}
}
