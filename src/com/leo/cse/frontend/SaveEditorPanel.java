package com.leo.cse.frontend;

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
import com.leo.cse.frontend.components.BooleanBox;
import com.leo.cse.frontend.components.Component;
import com.leo.cse.frontend.components.DefineBox;
import com.leo.cse.frontend.components.FlagsUI;
import com.leo.cse.frontend.components.IScrollable;
import com.leo.cse.frontend.components.IntegerBox;
import com.leo.cse.frontend.components.Label;
import com.leo.cse.frontend.components.LongBox;
import com.leo.cse.frontend.components.RadioBoxes;
import com.leo.cse.frontend.components.ShortBox;
import com.leo.cse.frontend.dialogs.AboutDialog;
import com.leo.cse.frontend.dialogs.DefineDialog;
import com.leo.cse.frontend.dialogs.Dialog;

public class SaveEditorPanel extends JPanel implements MouseListener, MouseWheelListener {

	private static final long serialVersionUID = 3503710885336468231L;

	private static final boolean DEBUG = false;
	private static final String[] TOOLBAR = new String[] { "Load profile", "Defines settings", "Save", "About" };

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

	private int flagScroll;
	private boolean flagHideUndefined = true;
	private boolean flagHideSystem = true;

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
		}, "Map"));
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
		}, "Song"));
		cl.add(new Label("Position: (", 4, 44));
		cl.add(new ShortBox(54, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getX() / 16);
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setX((short) (t * 16));
				return t;
			}
		}));
		cl.add(new Label(",", 118, 44));
		cl.add(new ShortBox(124, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getY() / 16);
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setY((short) (t * 16));
				return t;
			}
		}));
		cl.add(new Label(")", 188, 44));
		cl.add(new Label("Exact Pos: (", 4, 64));
		cl.add(new ShortBox(64, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getX();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setX(t);
				return t;
			}
		}));
		cl.add(new Label(",", 128, 64));
		cl.add(new ShortBox(134, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getY();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setY(t);
				return t;
			}
		}));
		cl.add(new Label(") (1 tile = 16x16px)", 198, 64));
		cl.add(new Label("Direction:", 4, 84));
		cl.add(new DefineBox(54, 84, 120, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getDirection();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setDirection(t);
				return t;
			}
		}, "Direction"));
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
		}));
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
		}));
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
		}));
		cl.add(new Label("(resets at 4294967295)", 192, 124));
		if (Defines.getSpecial("MimHack")) {
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
			}));
		}
		// inventory tab
		cl = compListMap.get(EditorTab.INVENTORY);
		int xx = 4;
		cl.add(new RadioBoxes(xx + 50, 2, xx + (122 * 7), 7, new Supplier<Integer>() {
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
		}));
		for (int i = 0; i < 7; i++) {
			final int i2 = i;
			cl.add(new Label("Selected:", xx, -4));
			cl.add(new Label("Weapon Slot " + (i + 1) + ":", xx, 6));
			cl.add(new DefineBox(xx, 22, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getId();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setId(t);
					return t;
				}
			}, "Weapon"));
			cl.add(new Label("Level:", xx, 38));
			cl.add(new IntegerBox(xx, 54, 120, 16, new Supplier<Integer>() {
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
			}));
			cl.add(new Label("Extra EXP:", xx, 70));
			cl.add(new IntegerBox(xx, 86, 120, 16, new Supplier<Integer>() {
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
			}));
			cl.add(new Label("Ammo:", xx + 10, 102));
			cl.add(new IntegerBox(xx + 10, 118, 110, 16, new Supplier<Integer>() {
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
			}));
			cl.add(new Label("/", xx + 2, 134));
			cl.add(new IntegerBox(xx + 10, 134, 110, 16, new Supplier<Integer>() {
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
			}));
			xx += 122;
		}
		int itemId = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				final int ii2 = itemId;
				cl.add(new DefineBox(j * (winSize.width / 5), 166 + i * 16, winSize.width / 5 - 5, 16,
						new Supplier<Integer>() {
							@Override
							public Integer get() {
								return Profile.getItem(ii2);
							}
						}, new Function<Integer, Integer>() {
							@Override
							public Integer apply(Integer t) {
								Profile.setItem(ii2, t);
								return t;
							}
						}, "Item"));
				itemId++;
			}
		}
		int equipId = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				final int ei2 = equipId;
				cl.add(new BooleanBox("Equip." + equipId, 4 + (winSize.width / 4) * i, 278 + 18 * j,
						new Supplier<Boolean>() {
							@Override
							public Boolean get() {
								return Profile.getEquip(ei2);
							}
						}, new Function<Boolean, Boolean>() {
							@Override
							public Boolean apply(Boolean t) {
								Profile.setEquip(ei2, t);
								return t;
							}
						}));
				equipId++;
			}
		}
		cl.add(new Label("Whimsical Star Count:", 4 + winSize.width / 2, 278));
		cl.add(new ShortBox(4 + winSize.width / 2, 294, 120, 16, new Supplier<Short>() {
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
		}));
		// warps tab
		cl = compListMap.get(EditorTab.WARPS);
		for (int i = 0; i < 7; i++) {
			final int i2 = i, yy = 4 + i * 20;
			cl.add(new Label("Warp Slot " + (i + 1) + ":", 4, yy));
			cl.add(new DefineBox(68, yy, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getTeleporter(i2).getId();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getTeleporter(i2).setId(t);
					return t;
				}
			}, "Warp"));
			cl.add(new Label("Location:", 192, yy));
			cl.add(new DefineBox(242, yy, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getTeleporter(i2).getLocation();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getTeleporter(i2).setLocation(t);
					return t;
				}
			}, "WarpLoc", "Warp Location"));
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
				return flagHideUndefined;
			}
		}, (Boolean t) -> {
			flagHideUndefined = t;
		}, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return flagHideSystem;
			}
		}, (Boolean t) -> {
			flagHideSystem = t;
		}));
		// variables tab
		if (Defines.getSpecial("VarHack")) {
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
					}));
					varId++;
				}
			}
			if (Defines.getSpecial("PhysVarHack")) {
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
						}));
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

	@Override
	protected void paintComponent(Graphics g) {
		final Dimension winSize = Main.window.getActualSize();
		final Dimension winSize2 = Main.window.getActualSize(false);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.clearRect(0, 0, winSize2.width, winSize2.height);
		g2d.setColor(Color.black);
		g2d.setFont(Resources.font);
		// toolbar
		g2d.drawLine(0, 0, winSize.width, 0);
		g2d.drawLine(0, 17, winSize.width, 17);
		int bi = 0;
		for (int xx = -1; xx < winSize.width; xx += winSize.width / TOOLBAR.length + 1) {
			if (bi == 2 && !Profile.isLoaded()) {
				g2d.setColor(new Color(0, 0, 0, 0.5f));
				g2d.fillRect(xx, 1, winSize.width / TOOLBAR.length + 1, 17);
				g2d.setColor(Color.black);
			}
			g2d.drawLine(xx, 1, xx, 17);
			g2d.drawImage(Resources.toolbarIcons[bi], xx + 1, 1, null);
			FrontUtils.drawString(g2d, TOOLBAR[bi], xx + 18, 0);
			bi++;
		}
		// components
		g2d.translate(0, 17);
		if (Profile.isLoaded()) {
			for (Component comp : compListMap.get(currentTab)) {
				comp.render(g2d);
				if (DEBUG) {
					Color oc = g2d.getColor();
					g2d.setColor(Color.red);
					g2d.drawRect(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
					g2d.setColor(oc);
				}
			}
		} else {
			g2d.setFont(Resources.fontL);
			g2d.setColor(Color.black);
			FrontUtils.drawStringCentered(g2d, "NO PROFILE LOADED!", winSize2.width / 2, winSize2.height / 2);
			g2d.setFont(Resources.font);
			g2d.setColor(new Color(0, 0, 0, 0.5f));
			g2d.fillRect(0, 0, winSize2.width, winSize2.height);
		}
		g2d.translate(0, -17);
		// dialog box
		if (dBox != null)
			dBox.render(g);
		// editor tabs
		g2d.setColor((Profile.isLoaded() ? Color.white : Color.gray));
		g2d.fillRect(0, winSize2.height - 17, winSize2.width, winSize2.height);
		g2d.setColor(Color.black);
		g2d.drawLine(0, winSize2.height - 17, winSize2.width, winSize2.height - 17);
		final EditorTab[] tv = EditorTab.values();
		int tn = tv.length;
		if (!Defines.getSpecial("VarHack"))
			tn--;
		int ti = 0;
		for (int xx = -1; xx < winSize2.width; xx += winSize2.width / tn + 1) {
			final EditorTab t = tv[ti];
			g2d.drawLine(xx, winSize2.height - 17, xx, winSize2.height - 1);
			g2d.drawImage(Resources.editorTabIcons[ti], xx + 1, winSize2.height - 16, null);
			FrontUtils.drawString(g2d, t.label(), xx + 18, winSize2.height - 20);
			if (Profile.isLoaded() && t == currentTab) {
				g2d.setColor((Profile.isLoaded() ? Color.white : Color.gray));
				g2d.drawLine(xx + 1, winSize2.height - 17, xx + winSize2.width / tn + 1, winSize2.height - 17);
				g2d.setColor(Color.black);
			}
			ti++;
		}
	}

	private void loadProfile() {
		if (fc == null)
			fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Profile Files", "dat");
		fc.setFileFilter(filter);
		fc.setCurrentDirectory(new File("."));
		int returnVal = fc.showOpenDialog(Main.window);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			Main.loadProfile(fc.getSelectedFile());
		else
			return;
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
			for (int xx = -1; xx < winSize.width; xx += winSize.width / 4 + 1) {
				if (FrontUtils.pointInRectangle(px, py, xx, 0, winSize.width / 4 + 1, 17)) {
					switch (bi) {
					case 0: // load profile
						loadProfile();
						break;
					case 1: // load defines
						dBox = new DefineDialog();
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
					case 3: // about
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
			if (!Defines.getSpecial("VarHack"))
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
