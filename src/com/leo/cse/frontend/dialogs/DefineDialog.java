package com.leo.cse.frontend.dialogs;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.SaveEditorPanel;

public class DefineDialog extends BaseDialog {

	public DefineDialog() {
		super("Define Settings", 300, 104);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		FrontUtils.drawString(g, "Current defines:\n" + Defines.get("Meta.Name") + "\nBy:\n"
				+ Defines.get("Meta.Author") + "\nSpecial support:\n" + Defines.getSpecials(), x + 4, y);
		g.drawLine(x, y + height - 34, x + width, y + height - 34);
		g.drawLine(x + width - 150, y + height - 18, x + width - 150, y + height - 34);
		FrontUtils.drawStringCentered(g, "Load defines", x + width / 4, y + height - 36);
		FrontUtils.drawStringCentered(g, "Load default defines", x + width - 150 + width / 4, y + height - 36);
	}

	@Override
	public boolean onClick(int x, int y) {
		if (super.onClick(x, y))
			return true;
		final int wx = getWindowX(), wy = getWindowY(false);
		if (FrontUtils.pointInRectangle(x, y, wx, wy + height - 18, 150, 16)) {
			boolean ret = loadDefines();
			if (ret)
				SaveEditorPanel.panel.addComponents();
			return ret;
		} else if (FrontUtils.pointInRectangle(x, y, wx + width - 150, wy + height - 18, 150, 16)) {
			try {
				Defines.readDefault();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the default defines file:\n" + e.getMessage(),
						"Could not load default defines file!", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean loadDefines() {
		if (SaveEditorPanel.fc == null)
			SaveEditorPanel.fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Defines Files", "properties");
		SaveEditorPanel.fc.setFileFilter(filter);
		SaveEditorPanel.fc.setCurrentDirectory(new File("."));
		int returnVal = SaveEditorPanel.fc.showOpenDialog(Main.window);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			try {
				Defines.read(SaveEditorPanel.fc.getSelectedFile());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the defines file:\n" + e.getMessage(),
						"Could not load defines file!", JOptionPane.ERROR_MESSAGE);
				return false;
			} finally {
				JOptionPane.showMessageDialog(Main.window, "The defines file was loaded successfully.",
						"Defines loaded successfully", JOptionPane.INFORMATION_MESSAGE);
			}
		else
			return false;
		return true;
	}

}
