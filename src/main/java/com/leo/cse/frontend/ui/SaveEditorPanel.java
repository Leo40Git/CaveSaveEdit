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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.IDraggable;
import com.leo.cse.frontend.ui.components.IScrollable;
import com.leo.cse.frontend.ui.components.ScrollBar;
import com.leo.cse.frontend.ui.dialogs.AboutDialog;
import com.leo.cse.frontend.ui.dialogs.Dialog;
import com.leo.cse.frontend.ui.dialogs.SettingsDialog;
import com.leo.cse.frontend.ui.panels.FlagsPanel;
import com.leo.cse.frontend.ui.panels.GeneralPanel;
import com.leo.cse.frontend.ui.panels.InventoryPanel;
import com.leo.cse.frontend.ui.panels.Panel;
import com.leo.cse.frontend.ui.panels.VariablesPanel;
import com.leo.cse.frontend.ui.panels.WarpsPanel;

public class SaveEditorPanel extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 3503710885336468231L;

	public static final int OFFSET_X = 16, OFFSET_Y = 32;

	private static final String[] TOOLBAR = new String[] { "Load Profile:Ctrl+O", "Load .exe:Ctrl+Shft+O",
			"Save:Ctrl+S", "Save As:Ctrl+Shft+S", "Settings", "About", "Quit" };

	public static SaveEditorPanel panel;

	public enum EditorTab {
		GENERAL("General"), INVENTORY("Inventory"), WARPS("Warps"), FLAGS("Flags"), VARIABLES("Variables");

		String label;

		EditorTab(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}
	}

	private EditorTab currentTab;
	private Map<EditorTab, Panel> tabMap;

	private Component lastFocus;
	private List<Dialog> dBoxes;
	private boolean loading;

	public static boolean sortMapsAlphabetically = Config.getBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, false);
	public static boolean showMapGrid = Config.getBoolean(Config.KEY_SHOW_MAP_GRID, false);
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
		currentTab = EditorTab.GENERAL;
		dBoxes = new ArrayList<>();
		addComponents();
	}

	public void addComponents() {
		tabMap = new HashMap<EditorTab, Panel>();
		tabMap.put(EditorTab.GENERAL, new GeneralPanel());
		tabMap.put(EditorTab.INVENTORY, new InventoryPanel());
		tabMap.put(EditorTab.WARPS, new WarpsPanel());
		tabMap.put(EditorTab.FLAGS, new FlagsPanel());
		if (MCI.getSpecial("VarHack"))
			tabMap.put(EditorTab.VARIABLES, new VariablesPanel());
	}

	public void saveSettings() {
		Config.setBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, sortMapsAlphabetically);
		Config.setBoolean(Config.KEY_SHOW_MAP_GRID, showMapGrid);
		Config.setBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, hideSystemFlags);
		Config.setBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, hideUndefinedFlags);
	}

	public void addDialogBox(Dialog dBox) {
		dBoxes.add(0, dBox);
	}

	private BufferedImage surf;

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		FrontUtils.drawNineSlice(g2d, Resources.shadow, 0, 0, getWidth(), getHeight());
		for (int xx = 16; xx < getWidth() - 16; xx += 3) {
			g.drawImage(Resources.drag, xx, 16, null);
			g.drawImage(Resources.drag, xx, 24, null);
		}
		g.setColor(Color.white);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, Main.window.getTitle(), 18, 16);
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		if (surf == null)
			surf = new BufferedImage(winSize2.width, winSize2.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics og = g;
		g = surf.getGraphics();
		g2d = (Graphics2D) g;
		// START RENDER CODE
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
			boolean c = false;
			final Color c1 = Main.lineColor;
			final Color c2 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 191);
			final String s = " Loading...";
			final int sw = g2d.getFontMetrics().stringWidth(s);
			final int sh = g2d.getFontMetrics().getHeight();
			for (int yy = 0; yy < winSize.height; yy += sh) {
				for (int xx = 0; xx < winSize.width; xx += sw * 2) {
					g2d.setColor((c ? c2 : c1));
					FrontUtils.drawString(g2d, s, xx, yy);
					g2d.setColor((c ? c1 : c2));
					FrontUtils.drawString(g2d, s, xx + sw, yy);
				}
				c = !c;
			}
		} else {
			if (ProfileManager.isLoaded()) {
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
		g2d.drawLine(0, winSize2.height - 18, winSize2.width, winSize2.height - 18);
		g2d.drawLine(0, winSize2.height - 1, winSize2.width, winSize2.height - 1);
		final EditorTab[] tv = EditorTab.values();
		int tn = tv.length;
		if (!MCI.getSpecial("VarHack"))
			tn--;
		int ti = 0;
		for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
			final EditorTab t = tv[ti];
			if (ProfileManager.isLoaded() && t == currentTab) {
				g2d.setColor(Main.COLOR_BG);
				g2d.drawLine(xx + 1, winSize2.height - 18, xx + winSize2.width / tn, winSize2.height - 18);
				g2d.setColor(Main.lineColor);
			}
			g2d.drawLine(xx, winSize2.height - 17, xx, winSize2.height - 1);
			g2d.drawImage(Resources.editorTabIcons[ti], xx + 1, winSize2.height - 17, null);
			String label = "";
			if (tabMap.get(t) != null)
				label = t.label();
			FrontUtils.drawString(g2d, label, xx + 18, winSize2.height - 19);
			ti++;
		}
		g2d.setColor(Main.lineColor);
		g2d.drawLine(0, 0, 0, winSize2.height);
		g2d.drawLine(winSize.width - 1, 0, winSize.width - 1, winSize2.height);
		// dialog boxes
		for (int i = dBoxes.size() - 1; i >= 0; i--)
			dBoxes.get(i).render(g);
		// END RENDER CODE
		g.dispose();
		g = og;
		g.drawImage(surf, OFFSET_X, OFFSET_Y, this);
	}

	private void loadProfile() {
		if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
			int sel = JOptionPane.showConfirmDialog(Main.window,
					"Are you sure you want to load a new profile?\nUnsaved changes will be lost!",
					"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (sel == JOptionPane.CANCEL_OPTION)
				return;
		}
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		int returnVal = FrontUtils.openFileChooser("Open profile", new FileNameExtensionFilter("Profile Files", "dat"),
				dir, false, false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			Main.loadProfile(FrontUtils.getSelectedFile());
			addComponents();
		}
	}

	private static final Set<FileFilter> MOD_FILE_FILTERS = new HashSet<>();

	static {
		MOD_FILE_FILTERS.add(new FileNameExtensionFilter("Executables", "exe"));
		// MOD_FILE_FILTERS.add(new FileNameExtensionFilter("CS+ stage.tbl", "tbl"));
	}

	private void loadExe() {
		if (!ProfileManager.isLoaded()) {
			JOptionPane.showMessageDialog(Main.window, "Please load a profile before loading an executable.",
					"Can't load executable", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File base = null;
		while (base == null || !base.exists()) {
			int returnVal = FrontUtils.openFileChooser("Open executable", MOD_FILE_FILTERS,
					(base == null ? new File(System.getProperty("user.dir")) : base), false, false);
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
		if (!ProfileManager.isLoaded()) {
			JOptionPane.showMessageDialog(Main.window, "There is no profile to save!\nPlease load a profile.",
					"No profile to save", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private void saveProfile() {
		if (!canSave())
			return;
		// force save flag to be on
		try {
			ProfileManager.setField(NormalProfile.FIELD_FLAGS, 431, true);
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
		try {
			ProfileManager.write();
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
				new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir"))), false, true);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = FrontUtils.getSelectedFile();
			if (file.exists()) {
				int confirmVal = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to overwrite this file?", "Overwrite confirmation",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (confirmVal != JOptionPane.YES_OPTION)
					return;
			}
			// force save flag to be on
			try {
				ProfileManager.setField(NormalProfile.FIELD_FLAGS, 431, true);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			try {
				ProfileManager.write(file, ProfileManager.getLoadedSection());
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

	private boolean dragLeftMouse;
	private int dragInitialX, dragInitialY;

	@Override
	public void mousePressed(MouseEvent e) {
		if (loading)
			return;
		if (e.getButton() != MouseEvent.BUTTON1) {
			dragLeftMouse = false;
			return;
		}
		dragLeftMouse = true;
		int px = e.getX(), py = e.getY();
		dragInitialX = px;
		dragInitialY = py;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (loading)
			return;
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		dragLeftMouse = false;
		draggingWindow = false;
		int px = e.getX(), py = e.getY();
		final Insets i = Main.window.getInsets();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK,
				ctrl = (mod & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK;
		if (!dBoxes.isEmpty()) {
			// dialog box
			Dialog dBox = dBoxes.get(0);
			if (dBox.onClick(px, py))
				dBoxes.remove(0);
			repaint();
		} else if (py <= 17) {
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
					case 2: // save
						saveProfile();
						break;
					case 3: // save as
						saveProfileAs();
						break;
					case 4: // settings
						addDialogBox(new SettingsDialog());
						break;
					case 5: // about
						addDialogBox(new AboutDialog());
						break;
					case 6: // quit
						Main.close();
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
		} else if (py >= winSize2.height - 18 && ProfileManager.isLoaded()) {
			// editor tabs
			final EditorTab[] tv = EditorTab.values();
			int tn = tv.length;
			if (!MCI.getSpecial("VarHack"))
				tn--;
			int ti = 0;
			for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, winSize2.height - 18, winSize2.width / tn + 1, 17)) {
					currentTab = tv[ti];
					lastFocus = null;
					repaint();
					break;
				}
				ti++;
			}
		} else if (ProfileManager.isLoaded()) {
			// components
			Component newFocus = null;
			for (Component comp : tabMap.get(currentTab).getComponents()) {
				final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
				if (lastDragged != null) {
					if (comp instanceof IDraggable) {
						IDraggable drag = (IDraggable) comp;
						if (lastDragged.get(drag) != null) {
							drag.onDragEnd(px, py);
							lastDragged.remove(drag);
						}
					}
				}
				if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
					comp.onClick(px, py - 17, shift, ctrl);
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
		if (!dBoxes.isEmpty())
			return;
		final Insets i = Main.window.getInsets();
		int px = e.getX(), py = e.getY();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseWheelEvent.SHIFT_DOWN_MASK) != 0,
				ctrl = (mod & MouseWheelEvent.CTRL_DOWN_MASK) != 0;
		ScrollBar scroll = tabMap.get(currentTab).getGlobalScrollbar();
		if (scroll != null)
			scroll.onScroll(e.getWheelRotation(), shift, ctrl);
		else
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
	private boolean draggingWindow = false;

	@Override
	public void mouseDragged(MouseEvent e) {
		if (loading)
			return;
		if (!dragLeftMouse)
			return;
		if (!dBoxes.isEmpty())
			return;
		if (lastDragged == null)
			lastDragged = new HashMap<IDraggable, Boolean>();
		int px = e.getX(), py = e.getY();
		if (lastDragged.isEmpty()) {
			if (py < OFFSET_Y || draggingWindow) {
				draggingWindow = true;
				int wx = Main.window.getX(), wy = Main.window.getY();
				int moveX = px - dragInitialX;
				int moveY = py - dragInitialY;
				Main.window.setLocation(wx + moveX, wy + moveY);
				return;
			}
		}
		final Insets i = Main.window.getInsets();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		Component newFocus = null;
		for (Component comp : tabMap.get(currentTab).getComponents()) {
			if (!(comp instanceof IDraggable))
				continue;
			IDraggable drag = (IDraggable) comp;
			final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh) || lastDragged.get(drag) != null) {
				drag.onDrag(px, py);
				lastDragged.put(drag, true);
				newFocus = comp;
				repaint();
			}
		}
		lastFocus = newFocus;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!dBoxes.isEmpty())
			return;
		int code = e.getKeyCode();
		int mods = e.getModifiersEx();
		boolean shift = (mods & KeyEvent.SHIFT_DOWN_MASK) != 0;
		boolean ctrl = (mods & KeyEvent.CTRL_DOWN_MASK) != 0;
		if (code == KeyEvent.VK_ESCAPE) {
			Main.close();
		}
		ScrollBar scroll = tabMap.get(currentTab).getGlobalScrollbar();
		if (scroll != null && (code == KeyEvent.VK_HOME || code == KeyEvent.VK_END)) {
			scroll.onKey(code, shift, ctrl);
			repaint();
		} else if (lastFocus == null) {
			switch (code) {
			case KeyEvent.VK_O:
				if (ctrl) {
					if (shift)
						loadExe();
					else
						loadProfile();
				}
				break;
			case KeyEvent.VK_S:
				if (ctrl) {
					if (shift)
						saveProfileAs();
					else {
						saveProfile();
						Main.setTitle(Main.window);
					}
				}
				break;
			case KeyEvent.VK_Z:
				if (ctrl) {
					if (shift)
						ProfileManager.redo();
					else
						ProfileManager.undo();
				}
				break;
			case KeyEvent.VK_Y:
				if (ctrl) {
					ProfileManager.redo();
				}
				break;
			default:
				break;
			}
		} else
			lastFocus.onKey(code, shift, ctrl);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
