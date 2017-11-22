package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.IProfile.ProfileFieldException;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.ProfileManager;
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
		compList.add(new BooleanBox("Sort alphabetically", false, 280, 4, new Supplier<Boolean>() {
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
					ProfileManager.setField(NormalProfile.FIELD_X_POSITION, (short) (t * 32));
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
					ProfileManager.setField(NormalProfile.FIELD_Y_POSITION, (short) (t * 32));
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
							(short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
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
							(short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
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
		if (ProfileManager.getType() == PlusProfile.class) {
			// difficulty
			compList.add(new Label("Difficulty:", 4, 144));
			compList.add(new RadioBoxes(54, 144, 176, 3, new String[] { "Original", "Easy", "Hard" }, () -> {
				int diff = 0;
				try {
					diff = (Short) ProfileManager.getField(PlusProfile.FIELD_DIFFICULTY);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				while (diff > 5)
					diff -= 5;
				if (diff % 2 == 1)
					diff--;
				return diff / 2;
			}, (Integer t) -> {
				try {
					short diff = (short) (t * 2);
					ProfileManager.setField(PlusProfile.FIELD_DIFFICULTY, diff);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, false, (Integer id) -> {
				return true;
			}));
			// beat hell
			compList.add(new BooleanBox("Beaten Bloodstained Sanctuary?", false, 234, 144, () -> {
				try {
					return (Boolean) ProfileManager.getField(PlusProfile.FIELD_BEAT_HELL);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return false;
			}, (Boolean t) -> {
				try {
					ProfileManager.setField(PlusProfile.FIELD_BEAT_HELL, t);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}));
			// modify date
			long unix = 0;
			try {
				unix = (long) ProfileManager.getField(PlusProfile.FIELD_MODIFY_DATE);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(unix * 1000);
			final int dateLoc = winSize.width / 2 + 40;
			compList.add(new Label("Last modified at:", dateLoc, 4));
			compList.add(new ShortBox(dateLoc, 24, 16, 16, () -> {
				return (short) (cal.get(Calendar.MONTH) + 1);
			}, (Short t) -> {
				if (t > 12)
					t = 12;
				cal.set(Calendar.MONTH, t - 1);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "month", 2));
			compList.add(new Label("/", dateLoc + 19, 24));
			compList.add(new ShortBox(dateLoc + 24, 24, 16, 16, () -> {
				return (short) cal.get(Calendar.DAY_OF_MONTH);
			}, (Short t) -> {
				short maxDay = (short) cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (t > maxDay)
					t = maxDay;
				cal.set(Calendar.DAY_OF_MONTH, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "day of month", 2));
			compList.add(new Label("/", dateLoc + 43, 24));
			compList.add(new ShortBox(dateLoc + 48, 24, 28, 16, () -> {
				return (short) cal.get(Calendar.YEAR);
			}, (Short t) -> {
				if (t > 9999)
					t = 9999;
				cal.set(Calendar.YEAR, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "year", 4));
			compList.add(new ShortBox(dateLoc + 88, 24, 16, 16, () -> {
				return (short) cal.get(Calendar.HOUR_OF_DAY);
			}, (Short t) -> {
				short maxS = (short) cal.getMaximum(Calendar.HOUR_OF_DAY);
				if (t > maxS)
					t = maxS;
				cal.set(Calendar.HOUR_OF_DAY, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "hours", 2));
			compList.add(new Label(":", dateLoc + 108, 24));
			compList.add(new ShortBox(dateLoc + 112, 24, 16, 16, () -> {
				return (short) cal.get(Calendar.MINUTE);
			}, (Short t) -> {
				short maxM = (short) cal.getMaximum(Calendar.MINUTE);
				if (t > maxM)
					t = maxM;
				cal.set(Calendar.MINUTE, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "minutes", 2));
			compList.add(new Label(":", dateLoc + 132, 24));
			compList.add(new ShortBox(dateLoc + 136, 24, 16, 16, () -> {
				return (short) cal.get(Calendar.SECOND);
			}, (Short t) -> {
				short maxM = (short) cal.getMaximum(Calendar.SECOND);
				if (t > maxM)
					t = maxM;
				cal.set(Calendar.SECOND, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "seconds", 2));
			compList.add(new Label(".", dateLoc + 155, 24));
			compList.add(new ShortBox(dateLoc + 160, 24, 22, 16, () -> {
				return (short) cal.get(Calendar.MILLISECOND);
			}, (Short t) -> {
				short maxM = (short) cal.getMaximum(Calendar.MILLISECOND);
				if (t > maxM)
					t = maxM;
				cal.set(Calendar.MILLISECOND, t);
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return t;
			}, "milliseconds", 3));
			compList.add(new Button("Set to current", dateLoc + 190, 22, 80, 16, () -> {
				cal.setTime(new Date());
				try {
					ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, cal.getTime().getTime() / 1000);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
			}));
			compList.add(new Label(() -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				dateFormat.setCalendar(cal);
				return "(" + dateFormat.format(cal.getTime()) + ")";
			}, dateLoc + 84, 40));
		}
		compList.add(mp = new MapView(winSize.width / 2 - 320, 164, new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return SaveEditorPanel.showMapGrid;
			}
		}));
		ProfileManager.addListener(mp);
		compList.add(new BooleanBox("Show Grid?", false, 756, 406, new Supplier<Boolean>() {
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
