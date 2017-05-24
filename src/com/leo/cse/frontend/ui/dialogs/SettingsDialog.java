package com.leo.cse.frontend.ui.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class SettingsDialog extends BaseDialog {

	private static final byte[] TEST_STRING = new byte[] { (byte) 'T', (byte) 'e', (byte) 's', (byte) 't' };

	public SettingsDialog() {
		super("Editor Settings", 300, 44);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		final int x = getWindowX(), y = getWindowY();
		g.setColor(Main.lineColor);
		g.drawRect(x + 4, y + 4, 292, 17);
		FrontUtils.drawStringCentered(g, "Change Line Color", x + 150, y + 4);
		g.drawImage(Resources.toolbarIcons[5], x + 5, y + 5, null);
		FrontUtils.drawString(g, "Encoding:", x + 4, y + 22);
		g.drawRect(x + 52, y + 23, 244, 17);
		FrontUtils.drawString(g, Main.encoding, x + 54, y + 22);
	}

	@Override
	public boolean onClick(int x, int y) {
		if (super.onClick(x, y))
			return true;
		final int wx = getWindowX(), wy = getWindowY();
		if (FrontUtils.pointInRectangle(x, y, wx + 4, wy + 4, 292, 17)) {
			setLineColor();
		} else if (FrontUtils.pointInRectangle(x, y, wx + 52, wy + 23, 244, 17)) {
			String e = null;
			while (e == null) {
				e = JOptionPane.showInputDialog(SaveEditorPanel.panel, "Enter new encoding:", Main.encoding);
				if (e != null)
					try {
						new String(TEST_STRING, e);
					} catch (UnsupportedEncodingException e1) {
						JOptionPane.showMessageDialog(SaveEditorPanel.panel, "Encoding \"" + e + "\" is unsupported!",
								"Unsupported encoding", JOptionPane.ERROR_MESSAGE);
						e = null;
					}
			}
			Main.encoding = e;
		}
		return false;
	}

	private void setLineColor() {
		Color temp = FrontUtils.showColorChooserDialog(SaveEditorPanel.panel, "Select new line color", Main.lineColor,
				false);
		if (temp != null) {
			Main.lineColor = temp;
			Resources.colorImages(Main.lineColor);
		}
	}

}
