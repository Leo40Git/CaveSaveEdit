package com.leo.cse.frontend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.ExeData;
import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.IDraggable;
import com.leo.cse.frontend.ui.components.IScrollable;
import com.leo.cse.frontend.ui.dialogs.AboutDialog;
import com.leo.cse.frontend.ui.dialogs.Dialog;
import com.leo.cse.frontend.ui.dialogs.MCIDialog;
import com.leo.cse.frontend.ui.dialogs.SettingsDialog;

public class SaveEditorPanel extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 3503710885336468231L;

	private static final String[] TOOLBAR = new String[] { "Load Profile:Ctrl+O", "Load .exe:Ctrl+Shift+O",
			"MCI Settings", "Save:Ctrl+S", "Save As:Ctrl+Shift+S", "Editor Settings", "About" };

	public static SaveEditorPanel panel;

	private EditorTab.ID currentTab;
	private Map<EditorTab.ID, EditorTab> tabMap;

	private Component lastFocus;
	private Dialog dBox;
	private boolean loading;

	public static boolean sortMapsAlphabetically = Config.getBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, false);
	public static int flagScroll;
	public static boolean hideSystemFlags = Config.getBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, true);
	public static boolean hideUndefinedFlags = Config.getBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, true);

	public Component getLastFocus() {
		return lastFocus;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public SaveEditorPanel() {
		panel = this;
		currentTab = EditorTab.ID.GENERAL;
		addComponents();
	}

	public void addComponents() {
		tabMap = new HashMap<EditorTab.ID, EditorTab>();
		tabMap.put(EditorTab.ID.GENERAL, new GeneralTab());
		tabMap.put(EditorTab.ID.INVENTORY, new InventoryTab());
		tabMap.put(EditorTab.ID.WARPS, new WarpsTab());
		tabMap.put(EditorTab.ID.FLAGS, new FlagsTab());
		if (MCI.getSpecial("VarHack"))
			tabMap.put(EditorTab.ID.VARIABLES, new VariablesTab());
	}

	public void saveSettings() {
		Config.setBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, sortMapsAlphabetically);
		Config.setBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, hideSystemFlags);
		Config.setBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, hideUndefinedFlags);
	}

	public void setDialogBox(Dialog dBox) {
		this.dBox = dBox;
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, 0, winSize2.width, winSize2.height);
		g2d.setColor(Main.lineColor);
		g2d.setFont(Resources.font);
		// toolbar
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, 0, winSize2.width, 17);
		g2d.setColor(Main.lineColor);
		g2d.drawLine(0, 0, winSize.width, 0);
		g2d.drawLine(0, 17, winSize.width, 17);
		int bi = 0;
		for (int xx = -1; xx < winSize.width; xx += winSize.width / TOOLBAR.length + 1) {
			g2d.drawLine(xx, 1, xx, 17);
			g2d.drawImage(Resources.toolbarIcons[bi], xx + 1, 1, null);
			String ts = TOOLBAR[bi];
			if (!ts.contains(":")) {
				FrontUtils.drawString(g2d, ts, xx + 18, 0);
				bi++;
				continue;
			}
			String[] tsp = ts.split(":");
			FrontUtils.drawString(g2d, tsp[0], xx + 18, 0);
			Color oc = g2d.getColor();
			Color faded = new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), 127);
			g2d.setColor(faded);
			g2d.setFont(Resources.fontS);
			FrontUtils.drawString(g2d, tsp[1],
					xx + winSize.width / TOOLBAR.length + 1 - g2d.getFontMetrics().stringWidth(tsp[1]), 3);
			g2d.setColor(oc);
			g2d.setFont(Resources.font);
			bi++;
		}
		// components
		g2d.translate(0, 17);
		if (loading) {
			g2d.setFont(Resources.font);
			g2d.setColor(Main.lineColor);
			final String s = " Loading...";
			final int sw = g2d.getFontMetrics().stringWidth(s);
			final int sh = g2d.getFontMetrics().getHeight();
			for (int yy = 0; yy < winSize.height; yy += sh)
				for (int xx = 0; xx < winSize.width; xx += sw)
					FrontUtils.drawString(g2d, s, xx, yy);
		} else {
			if (Profile.isLoaded()) {
				for (Component comp : tabMap.get(currentTab).getComponents())
					comp.render(g2d);
			} else {
				g2d.setFont(Resources.fontL);
				g2d.setColor(Main.lineColor);
				FrontUtils.drawStringCentered(g2d, "NO PROFILE LOADED!", winSize2.width / 2, winSize2.height / 2, true);
			}
		}
		g2d.translate(0, -17);
		// editor tabs
		g2d.setFont(Resources.font);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, winSize2.height - 17, winSize2.width, winSize2.height);
		g2d.setColor(Main.lineColor);
		g2d.drawLine(0, winSize2.height - 17, winSize2.width, winSize2.height - 17);
		final EditorTab.ID[] tv = EditorTab.ID.values();
		int tn = tv.length;
		if (!MCI.getSpecial("VarHack"))
			tn--;
		int ti = 0;
		for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
			final EditorTab.ID t = tv[ti];
			if (Profile.isLoaded() && t == currentTab) {
				g2d.setColor(Main.COLOR_BG);
				g2d.fillRect(xx + 1, winSize2.height - 17, winSize2.width / tn + 1, 17);
				g2d.setColor(Main.lineColor);
			}
			g2d.drawLine(xx, winSize2.height - 17, xx, winSize2.height - 1);
			g2d.drawImage(Resources.editorTabIcons[ti], xx + 1, winSize2.height - 16, null);
			FrontUtils.drawString(g2d, tabMap.get(t).getLabel(), xx + 18, winSize2.height - 19);
			ti++;
		}
		// dialog box
		if (dBox != null)
			dBox.render(g);
	}

	private void loadProfile() {
		if (Profile.isLoaded() && Profile.isModified()) {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to load a new profile?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		int returnVal = FrontUtils.openFileChooser("Open profile", new FileNameExtensionFilter("Profile Files", "dat"),
				new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir"))), false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Main.loadProfile(FrontUtils.getSelectedFile());
			addComponents();
		}
	}

	private void loadExe() {
		File base = null;
		while (base == null || !base.exists()) {
			int returnVal = FrontUtils.openFileChooser("Open executable",
					new FileNameExtensionFilter("Applications", "exe"),
					(base == null ? new File(System.getProperty("user.dir")) : base), false);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				base = FrontUtils.getSelectedFile();
			else
				return;
			if (!base.exists())
				JOptionPane.showMessageDialog(Main.window, "Executable \"" + base.getName() + "\" does not exist!",
						"Executable does not exist", JOptionPane.ERROR_MESSAGE);
		}
		loading = true;
		Main.window.repaint();
		final File base2 = base;
		SwingUtilities.invokeLater(() -> {
			try {
				ExeData.load(base2);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while loading the executable:\n" + e.getMessage(),
						"Could not load executable!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				addComponents();
				SwingUtilities.invokeLater(() -> {
					loading = false;
					Main.window.repaint();
				});
			}
		});

	}

	private boolean canSave() {
		if (!Profile.isLoaded()) {
			JOptionPane.showMessageDialog(Main.window, "There is no profile to save!\nPlease load a profile.",
					"No profile to save", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private void saveProfile() {
		if (!canSave())
			return;
		try {
			Profile.write();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(Main.window,
					"An error occured while saving the profile file:\n" + e1.getMessage(),
					"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveProfileAs() {
		if (!canSave())
			return;
		int returnVal = FrontUtils.openFileChooser("Save profile", new FileNameExtensionFilter("Profile Files", "dat"),
				new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir"))), true);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = FrontUtils.getSelectedFile();
			if (file.exists()) {
				int confirmVal = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to overwrite this file?", "Overwrite confirmation",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirmVal != JOptionPane.YES_OPTION)
					return;
			}
			try {
				Profile.write(file);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Main.window,
						"An error occured while saving the profile file:\n" + e1.getMessage(),
						"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
				return;
			} finally {
				Config.set(Config.KEY_LAST_PROFIE, file.getAbsolutePath());
			}
		}
	}

	private boolean ignoreReleased = false, ignoreDragged = false;

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		if (ignoreReleased) {
			ignoreReleased = false;
			return;
		}
		final Insets i = Main.window.getInsets();
		final int px = e.getX() - i.left, py = e.getY() - i.top;
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK,
				ctrl = (mod & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK;
		if (py <= 17) {
			// toolbar
			int bi = 0;
			for (int xx = -1; xx < winSize.width; xx += winSize.width / TOOLBAR.length + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, 0, winSize.width / TOOLBAR.length + 1, 17)) {
					switch (bi) {
					case 0: // load profile
						loadProfile();
						break;
					case 1: // load exe
						loadExe();
						break;
					case 2: // load defines
						dBox = new MCIDialog();
						break;
					case 3: // save
						saveProfile();
						break;
					case 4: // save as
						saveProfileAs();
						break;
					case 5: // editor settings
						dBox = new SettingsDialog();
						break;
					case 6: // about
						dBox = new AboutDialog();
						break;
					default:
						System.out.println("no defined behavior for toolbar item " + bi);
						break;
					}
					repaint();
					break;
				}
				bi++;
			}
		} else if (py >= winSize2.height - 17 && Profile.isLoaded()) {
			// editor tabs
			final EditorTab.ID[] tv = EditorTab.ID.values();
			int tn = tv.length;
			if (!MCI.getSpecial("VarHack"))
				tn--;
			int ti = 0;
			for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, winSize2.height - 17, winSize2.width / tn + 1, 16)) {
					currentTab = tv[ti];
					lastFocus = null;
					repaint();
					break;
				}
				ti++;
			}
		} else if (dBox != null) {
			// dialog box
			if (dBox.onClick(px, py))
				dBox = null;
			repaint();
		} else if (Profile.isLoaded()) {
			// components
			Component newFocus = null;
			for (Component comp : tabMap.get(currentTab).getComponents()) {
				final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
				if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
					ignoreReleased = comp.onClick(px, py - 17, shift, ctrl);
					ignoreDragged = ignoreReleased;
					newFocus = comp;
					break;
				}
			}
			lastFocus = newFocus;
			repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (dBox != null)
			return;
		final Insets i = Main.window.getInsets();
		final int px = e.getX() - i.left, py = e.getY() - i.top;
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseWheelEvent.SHIFT_DOWN_MASK) != 0,
				ctrl = (mod & MouseWheelEvent.CTRL_DOWN_MASK) != 0;
		for (Component comp : tabMap.get(currentTab).getComponents()) {
			if (!(comp instanceof IScrollable))
				continue;
			final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh))
				((IScrollable) comp).onScroll(e.getWheelRotation(), shift, ctrl);
		}
		repaint();
	}

	private Map<IDraggable, Boolean> lastDragged;

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dBox != null)
			return;
		if (ignoreDragged) {
			ignoreDragged = false;
			return;
		}
		if (lastDragged == null)
			lastDragged = new HashMap<IDraggable, Boolean>();
		final Insets i = Main.window.getInsets();
		final int px = e.getX() - i.left, py = e.getY() - i.top;
		Component newFocus = null;
		for (Component comp : tabMap.get(currentTab).getComponents()) {
			if (!(comp instanceof IDraggable))
				continue;
			final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
				((IDraggable) comp).onDrag(px, py);
				lastDragged.put((IDraggable) comp, true);
				newFocus = comp;
				repaint();
			} else {
				if (lastDragged.get((IDraggable) comp) != null) {
					((IDraggable) comp).onDragEnd(px, py);
					lastDragged.remove((IDraggable) comp);
					repaint();
				}
			}
		}
		lastFocus = newFocus;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		int mods = e.getModifiersEx();
		boolean shift = (mods & KeyEvent.SHIFT_DOWN_MASK) != 0;
		boolean ctrl = (mods & KeyEvent.CTRL_DOWN_MASK) != 0;
		if (lastFocus == null) {
			if (code == KeyEvent.VK_O) {
				if (ctrl) {
					if (shift)
						loadExe();
					else
						loadProfile();
				}
			}
			if (code == KeyEvent.VK_S) {
				if (ctrl) {
					if (shift)
						saveProfileAs();
					else {
						saveProfile();
						Main.setTitle(Main.window);
					}
				}
			}
		} else {
			lastFocus.onKey(code, shift, ctrl);
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
