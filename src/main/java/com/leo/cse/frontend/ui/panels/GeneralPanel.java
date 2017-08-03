package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
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
				return Profile.getSong();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setSong(t);
				return t;
			}
		}, "Song", "song"));
		compList.add(new Label("Position: (", 4, 44));
		compList.add(new ShortBox(54, 44, 60, 16, new Supplier<Short>() {
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
		compList.add(new Label(",", 118, 44));
		compList.add(new ShortBox(124, 44, 60, 16, new Supplier<Short>() {
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
		compList.add(new Label(")", 188, 44));
		compList.add(new Label("Exact Pos: (", 4, 64));
		compList.add(new ShortBox(64, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getX() / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setX((short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
				return t;
			}
		}, "exact X position"));
		compList.add(new Label(",", 128, 64));
		compList.add(new ShortBox(134, 64, 60, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return (short) (Profile.getY() / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setY((short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
				return t;
			}
		}, "exacct Y position"));
		int tile = 16 * MCI.getInteger("Game.GraphicsResolution", 1);
		String tileSize = tile + "x" + tile;
		compList.add(new Label(") (1 tile = " + tileSize + "px)", 198, 64));
		compList.add(new Label("Direction:", 4, 84));
		compList.add(new RadioBoxes(54, 84, 120, 2, new String[] { "Left", "Right" }, new Supplier<Integer>() {
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
		}, false, (int index) -> {
			return true;
		}));
		compList.add(new Label("Health:", 4, 104));
		compList.add(new ShortBox(44, 104, 60, 16, new Supplier<Short>() {
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
		compList.add(new Label("/", 108, 104));
		compList.add(new ShortBox(114, 104, 60, 16, new Supplier<Short>() {
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
		compList.add(new Label("Seconds Played:", 4, 124));
		compList.add(new IntegerBox(92, 124, 120, 16, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getTime() / MCI.getInteger("Game.FPS", 50);
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setTime(t * MCI.getInteger("Game.FPS", 50));
				return t;
			}
		}, "time played"));
		compList.add(new Label("(resets at " + (4294967295l / MCI.getInteger("Game.FPS", 50)) + ")", 216, 124));
		compList.add(mp = new MapView(winSize.width / 2 - 320, 164, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.showMapGrid;
			}
		}));
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
		compList.add(new Button("Snap to Grid", 756, 426, 100, 20, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				Profile.setX((short) (Math.round(Profile.getX() / 32.0) * 32));
				Profile.setY((short) (Math.round(Profile.getY() / 32.0) * 32));
				mp.getCamCoords();
				return false;
			}
		}).setEnabled(new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return ExeData.isLoaded();
			}
		}));
	}

}
