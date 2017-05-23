package com.leo.cse.frontend.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.DefineBox;
import com.leo.cse.frontend.ui.components.FlagsUI;
import com.leo.cse.frontend.ui.components.IScrollable;
import com.leo.cse.frontend.ui.components.IntegerBox;
import com.leo.cse.frontend.ui.components.ItemBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.LongBox;
import com.leo.cse.frontend.ui.components.PositionPreview;
import com.leo.cse.frontend.ui.components.RadioBoxes;
import com.leo.cse.frontend.ui.components.ShortBox;
import com.leo.cse.frontend.ui.components.WarpBox;
import com.leo.cse.frontend.ui.components.WeaponBox;
import com.leo.cse.frontend.ui.dialogs.AboutDialog;
import com.leo.cse.frontend.ui.dialogs.Dialog;
import com.leo.cse.frontend.ui.dialogs.MCIDialog;

public class SaveEditorPanel extends JPanel implements MouseListener, MouseWheelListener {

	private static final long serialVersionUID = 3503710885336468231L;

	private static final String[] TOOLBAR = new String[] { "Load profile", "MCI settings", "Save", "Change line color",
			"About" };

	public enum EditorTab {
		GENERAL("General"), INVENTORY("Inventory"), WARPS("Warps"), FLAGS("Flags"), VARIABLES("Variables");

		private String label;

		EditorTab(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}
	}

	public static SaveEditorPanel panel;
	public static JFileChooser fc;

	private EditorTab currentTab;
	private Map<EditorTab, List<Component>> compListMap;
	private Dialog dBox;

