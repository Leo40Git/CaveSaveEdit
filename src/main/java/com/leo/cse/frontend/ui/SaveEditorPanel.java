package com.leo.cse.frontend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
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
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
import com.leo.cse.frontend.ui.dialogs.NikuEditDialog;
import com.leo.cse.frontend.ui.dialogs.SettingsDialog;
import com.leo.cse.frontend.ui.panels.FlagsPanel;
import com.leo.cse.frontend.ui.panels.GeneralPanel;
import com.leo.cse.frontend.ui.panels.InventoryPanel;
import com.leo.cse.frontend.ui.panels.Panel;
import com.leo.cse.frontend.ui.panels.PlusPanel;
import com.leo.cse.frontend.ui.panels.VariablesPanel;
import com.leo.cse.frontend.ui.panels.WarpsPanel;

public class SaveEditorPanel extends JPanel implements MouseInputListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 3503710885336468231L;

	public static final int OFFSET_X = 16, OFFSET_Y = 32;

	static class MenuBarItem {
		private String label;
		private String hotkey;
		private BufferedImage icon;
		private Runnable onClick;
		private boolean hover;
		private Supplier<Boolean> enabled;

		public MenuBarItem(String label, String hotkey, BufferedImage icon, Runnable onClick,
				Supplier<Boolean> enabled) {
			this.label = label;
			this.hotkey = hotkey;
			this.icon = icon;
			this.onClick = onClick;
			this.enabled = enabled;
		}

		public MenuBarItem(String label, String hotkey, BufferedImage icon, Runnable onClick) {
			this(label, hotkey, icon, onClick, Main.TRUE_SUPPLIER);
		}

		public MenuBarItem(String label, String hotkey, Runnable onClick, Supplier<Boolean> enabled) {
			this(label, hotkey, null, onClick, enabled);
		}

		public MenuBarItem(String label, String hotkey, Runnable onClick) {
			this(label, hotkey, onClick, Main.TRUE_SUPPLIER);
		}

		public MenuBarItem(String label, BufferedImage icon, Runnable onClick, Supplier<Boolean> enabled) {
			this(label, null, icon, onClick, Main.TRUE_SUPPLIER);
		}

		public MenuBarItem(String label, BufferedImage icon, Runnable onClick) {
			this(label, icon, onClick, Main.TRUE_SUPPLIER);
		}

		public MenuBarItem(String label, Runnable onClick, Supplier<Boolean> enabled) {
			this(label, null, null, onClick, enabled);
		}

		public MenuBarItem(String label, Runnable onClick) {
			this(label, onClick, Main.TRUE_SUPPLIER);
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getHotkey() {
			return hotkey;
		}

		public void setHotkey(String hotkey) {
			this.hotkey = hotkey;
		}

		public BufferedImage getIcon() {
			return icon;
		}

		public void setIcon(BufferedImage icon) {
			this.icon = icon;
		}

		public boolean isHover() {
			return hover;
		}

		public void setHover(boolean hover) {
			this.hover = hover;
		}

		public void onClick() {
			onClick.run();
		}

		public boolean isEnabled() {
			return enabled.get();
		}
	}

	static class MenuBar {
		private String name;
		private List<MenuBarItem> items;
		private String longestItemLabelCache;

		public MenuBar(String name, List<MenuBarItem> items) {
			this.name = name;
			this.items = items;
		}

		public String getName() {
			return name;
		}

		public List<MenuBarItem> getItems() {
			return items;
		}

		public String getLongestItemLabel() {
			if (items == null || items.isEmpty())
				return null;
			if (longestItemLabelCache != null)
				return longestItemLabelCache;
			List<String> labels = new ArrayList<>();
			for (MenuBarItem item : items)
				labels.add(item.getLabel());
			labels.sort((String o1, String o2) -> {
				int length1 = o1.length(), length2 = o2.length();
				if (length1 == length2)
					return 0;
				if (length1 < length2)
					return 1;
				if (length1 > length2)
					return -1;
				return 0;
			});
			longestItemLabelCache = labels.get(0);
			return longestItemLabelCache;
		}
	}

	public static SaveEditorPanel panel;

	public enum EditorTab {
		GENERAL("General", 0),
		INVENTORY("Inventory", 1),
		WARPS("Warps", 2),
		FLAGS("Flags", 3),
		VARIABLES("Variables", 4),
		PLUS_EXCLUSIVE("Cave Story+ Exclusive", 5);

		String label;
		int icon;

		EditorTab(String label, int icon) {
			this.label = label;
			this.icon = icon;
		}

		public String label() {
			return label;
		}

		public int icon() {
			return icon;
		}
	}

	static class EditorPanel {
		private EditorTab tab;
		private Panel panel;

		public EditorPanel(EditorTab tab, Panel panel) {
			this.tab = tab;
			this.panel = panel;
		}

		public EditorTab getTab() {
			return tab;
		}

		public Panel getPanel() {
			return panel;
		}
	}

	private int currentTab;
	private EditorPanel[] tabs;
	private List<MenuBar> menuBars;

	private boolean quitHover;
	private int menubarHover = -1;
	private int tabHover = -1;
	private int currentMenubar = -1;
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
		currentTab = 0;
		dBoxes = new ArrayList<>();
		addComponents();
	}

	public void addComponents() {
		menuBars = new ArrayList<>();
		List<MenuBarItem> mbiFile = new ArrayList<>();
		mbiFile.add(new MenuBarItem("Load Profile", "Ctrl+O", Resources.toolbarIcons[0], () -> {
			loadProfile();
		}));
		mbiFile.add(new MenuBarItem("Unload Profile", () -> {
			if (ProfileManager.isLoaded() && ProfileManager.isModified()) {
				int sel = JOptionPane.showConfirmDialog(Main.window,
						"Are you sure you want to unload the profile?\nUnsaved changes will be lost!",
						"Unsaved changes detected", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (sel == JOptionPane.CANCEL_OPTION)
					return;
			}
			ProfileManager.unload();
		}, () -> {
			return ProfileManager.isLoaded();
		}));
		mbiFile.add(new MenuBarItem("Load Game/Mod", "Ctrl+Shift+O", Resources.toolbarIcons[1], () -> {
			loadExe();
		}));
		mbiFile.add(new MenuBarItem("Unload Game/Mod", () -> {
			ExeData.unload();
		}, () -> {
			return ExeData.isLoaded();
		}));
		Supplier<Boolean> saveEnabled = () -> {
			return ProfileManager.isLoaded();
		};
		mbiFile.add(new MenuBarItem("Save", "Ctrl+S", Resources.toolbarIcons[2], () -> {
			saveProfile();
		}, saveEnabled));
		mbiFile.add(new MenuBarItem("Save As", "Ctrl+Shift+S", Resources.toolbarIcons[3], () -> {
			saveProfileAs();
		}, saveEnabled));
		mbiFile.add(new MenuBarItem("Settings", Resources.toolbarIcons[4], () -> {
			addDialogBox(new SettingsDialog());
		}));
		mbiFile.add(new MenuBarItem("About", Resources.toolbarIcons[5], () -> {
			addDialogBox(new AboutDialog());
		}));
		mbiFile.add(new MenuBarItem("Quit", Resources.toolbarIcons[6], () -> {
			Main.close();
		}));
		menuBars.add(new MenuBar("File", mbiFile));
		List<MenuBarItem> mbiEdit = new ArrayList<>();
		mbiEdit.add(new MenuBarItem("Undo", "Ctrl+Z", () -> {
			ProfileManager.undo();
		}, () -> {
			return ProfileManager.canUndo();
		}));
		mbiEdit.add(new MenuBarItem("Redo", "Ctrl+Y", () -> {
			ProfileManager.redo();
		}, () -> {
			return ProfileManager.canRedo();
		}));
		menuBars.add(new MenuBar("Edit", mbiEdit));
		List<MenuBarItem> mbiTools = new ArrayList<>();
		mbiTools.add(new MenuBarItem("Edit 290.rec", Resources.toolbarIcons[7], () -> {
			addDialogBox(new NikuEditDialog());
		}));
		menuBars.add(new MenuBar("Tools", mbiTools));
		boolean var = MCI.getSpecial("VarHack"), plus = ExeData.isPlusMode();
		tabs = new EditorPanel[(var || plus ? 5 : 4)];
		tabs[0] = new EditorPanel(EditorTab.GENERAL, new GeneralPanel());
		tabs[1] = new EditorPanel(EditorTab.INVENTORY, new InventoryPanel());
		tabs[2] = new EditorPanel(EditorTab.WARPS, new WarpsPanel());
		tabs[3] = new EditorPanel(EditorTab.FLAGS, new FlagsPanel());
		if (var)
			tabs[4] = new EditorPanel(EditorTab.VARIABLES, new VariablesPanel());
		if (plus)
			tabs[4] = new EditorPanel(EditorTab.PLUS_EXCLUSIVE, new PlusPanel());
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
		// window shadow
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, getWidth(), getHeight());
		FrontUtils.drawNineSlice(g2d, Resources.shadow, 0, 0, getWidth(), getHeight());
		// window title
		for (int xx = 16; xx < getWidth() - 16; xx += 3) {
			g.drawImage(Resources.drag, xx, 16, null);
			g.drawImage(Resources.drag, xx, 24, null);
		}
		g.setColor(Color.white);
		g.setFont(Resources.font);
		FrontUtils.drawString(g, Main.window.getTitle(), 18, 16);
		g.drawLine(getWidth() - 33, 17, getWidth() - 33, 32);
		if (quitHover)
			g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		else
			g.setColor(Main.COLOR_BG);
		g.fillRect(getWidth() - 32, 17, 16, 16);
		g.drawImage(Resources.toolbarIcons[6], getWidth() - 32, 17, this);
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		final Rectangle compViewport = new Rectangle(0, 0, winSize2.width, winSize2.height);
		if (surf == null)
			surf = new BufferedImage(winSize2.width, winSize2.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics og = g;
		g = surf.getGraphics();
		g2d = (Graphics2D) g;
		// START RENDER CODE
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, 0, winSize2.width, winSize2.height);
		// menu bar
		g2d.setColor(Main.lineColor);
		g2d.drawLine(0, 0, winSize.width, 0);
		g2d.drawLine(0, 17, winSize.width, 17);
		int nextX = 3, mX = 0;
		g2d.setFont(Resources.font);
		for (int i = 0; i < menuBars.size(); i++) {
			if (i == currentMenubar)
				mX = nextX;
			MenuBar mb = menuBars.get(i);
			String mbName = mb.getName();
			int width = 62;
			if (i == menubarHover) {
				g2d.setColor(
						new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
				g2d.fillRect(nextX - 3, 0, width + 3, 17);
			}
			g2d.setColor(Main.lineColor);
			FrontUtils.drawString(g2d, mbName, nextX, 0);
			nextX += width;
			g2d.drawLine(nextX, 1, nextX, 17);
			nextX += 3;
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
				for (Component comp : tabs[currentTab].getPanel().getComponents())
					comp.render(g2d, compViewport);
			} else {
				g2d.setFont(Resources.fontL);
				g2d.setColor(Main.lineColor);
				FrontUtils.drawStringCentered(g2d, "NO PROFILE LOADED!", winSize2.width / 2, winSize2.height / 2, true,
						false);
			}
		}
		g2d.translate(0, -17);
		// menu bar items
		if (currentMenubar > -1) {
			g2d.setFont(Resources.font);
			MenuBar mb = menuBars.get(currentMenubar);
			List<MenuBarItem> items = mb.getItems();
			mX -= 3;
			if (items != null) {
				final int mWidth = 280;
				int mHeight = items.size() * 22;
				int mY = 17;
				g2d.setColor(Main.COLOR_BG);
				g2d.fillRect(mX, mY, mWidth, mHeight);
				g2d.setColor(Main.lineColor);
				g2d.drawRect(mX, mY, mWidth, mHeight);
				for (MenuBarItem item : items) {
					Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(),
							31);
					boolean enabled = item.isEnabled();
					if (item.isHover() && enabled) {
						g2d.setColor(lc2);
						g2d.fillRect(mX, mY, mWidth, 22);
						g2d.setColor(Main.lineColor);
					}
					if (!enabled) {
						g2d.setColor(lc2);
						FrontUtils.drawCheckeredGrid(g, mX + 1, mY + 1, mWidth - 1, 21);
						g2d.setColor(Main.lineColor);
					}
					BufferedImage icon = item.getIcon();
					if (icon != null)
						g2d.drawImage(icon, mX + 2, mY + 3, this);
					FrontUtils.drawString(g2d, item.getLabel(), mX + 20, mY + 2, !enabled);
					String hotkey = item.getHotkey();
					if (hotkey != null) {
						g2d.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(),
								Main.lineColor.getBlue(), 127));
						g2d.setFont(Resources.fontS);
						FrontUtils.drawStringRight(g2d, hotkey, mX + mWidth - 2, mY + 4, !enabled);
						g2d.setColor(Main.lineColor);
						g2d.setFont(Resources.font);
					}
					mY += 22;
					g2d.drawLine(mX, mY, mX + mWidth, mY);
				}
			}
		}
		// editor tabs
		g2d.setFont(Resources.font);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, winSize2.height - 17, winSize2.width, winSize2.height);
		g2d.setColor(Main.lineColor);
		g2d.drawLine(0, winSize2.height - 18, winSize2.width, winSize2.height - 18);
		g2d.drawLine(0, winSize2.height - 1, winSize2.width, winSize2.height - 1);
		int tn = tabs.length;
		int ti = 0;
		for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
			final EditorTab t = tabs[ti].getTab();
			if (ProfileManager.isLoaded() && ti == currentTab) {
				g2d.setColor(Main.COLOR_BG);
				g2d.drawLine(xx + 1, winSize2.height - 18, xx + winSize2.width / tn, winSize2.height - 18);
				g2d.setColor(Main.lineColor);
			}
			if (ti == tabHover) {
				g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
				g2d.fillRect(xx + 1, winSize2.height - 18, winSize2.width / tn, 17);
			}
			g2d.setColor(Main.lineColor);
			g2d.drawLine(xx, winSize2.height - 17, xx, winSize2.height - 1);
			g2d.drawImage(Resources.editorTabIcons[t.icon()], xx + 1, winSize2.height - 17, null);
			String label = t.label();
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

	private static final FileFilter[] MOD_FILE_FILTERS;

	static {
		MOD_FILE_FILTERS = new FileFilter[2];
		MOD_FILE_FILTERS[0] = new FileNameExtensionFilter("Executables", "exe");
		MOD_FILE_FILTERS[1] = new FileNameExtensionFilter("CS+ stage.tbl", "tbl");
	}

	private void loadExe() {
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, System.getProperty("user.dir")));
		if (!dir.exists())
			dir = new File(System.getProperty("user.dir"));
		File base = null;
		while (base == null || !base.exists()) {
			int returnVal = FrontUtils.openFileChooser("Open game/mod", MOD_FILE_FILTERS, (base == null ? dir : base),
					false, false);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				base = FrontUtils.getSelectedFile();
			else
				return;
			if (!base.exists())
				JOptionPane.showMessageDialog(Main.window,
						"Game/mod base file \"" + base.getName() + "\" does not exist!", "Executable does not exist",
						JOptionPane.ERROR_MESSAGE);
		}
		loading = true;
		Main.window.repaint();
		final File base2 = base;
		SwingUtilities.invokeLater(() -> {
			try {
				ExeData.load(base2);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(Main.window, "An error occured while loading the executable:\n" + e,
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
			ProfileManager.save();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(Main.window, "An error occured while saving the profile file:\n" + e1,
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
				ProfileManager.save(file, ProfileManager.getLoadedSection());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the profile file:\n" + e1,
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
		firstDragEvent = true;
		onQuit = false;
		notDraggingComps = false;
		int px = e.getX(), py = e.getY();
		if (py < OFFSET_Y && px >= getWidth() - 33 && px < getWidth() - 17) {
			Main.close();
			return;
		}
		final Insets i = Main.window.getInsets();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		final Dimension winSize2 = Main.window.getActualSize(false);
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK,
				ctrl = (mod & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK;
		if (!dBoxes.isEmpty()) {
			// dialog box
			Dialog dBox = dBoxes.get(0);
			dBox.onClick(px, py, shift, ctrl);
			if (dBox.wantsToClose())
				dBoxes.remove(0);
		} else if (py <= 17) {
			// menu bar
			Graphics g = surf.getGraphics();
			g.setFont(Resources.font);
			int nextX = 0;
			for (int j = 0; j < menuBars.size(); j++) {
				int width = 65;
				if (FrontUtils.pointInRectangle(px, py, nextX, 0, width, 18)) {
					currentMenubar = j;
					menubarHover = j;
					break;
				}
				nextX += width;
			}
		} else if (currentMenubar != -1) {
			// menu bar items
			Graphics g = surf.getGraphics();
			g.setFont(Resources.font);
			MenuBar mb = null;
			int mX = 0;
			for (int j = 0; j < menuBars.size(); j++) {
				mb = menuBars.get(j);
				if (j == currentMenubar)
					break;
				mX += g.getFontMetrics().stringWidth(mb.getName()) + 5;
			}
			List<MenuBarItem> items = mb.getItems();
			final int mWidth = 280;
			int mY = 17;
			for (MenuBarItem item : items) {
				if (FrontUtils.pointInRectangle(px, py, mX, mY, mWidth, 21)) {
					item.setHover(true);
					if (item.isEnabled())
						item.onClick();
					break;
				}
				mY += 22;
			}
			currentMenubar = -1;
		} else if (py >= winSize2.height - 18 && ProfileManager.isLoaded()) {
			// editor tabs
			int tn = tabs.length;
			int ti = 0;
			for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, winSize2.height - 18, winSize2.width / tn + 1, 17)) {
					currentTab = ti;
					tabHover = ti;
					lastFocus = null;
					break;
				}
				ti++;
			}
		} else if (ProfileManager.isLoaded()) {
			// components
			Component newFocus = null;
			for (Component comp : tabs[currentTab].getPanel().getComponents()) {
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
					comp.updateHover(px, py, true);
					newFocus = comp;
					break;
				}
			}
			lastFocus = newFocus;
		}
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (currentMenubar != -1)
			return;
		final Insets i = Main.window.getInsets();
		int px = e.getX(), py = e.getY();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseWheelEvent.SHIFT_DOWN_MASK) != 0,
				ctrl = (mod & MouseWheelEvent.CTRL_DOWN_MASK) != 0;
		if (!dBoxes.isEmpty()) {
			dBoxes.get(0).onScroll(e.getWheelRotation(), shift, ctrl);
		} else {
			ScrollBar scroll = tabs[currentTab].getPanel().getGlobalScrollbar();
			if (scroll != null)
				scroll.onScroll(e.getWheelRotation(), shift, ctrl);
			else if (ProfileManager.isLoaded())
				for (Component comp : tabs[currentTab].getPanel().getComponents()) {
					if (!(comp instanceof IScrollable))
						continue;
					final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
					if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh))
						((IScrollable) comp).onScroll(e.getWheelRotation(), shift, ctrl);
				}
		}
		repaint();
	}

	private Map<IDraggable, Boolean> lastDragged;
	private boolean draggingWindow = false, notDraggingComps = false, firstDragEvent = false, onQuit = false;

	@Override
	public void mouseDragged(MouseEvent e) {
		boolean isFDE = firstDragEvent;
		firstDragEvent = false;
		if (!dragLeftMouse)
			return;
		int px = e.getX(), py = e.getY();
		if (px >= getWidth() - 33 && !draggingWindow)
			onQuit = true;
		else
			onQuit = false;
		if (lastDragged == null)
			lastDragged = new HashMap<IDraggable, Boolean>();
		if (lastDragged.isEmpty() && !onQuit && (isFDE || draggingWindow)) {
			if (px > 14 && px < getWidth() - 14 && py > 14 && py < OFFSET_Y) {
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
		if (!dBoxes.isEmpty()) {
			notDraggingComps = true;
			mouseMoved(px, py + OFFSET_Y);
			return;
		}
		final Dimension winSize2 = Main.window.getActualSize(false);
		if (py <= 17 || currentMenubar != -1 || py >= winSize2.height - 18) {
			notDraggingComps = true;
			mouseMoved(px, py + OFFSET_Y);
			return;
		}
		if (loading || !ProfileManager.isLoaded() || notDraggingComps)
			return;
		Component newFocus = null;
		for (Component comp : tabs[currentTab].getPanel().getComponents()) {
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
	public void mouseMoved(MouseEvent e) {
		mouseMoved(e.getX(), e.getY());
	}

	private void mouseMoved(int px, int py) {
		if (loading)
			return;
		quitHover = false;
		if (py < OFFSET_Y && px >= getWidth() - 33 && px < getWidth() - 17)
			quitHover = true;
		final Insets i = Main.window.getInsets();
		px -= i.left + OFFSET_X;
		py -= i.top + OFFSET_Y;
		final Dimension winSize2 = Main.window.getActualSize(false);
		menubarHover = -1;
		tabHover = -1;
		boolean somethingInFront = false;
		if (!dBoxes.isEmpty()) {
			somethingInFront = true;
			dBoxes.get(0).updateHover(px, py);
		}
		if (py <= 17) {
			// menu bar
			Graphics g = surf.getGraphics();
			g.setFont(Resources.font);
			int nextX = 0;
			for (int j = 0; j < menuBars.size(); j++) {
				int width = 65;
				if (FrontUtils.pointInRectangle(px, py, nextX, 0, width, 18)) {
					menubarHover = j;
					if (currentMenubar != -1)
						currentMenubar = j;
					break;
				}
				nextX += width;
			}
		}
		if (currentMenubar != -1) {
			// menu bar items
			somethingInFront = true;
			Graphics g = surf.getGraphics();
			g.setFont(Resources.font);
			MenuBar mb = null;
			int mX = 0;
			for (int j = 0; j < menuBars.size(); j++) {
				mb = menuBars.get(j);
				if (j == currentMenubar)
					break;
				mX += g.getFontMetrics().stringWidth(mb.getName()) + 5;
			}
			List<MenuBarItem> items = mb.getItems();
			final int mWidth = 280;
			int mY = 17;
			for (MenuBarItem item : items) {
				if (FrontUtils.pointInRectangle(px, py, mX, mY, mWidth, 21))
					item.setHover(true);
				else
					item.setHover(false);
				mY += 22;
			}
		}
		if (!somethingInFront) {
			if (py >= winSize2.height - 18 && ProfileManager.isLoaded()) {
				// editor tabs
				int tn = tabs.length;
				int ti = 0;
				for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
					if (FrontUtils.pointInRectangle(px, py, xx, winSize2.height - 18, winSize2.width / tn + 1, 17)) {
						tabHover = ti;
					}
					ti++;
				}
			}
			if (ProfileManager.isLoaded()) {
				for (Component comp : tabs[currentTab].getPanel().getComponents()) {
					final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
					boolean hover = false;
					if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
						hover = true;
					}
					comp.updateHover(px, py, hover);
				}
			}
		}
		repaint();

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
		ScrollBar scroll = tabs[currentTab].getPanel().getGlobalScrollbar();
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
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
