package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.Button;
import com.leo.cse.frontend.ui.components.DefineBox;
import com.leo.cse.frontend.ui.components.IntegerBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.MapBox;
import com.leo.cse.frontend.ui.components.MapView;
import com.leo.cse.frontend.ui.components.RadioBoxes;
import com.leo.cse.frontend.ui.components.ShortBox;

public class GeneralPanel extends Panel {

	private MapView mp;

	public GeneralPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		compList.add(new Label("Map:", 4, 4));
		compList.add(new MapBox(36, 4, 240, 16, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.sortMapsAlphabetically;
			}
		}));
		compList.add(new BooleanBox("Sort alphabetically", 280, 4, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.sortMapsAlphabetically;
			}
		}, new Function<Boolean, Boolean>() {
			@Override
			public Boolean apply(Boolean t) {
				SaveEditorPanel.sortMapsAlphabetically = t;
				return t;
			}
		}));
		compList.add(new Label("Song:", 4, 24));
		compList.add(new DefineBox(36, 24, 240, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				try {
					return (Integer) ProfileManager.getField(NormalProfile.FIELD_SONG);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_SONG, t);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "Song", "song"));
		compList.add(new Label("Position: (", 4, 44));
		compList.add(new ShortBox(54, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					short x = (Short) ProfileManager.getField(NormalProfile.FIELD_X_POSITION);
					return (short) (x / 32);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_X_POSITION, t * 32);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "X position"));
		compList.add(new Label(",", 118, 44));
		compList.add(new ShortBox(124, 44, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					short x = (Short) ProfileManager.getField(NormalProfile.FIELD_Y_POSITION);
					return (short) (x / 32);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_Y_POSITION, t * 32);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "Y position"));
		compList.add(new Label(")", 188, 44));
		compList.add(new Label("Exact Pos: (", 4, 64));
		compList.add(new ShortBox(64, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					short x = (Short) ProfileManager.getField(NormalProfile.FIELD_X_POSITION);
					return (short) (x / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_X_POSITION,
							t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "exact X position"));
		compList.add(new Label(",", 128, 64));
		compList.add(new ShortBox(134, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					short y = (Short) ProfileManager.getField(NormalProfile.FIELD_Y_POSITION);
					return (short) (y / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_Y_POSITION,
							t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "exacct Y position"));
		int tile = 16 * MCI.getInteger("Game.GraphicsResolution", 1);
		String tileSize = tile + "x" + tile;
		compList.add(new Label(") (1 tile = " + tileSize + "px)", 198, 64));
		compList.add(new Label("Direction:", 4, 84));
		compList.add(new RadioBoxes(54, 84, 120, 2, new String[] { "Left", "Right" }, new Supplier<Integer>() {
			@Override
			public Integer get() {
				try {
					return ((Integer) ProfileManager.getField(NormalProfile.FIELD_DIRECTION) == 2 ? 1 : 0);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_DIRECTION, (t == 1 ? 2 : 0));
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, false, (Integer index) -> {
			return true;
		}));
		compList.add(new Label("Health:", 4, 104));
		compList.add(new ShortBox(44, 104, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					return (Short) ProfileManager.getField(NormalProfile.FIELD_CURRENT_HEALTH);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_CURRENT_HEALTH, t);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "current health"));
		compList.add(new Label("/", 108, 104));
		compList.add(new ShortBox(114, 104, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				try {
					return (Short) ProfileManager.getField(NormalProfile.FIELD_MAXIMUM_HEALTH);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_MAXIMUM_HEALTH, t);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "maximum health"));
		compList.add(new Label("Seconds Played:", 4, 124));
		compList.add(new IntegerBox(92, 124, 120, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				try {
					int time = (Integer) ProfileManager.getField(NormalProfile.FIELD_TIME_PLAYED);
					return time / MCI.getInteger("Game.FPS", 50);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_TIME_PLAYED, t * MCI.getInteger("Game.FPS", 50));
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "time played"));
		compList.add(new Label("(resets at " + (4294967295l / MCI.getInteger("Game.FPS", 50)) + ")", 216, 124));
		compList.add(mp = new MapView(winSize.width / 2 - 320, 164, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.showMapGrid;
			}
		}));
		Profile.addListener(mp);
		compList.add(new BooleanBox("Show Grid?", 756, 406, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.showMapGrid;
			}
		}, new Function<Boolean, Boolean>() {
			@Override
			public Boolean apply(Boolean t) {
				SaveEditorPanel.showMapGrid = t;
				return t;
			}
		}).setEnabled(new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return ExeData.isLoaded();
			}
		}));
		compList.add(new Button("Snap to Grid", 756, 426, 100, 20, () -> {
			Short[] pos = new Short[2];
			try {
				pos[0] = (short) (Math.round((Short) (ProfileManager.getField(NormalProfile.FIELD_X_POSITION)) / 32.0)
						* 32);
				pos[1] = (short) (Math.round((Short) (ProfileManager.getField(NormalProfile.FIELD_Y_POSITION)) / 32.0)
						* 32);
				ProfileManager.setField(NormalProfile.FIELD_POSITION, pos);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			mp.updatePlayerPos();
			mp.updateCamCoords();
		}).setEnabled(new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return ExeData.isLoaded();
			}
		}));
	}

}