	private boolean sortMapsAlphabetically = Config.getBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, false);
	private int flagScroll;
	private boolean hideSystemFlags = Config.getBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, true);
	private boolean hideUndefinedFlags = Config.getBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, true);

	public SaveEditorPanel() {
		panel = this;
		currentTab = EditorTab.GENERAL;
		addComponents();
	}

	public void addComponents() {
		compListMap = new HashMap<EditorTab, List<Component>>();
		for (EditorTab key : EditorTab.values())
			compListMap.put(key, new ArrayList<Component>());
		List<Component> cl = null;
		final Dimension winSize = Main.WINDOW_SIZE;
		// player tab
		cl = compListMap.get(EditorTab.GENERAL);
		cl.add(new Label("Map:", 4, 4));
		cl.add(new DefineBox(36, 4, 240, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getMap();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setMap(t);
				return t;
			}
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return sortMapsAlphabetically;
			}
		}, "Map", "map"));
		cl.add(new BooleanBox("Sort alphabetically", 280, 4, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return sortMapsAlphabetically;
			}
		}, new Function<Boolean, Boolean>() {
			@Override
			public Boolean apply(Boolean t) {
				sortMapsAlphabetically = t;
				return t;
			}
		}));
		cl.add(new Label("Song:", 4, 24));
		cl.add(new DefineBox(36, 24, 240, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getSong();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setSong(t);
				return t;
			}
		}, "Song", "song"));
		cl.add(new Label("Position: (", 4, 44));
		cl.add(new ShortBox(54, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getX() / 32);
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setX((short) (t * 32));
				return t;
			}
		}, "X position"));
		cl.add(new Label(",", 118, 44));
		cl.add(new ShortBox(124, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getY() / 32);
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setY((short) (t * 32));
				return t;
			}
		}, "Y position"));
		cl.add(new Label(")", 188, 44));
		cl.add(new Label("Exact Pos: (", 4, 64));
		cl.add(new ShortBox(64, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getX() / (MCI.getSpecial("DoubleRes") ? 1 : 2));
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setX((short) (t * (MCI.getSpecial("DoubleRes") ? 1 : 2)));
				return t;
			}
		}, "exact X position"));
		cl.add(new Label(",", 128, 64));
		cl.add(new ShortBox(134, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getY() / (MCI.getSpecial("DoubleRes") ? 1 : 2));
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setY((short) (t * (MCI.getSpecial("DoubleRes") ? 1 : 2)));
				return t;
			}
		}, "exacct Y position"));
		String tileSize = "16x16";
		if (MCI.getSpecial("DoubleRes"))
			tileSize = "32x32";
		cl.add(new Label(") (1 tile = " + tileSize + "px)", 198, 64));
		cl.add(new Label("Direction:", 4, 84));
		cl.add(new RadioBoxes(54, 84, 120, 2, new String[] { "Left", "Right" }, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return (Profile.getDirection() == 2 ? 1 : 0);
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setDirection((t == 1 ? 2 : 0));
				return t;
			}
		}, false));
		cl.add(new Label("Health:", 4, 104));
		cl.add(new ShortBox(44, 104, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getCurHealth();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setCurHealth(t);
				return t;
			}
		}, "current health"));
		cl.add(new Label("/", 108, 104));
		cl.add(new ShortBox(114, 104, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getMaxHealth();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setMaxHealth(t);
				return t;
			}
		}, "maximum health"));
		cl.add(new Label("Time Played:", 4, 124));
		cl.add(new IntegerBox(68, 124, 120, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getTime();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setTime(t);
				return t;
			}
		}, "time played"));
		cl.add(new Label("(resets at 4294967295)", 192, 124));
		if (!MCI.getSpecial("VarHack") && MCI.getSpecial("MimHack")) {
			cl.add(new Label("<MIM Costume:", 4, 144));
			cl.add(new LongBox(78, 144, 120, 16, new Supplier<Long>() {
				@Override
				public Long get() {
					return Profile.getMimCostume();
				}
			}, new Function<Long, Long>() {
				@Override
				public Long apply(Long t) {
					Profile.setMimCostume(t);
					return t;
				}
			}, "<MIM costume"));
		}
		cl.add(new PositionPreview(winSize.width / 2 - 320, 164, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getMap();
			}
		}));
		// inventory tab
		cl = compListMap.get(EditorTab.INVENTORY);
		final String l = "Selected";
		int xx = 4;
		cl.add(new RadioBoxes(xx + 26, 2, xx + (122 * 7), 7, new String[] { l, l, l, l, l, l, l },
				new Supplier<Integer>() {
					@Override
					public Integer get() {
						return Profile.getCurWeapon();
					}
				}, new Function<Integer, Integer>() {
					@Override
					public Integer apply(Integer t) {
						Profile.setCurWeapon(t);
						return t;
					}
				}, true));
		for (int i = 0; i < 7; i++) {
			final int i2 = i;
			cl.add(new Label("Weapon Slot " + (i + 1) + ":", xx, 6));
			cl.add(new WeaponBox(xx, 22, i));
			cl.add(new Label("Level:", xx, 72));
			cl.add(new IntegerBox(xx, 90, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getLevel();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setLevel(t);
					return t;
				}
			}, "weapon " + (i + 1) + " level"));
			cl.add(new Label("Extra EXP:", xx, 108));
			cl.add(new IntegerBox(xx, 126, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getExp();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setExp(t);
					return t;
				}
			}, "weapon " + (i + 1) + " extra EXP"));
			cl.add(new Label("Ammo:", xx + 10, 144));
			cl.add(new IntegerBox(xx + 10, 162, 110, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getCurrentAmmo();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setCurrentAmmo(t);
					return t;
				}
			}, "weapon " + (i + 1) + " current ammo"));
			cl.add(new Label("/", xx + 2, 180));
			cl.add(new IntegerBox(xx + 10, 180, 110, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getMaxAmmo();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setMaxAmmo(t);
					return t;
				}
			}, "weapon " + (i + 1) + " maximum ammo"));
			xx += 122;
		}
		int itemId = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				cl.add(new ItemBox(j * (winSize.width / 5), 204 + i * 50, winSize.width / 5 - 5, 48, itemId));
				itemId++;
			}
		}
		int equipId = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				final int i2 = equipId;
				cl.add(new BooleanBox("Equip." + equipId, 4 + (winSize.width / 3) * i, 507 + 18 * j,
						new Supplier<Boolean>() {
							@Override
							public Boolean get() {
								return Profile.getEquip(i2);
							}
						}, new Function<Boolean, Boolean>() {
							@Override
							public Boolean apply(Boolean t) {
								Profile.setEquip(i2, t);
								return t;
							}
						}));
				equipId++;
			}
		}
		cl.add(new Label("Whimsical Star Count:", 4 + (winSize.width / 3) * 2, 505));
		cl.add(new ShortBox(4 + (winSize.width / 3) * 2, 521, 120, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getStarCount();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setStarCount(t);
				return t;
			}
		}, "Whimsical Star count"));
		// warps tab
		cl = compListMap.get(EditorTab.WARPS);
		for (int i = 0; i < 7; i++) {
			final int i2 = i, yy = 4 + i * 50;
			cl.add(new Label("Warp Slot " + (i + 1) + ":", 4, yy + 17));
			cl.add(new WarpBox(68, yy, 120, 48, i2));
			cl.add(new Label("Location:", 192, yy + 17));
			cl.add(new DefineBox(242, yy + 17, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWarp(i2).getLocation();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWarp(i2).setLocation(t);
					return t;
				}
			}, "WarpLoc", "warp " + (i + 1) + "'s location"));
		}
		// flags tab
		cl = compListMap.get(EditorTab.FLAGS);
		cl.add(new FlagsUI(new Supplier<Integer>() {
			@Override
			public Integer get() {
				return flagScroll;
			}
		}, (Integer t) -> {
			flagScroll = t;
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return hideSystemFlags;
			}
		}, (Boolean t) -> {
			hideSystemFlags = t;
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return hideUndefinedFlags;
			}
		}, (Boolean t) -> {
			hideUndefinedFlags = t;
		}));
		// variables tab
		if (MCI.getSpecial("VarHack")) {
			cl = compListMap.get(EditorTab.VARIABLES);
			cl.add(new Label("Variables:", 4, 4));
			final int width = winSize.width / 8;
			int varId = 0;
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 8; j++) {
					if (varId < 8 && varId != 6) {
						varId++;
						j--;
						continue;
					}
					if (varId > 123)
						break;
					final int vi2 = varId + 1;
					cl.add(new Label("V" + FrontUtils.padLeft(Integer.toString(varId), "0", 3) + ":", j * width + 2,
							24 + i * 16));
					cl.add(new ShortBox(j * width + 40, 24 + i * 16, width - 44, 16, new Supplier<Short>() {
						@Override
						public Short get() {
							return Profile.getVariable(vi2);
						}
					}, new Function<Short, Short>() {
						@Override
						public Short apply(Short t) {
							Profile.setVariable(vi2, t);
							return t;
						}
					}, "variable " + varId));
					varId++;
				}
			}
			if (MCI.getSpecial("PhysVarHack")) {
				final String[] pvl = { "Max Walk Speed", "Max Fall Speed", "Gravity", "Alt Gravity", "Walk Accel",
						"Jump Control", "Friction", "Jump Force" };
				cl.add(new Label("Physics Variables:", 4, 264));
				varId = 0;
				int label = 0;
				boolean labelWater = false;
				for (int i = 0; i < 4; i += 2) {
					for (int j = 0; j < 8; j++) {
						if (varId > 15)
							break;
						final int vi2 = varId;
						cl.add(new Label(pvl[label] + (labelWater ? " (W)" : "") + ":", j * width + 2, 284 + i * 16));
						cl.add(new ShortBox(j * width + 2, 300 + i * 16, width - 6, 16, new Supplier<Short>() {
							@Override
							public Short get() {
								return Profile.getPhysVariable(vi2);
							}
						}, new Function<Short, Short>() {
							@Override
							public Short apply(Short t) {
								Profile.setPhysVariable(vi2, t);
								return t;
							}
						}, (labelWater ? "water " : "") + pvl[label].toLowerCase()));
						varId++;
						label++;
						if (label > 7) {
							label = 0;
							labelWater = true;
						}
					}
				}
				cl.add(new Label("(W) - Water physics variable", 4, 350));
				cl.add(new BooleanBox("Water doesn't cause splash and trigger air timer", 4, 374,
						new Supplier<Boolean>() {
							@Override
							public Boolean get() {
								return (Profile.getPhysVariable(16) == 1 ? true : false);
							}
						}, new Function<Boolean, Boolean>() {
							@Override
							public Boolean apply(Boolean t) {
								Profile.setPhysVariable(16, (short) (t ? 1 : 0));
								return t;
							}
						}));
			}
		}
	}

	public void saveSettings() {
		Config.setBoolean(Config.KEY_SORT_MAPS_ALPHABETICALLY, sortMapsAlphabetically);
		Config.setBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, hideSystemFlags);
		Config.setBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, hideUndefinedFlags);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, 0, winSize2.width, winSize2.height);
		g2d.setColor(Main.customColor);
		g2d.setFont(Resources.font);
		// toolbar
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, 0, winSize2.width, 17);
		g2d.setColor(Main.customColor);
		g2d.drawLine(0, 0, winSize.width, 0);
		g2d.drawLine(0, 17, winSize.width, 17);
		int bi = 0;
		for (int xx = -1; xx < winSize.width; xx += winSize.width / TOOLBAR.length + 1) {
			g2d.drawLine(xx, 1, xx, 17);
			g2d.drawImage(Resources.toolbarIcons[bi], xx + 1, 1, null);
			FrontUtils.drawString(g2d, TOOLBAR[bi], xx + 18, 0);
			bi++;
		}
		// components
		g2d.translate(0, 17);
		if (Profile.isLoaded()) {
			for (Component comp : compListMap.get(currentTab))
				comp.render(g2d);
		} else {
			g2d.setFont(Resources.fontL);
			g2d.setColor(Main.customColor);
			FrontUtils.drawStringCentered(g2d, "NO PROFILE LOADED!", winSize2.width / 2, winSize2.height / 2, true);
		}
		g2d.translate(0, -17);
		// editor tabs
		g2d.setFont(Resources.font);
		g2d.setColor(Main.COLOR_BG);
		g2d.fillRect(0, winSize2.height - 17, winSize2.width, winSize2.height);
		g2d.setColor(Main.customColor);
		g2d.drawLine(0, winSize2.height - 17, winSize2.width, winSize2.height - 17);
		final EditorTab[] tv = EditorTab.values();
		int tn = tv.length;
		if (!MCI.getSpecial("VarHack"))
			tn--;
		int ti = 0;
		for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
			final EditorTab t = tv[ti];
			if (Profile.isLoaded() && t == currentTab) {
				g2d.setColor(Main.COLOR_BG);
				g2d.fillRect(xx + 1, winSize2.height - 17, winSize2.width / tn + 1, 17);
				g2d.setColor(Main.customColor);
			}
			g2d.drawLine(xx, winSize2.height - 17, xx, winSize2.height - 1);
			g2d.drawImage(Resources.editorTabIcons[ti], xx + 1, winSize2.height - 16, null);
			FrontUtils.drawString(g2d, t.label(), xx + 18, winSize2.height - 20);
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
		if (fc == null)
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Profile Files", "dat");
		fc.setFileFilter(filter);
		File dir = new File(Config.get(Config.KEY_LAST_PROFIE, "."));
		while (!dir.isDirectory()) {
			if (dir.getParentFile() == null)
				return;
			dir = dir.getParentFile();
		}
		fc.setCurrentDirectory(dir);
		int returnVal = fc.showOpenDialog(Main.window);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			Main.loadProfile(fc.getSelectedFile());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
					case 1: // load defines
						dBox = new MCIDialog();
						break;
					case 2: // save
						if (!Profile.isLoaded()) {
							JOptionPane.showMessageDialog(Main.window,
									"There is no profile to save!\nPlease load a profile.", "No profile to save",
									JOptionPane.ERROR_MESSAGE);
							break;
						}
						try {
							Profile.write();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(Main.window,
									"An error occured while saving the profile file:\n" + e1.getMessage(),
									"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
							break;
						} finally {
							JOptionPane.showMessageDialog(Main.window, "The profile file was saved successfully.",
									"Profile saved successfully", JOptionPane.INFORMATION_MESSAGE);
						}
						break;
					case 3: // set color
						Color temp = FrontUtils.showColorChooserDialog(this, "Select new color", Main.customColor, false);
						if (temp != null) {
							Main.customColor = temp;
							Resources.colorImages(Main.customColor);
						}
						break;
					case 4: // about
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
			final EditorTab[] tv = EditorTab.values();
			int tn = tv.length;
			if (!MCI.getSpecial("VarHack"))
				tn--;
			int ti = 0;
			for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, winSize2.height - 17, winSize2.width / tn + 1, 16)) {
					currentTab = tv[ti];
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
			for (Component comp : compListMap.get(currentTab)) {
				final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
				if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
					comp.onClick(px, py - 17, shift, ctrl);
					repaint();
					break;
				}
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		final Insets i = Main.window.getInsets();
		final int px = e.getX() - i.left, py = e.getY() - i.top;
		final int mod = e.getModifiersEx();
		final boolean shift = (mod & MouseWheelEvent.SHIFT_DOWN_MASK) == MouseWheelEvent.SHIFT_DOWN_MASK,
				ctrl = (mod & MouseWheelEvent.CTRL_DOWN_MASK) == MouseWheelEvent.CTRL_DOWN_MASK;
		for (Component comp : compListMap.get(currentTab)) {
			if (!(comp instanceof IScrollable))
				continue;
			final int rx = comp.getX(), ry = comp.getY() + 17, rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(px, py, rx, ry, rw, rh)) {
				((IScrollable) comp).onScroll(e.getWheelRotation(), shift, ctrl);
				repaint();
			}
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

}
