package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.leo.cse.backend.exe.ExeData;
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

	public static Calendar modifyDate;

	private MapView mp;

	private void updateTime() {
		long time = modifyDate.getTime().getTime() / 1000;
		if (time < 0)
			time = 0;
		ProfileManager.setField(PlusProfile.FIELD_MODIFY_DATE, time);
		modifyDate.setTimeInMillis(time * 1000);
	}

	public GeneralPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		compList.add(new Label("Map:", 4, 4));
		compList.add(new MapBox(36, 4, 240, 16, () -> SaveEditorPanel.sortMapsAlphabetically));
		compList.add(new BooleanBox("Sort alphabetically", false, 280, 4, () -> SaveEditorPanel.sortMapsAlphabetically,
				t -> {
					SaveEditorPanel.sortMapsAlphabetically = t;
					return t;
				}));
		compList.add(new Label("Song:", 4, 24));
		compList.add(new DefineBox(36, 24, 240, 16, () -> {
			return (Integer) ProfileManager.getField(NormalProfile.FIELD_SONG);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_SONG, t);
			return t;
		}, "Song", "song"));
		compList.add(new Label("Position: (", 4, 44));
		compList.add(new ShortBox(54, 44, 60, 16, () -> {
			short x = (Short) ProfileManager.getField(NormalProfile.FIELD_X_POSITION);
			return (short) (x / 32);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_X_POSITION, (short) (t * 32));
			return t;
		}, "X position"));
		compList.add(new Label(",", 118, 44));
		compList.add(new ShortBox(124, 44, 60, 16, () -> {
			short x = (Short) ProfileManager.getField(NormalProfile.FIELD_Y_POSITION);
			return (short) (x / 32);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_Y_POSITION, (short) (t * 32));
			return t;
		}, "Y position"));
		compList.add(new Label(")", 188, 44));
		compList.add(new Label("Exact Pos: (", 4, 64));
		compList.add(new ShortBox(64, 64, 60, 16, () -> {
			short x = (Short) ProfileManager.getField(NormalProfile.FIELD_X_POSITION);
			return (short) (x / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_X_POSITION,
					(short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
			return t;
		}, "exact X position"));
		compList.add(new Label(",", 128, 64));
		compList.add(new ShortBox(134, 64, 60, 16, () -> {
			short y = (Short) ProfileManager.getField(NormalProfile.FIELD_Y_POSITION);
			return (short) (y / (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_Y_POSITION,
					(short) (t * (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1))));
			return t;
		}, "exacct Y position"));
		int tile = 16 * MCI.getInteger("Game.GraphicsResolution", 1);
		String tileSize = tile + "x" + tile;
		compList.add(new Label(") (1 tile = " + tileSize + "px)", 198, 64));
		compList.add(new Label("Direction:", 4, 84));
		compList.add(new RadioBoxes(54, 84, 120, 2, new String[] { "Left", "Right" }, () -> {
			return ((Integer) ProfileManager.getField(NormalProfile.FIELD_DIRECTION) == 2 ? 1 : 0);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_DIRECTION, (t == 1 ? 2 : 0));
			return t;
		}, false, (Integer index) -> {
			return true;
		}));
		compList.add(new Label("Health:", 4, 104));
		compList.add(new ShortBox(44, 104, 60, 16, () -> {
			return (Short) ProfileManager.getField(NormalProfile.FIELD_CURRENT_HEALTH);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_CURRENT_HEALTH, t);
			return t;
		}, "current health"));
		compList.add(new Label("/", 108, 104));
		compList.add(new ShortBox(114, 104, 60, 16, () -> {
			return (Short) ProfileManager.getField(NormalProfile.FIELD_MAXIMUM_HEALTH);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_MAXIMUM_HEALTH, t);
			return t;
		}, "maximum health"));
		compList.add(new Label("Seconds Played:", 4, 124));
		compList.add(new IntegerBox(92, 124, 120, 16, () -> {
			int time = (Integer) ProfileManager.getField(NormalProfile.FIELD_TIME_PLAYED);
			return time / MCI.getInteger("Game.FPS", 50);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_TIME_PLAYED, t * MCI.getInteger("Game.FPS", 50));
			return t;
		}, "time played"));
		compList.add(new Label("(resets at " + (4294967295l / MCI.getInteger("Game.FPS", 50)) + ")", 216, 124));
		if (ProfileManager.getType() == PlusProfile.class) {
			if (((int) ProfileManager.callMethod(PlusProfile.METHOD_GET_ACTIVE_FILE)) < 3) {
				// difficulty
				compList.add(new Label("Difficulty:", 4, 144));
				compList.add(new RadioBoxes(54, 144, 176, 3, new String[] { "Original", "Easy", "Hard" }, () -> {
					int diff = (Short) ProfileManager.getField(PlusProfile.FIELD_DIFFICULTY);
					while (diff > 5)
						diff -= 5;
					if (diff % 2 == 1)
						diff--;
					return diff / 2;
				}, (Integer t) -> {
					short diff = (short) (t * 2);
					ProfileManager.setField(PlusProfile.FIELD_DIFFICULTY, diff);
					return t;
				}, false, (Integer id) -> {
					return true;
				}));
				// beat hell
				compList.add(new BooleanBox("Beaten Bloodstained Sanctuary?", false, 234, 144, () -> {
					return (Boolean) ProfileManager.getField(PlusProfile.FIELD_BEAT_HELL);
				}, (Boolean t) -> {
					ProfileManager.setField(PlusProfile.FIELD_BEAT_HELL, t);
					return t;
				}));
			}
			// modify date
			modifyDate = new GregorianCalendar();
			modifyDate.setTimeInMillis(0);
			final int dateLoc = winSize.width / 2 + 40;
			compList.add(new Label("Last modified at:", dateLoc, 4));
			compList.add(new ShortBox(dateLoc, 24, 16, 16, () -> {
				return (short) (modifyDate.get(Calendar.MONTH) + 1);
			}, (Short t) -> {
				if (t > 12)
					t = 12;
				modifyDate.set(Calendar.MONTH, t - 1);
				updateTime();
				return t;
			}, "month", 2));
			compList.add(new Label("/", dateLoc + 19, 24));
			compList.add(new ShortBox(dateLoc + 24, 24, 16, 16, () -> {
				return (short) modifyDate.get(Calendar.DAY_OF_MONTH);
			}, (Short t) -> {
				short maxDay = (short) modifyDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				if (t > maxDay)
					t = maxDay;
				modifyDate.set(Calendar.DAY_OF_MONTH, t);
				updateTime();
				return t;
			}, "day of month", 2));
			compList.add(new Label("/", dateLoc + 43, 24));
			compList.add(new ShortBox(dateLoc + 48, 24, 28, 16, () -> {
				return (short) modifyDate.get(Calendar.YEAR);
			}, (Short t) -> {
				if (t > 9999)
					t = 9999;
				modifyDate.set(Calendar.YEAR, t);
				updateTime();
				return t;
			}, "year", 4));
			compList.add(new ShortBox(dateLoc + 88, 24, 16, 16, () -> {
				return (short) modifyDate.get(Calendar.HOUR_OF_DAY);
			}, (Short t) -> {
				short maxS = (short) modifyDate.getMaximum(Calendar.HOUR_OF_DAY);
				if (t > maxS)
					t = maxS;
				modifyDate.set(Calendar.HOUR_OF_DAY, t);
				updateTime();
				return t;
			}, "hours", 2));
			compList.add(new Label(":", dateLoc + 108, 24));
			compList.add(new ShortBox(dateLoc + 112, 24, 16, 16, () -> {
				return (short) modifyDate.get(Calendar.MINUTE);
			}, (Short t) -> {
				short maxM = (short) modifyDate.getMaximum(Calendar.MINUTE);
				if (t > maxM)
					t = maxM;
				modifyDate.set(Calendar.MINUTE, t);
				updateTime();
				return t;
			}, "minutes", 2));
			compList.add(new Label(":", dateLoc + 132, 24));
			compList.add(new ShortBox(dateLoc + 136, 24, 16, 16, () -> {
				return (short) modifyDate.get(Calendar.SECOND);
			}, (Short t) -> {
				short maxM = (short) modifyDate.getMaximum(Calendar.SECOND);
				if (t > maxM)
					t = maxM;
				modifyDate.set(Calendar.SECOND, t);
				updateTime();
				return t;
			}, "seconds", 2));
			compList.add(new Button("Set to current", dateLoc + 164, 24, 80, 16, () -> {
				modifyDate.setTime(new Date());
				updateTime();
			}));
			compList.add(new Label(() -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
				dateFormat.setCalendar(modifyDate);
				return "(" + dateFormat.format(modifyDate.getTime()) + ")";
			}, dateLoc + 84, 40));
		}
		compList.add(mp = new MapView(winSize.width / 2 - 320, 164, () -> SaveEditorPanel.showMapGrid));
		ProfileManager.addListener(mp);
		compList.add(new BooleanBox("Show Grid?", false, 758, 406, () -> SaveEditorPanel.showMapGrid, t -> {
			SaveEditorPanel.showMapGrid = t;
			return t;
		}).setEnabled(() -> ExeData.isLoaded()));
		compList.add(new Button("Snap to Grid", 758, 426, 100, 20, () -> {
			Short[] pos = new Short[2];
			pos[0] = (short) (Math.round((Short) (ProfileManager.getField(NormalProfile.FIELD_X_POSITION)) / 32.0)
					* 32);
			pos[1] = (short) (Math.round((Short) (ProfileManager.getField(NormalProfile.FIELD_Y_POSITION)) / 32.0)
					* 32);
			ProfileManager.setField(NormalProfile.FIELD_POSITION, pos);
			mp.updatePlayerPos();
			mp.updateCamCoords();
		}).setEnabled(() -> ExeData.isLoaded()));
	}

}
