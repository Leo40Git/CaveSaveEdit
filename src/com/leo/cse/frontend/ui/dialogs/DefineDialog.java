package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;

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
		g.setColor(Color.white);
		g.fillRect(x + 1, y + height - 34, 299, 16);
		g.setColor(Color.black);
		g.drawRect(x, y + height - 35, width, 17);
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
		SaveEditorPanel.fc.setCurrentDirectory(new File(Config.get(Config.KEY_LAST_DEFINES, ".")));
		int returnVal = SaveEditorPanel.fc.showOpenDialog(Main.window);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = SaveEditorPanel.fc.getSelectedFile();
			try {
				Defines.read(file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the defines file:\n" + e.getMessage(),
						"Could not load defines file!", JOptionPane.ERROR_MESSAGE);
				return false;
			} finally {
				Config.set(Config.KEY_LAST_DEFINES, file.getAbsolutePath());
				JOptionPane.showMessageDialog(Main.window, "The defines file was loaded successfully.",
						"Defines loaded successfully", JOptionPane.INFORMATION_MESSAGE);
			}
		} else
			return false;
		return true;
	}

}
