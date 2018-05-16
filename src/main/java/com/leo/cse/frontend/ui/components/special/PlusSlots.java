package com.leo.cse.frontend.ui.components.special;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.MCI.EntityExtras;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.box.Button;
import com.leo.cse.frontend.ui.components.box.InputBox;
import com.leo.cse.frontend.ui.components.visual.Label;
import com.leo.cse.frontend.ui.components.visual.Line;
import com.leo.cse.frontend.ui.dialogs.BaseDialog;
import com.leo.cse.frontend.ui.dialogs.ConfirmDialog;
import com.leo.cse.frontend.ui.panels.GeneralPanel;

public class PlusSlots extends Component {

	class PlusSlot extends InputBox {

		private int section;

		private BufferedImage[] imgCache;
		private static final int IC_NUMBERS = 0, IC_NUMBERS_LAST = 9, IC_HP_BAR = 10, IC_HP_FILL = 11, IC_WEPS = 12,
				IC_WEPS_LAST = 25;
		private static final int IC_LENGTH = IC_WEPS_LAST + 1;

		public PlusSlot(int section, int x, int y) {
			super("PlusSlot:" + section, x, y, 260, 102);
			this.section = section;
		}

		@Override
		public void render(Graphics g, Rectangle viewport) {
			super.render(g, viewport);
			if (mode == MODE_PASTE && section == srcSec) {
				Color lc3 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 15);
				g.setColor(lc3);
				g.fillRect(x, y, width, height - 1);
			}
			if (imgCache == null)
				updateImageCache();
			g.setColor(Main.lineColor);
			g.setFont(Resources.font);
			FrontUtils.drawString(g, Integer.toUnsignedString(section + 1), x + 248, y);
			boolean exists = (boolean) ProfileManager.callMethod(PlusProfile.METHOD_FILE_EXISTS, section);
			if (!exists) {
				FrontUtils.drawString(g, "New", x + 4, y + 58);
				return;
			}
			ProfileManager.callMethod(PlusProfile.METHOD_PUSH_ACTIVE_FILE, section);
			int wepx = 0, wepnum = (Integer) ProfileManager.getField(NormalProfile.FIELD_CURRENT_WEAPON);
			for (int i = 0; i < 7; i++) {
				int wep = (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_ID, wepnum);
				wepnum++;
				if (wepnum > 6)
					wepnum = 0;
				if (wep == 0)
					continue;
				g.drawImage(imgCache[IC_WEPS + wep - 1], x + 4 + (34 * wepx), y + 4, null);
				wepx++;
			}
			short maxHP = (short) ProfileManager.getField(NormalProfile.FIELD_MAXIMUM_HEALTH);
			short curHP = (short) ProfileManager.getField(NormalProfile.FIELD_CURRENT_HEALTH);
			g.drawImage(imgCache[IC_HP_BAR], x + 4, y + 40, null);
			int curHPTen = curHP / 10;
			if (curHPTen != 0)
				g.drawImage(imgCache[IC_NUMBERS + curHPTen], x + 20, y + 40, null);
			g.drawImage(imgCache[IC_NUMBERS + (curHP % 10)], x + 36, y + 40, null);
			if (maxHP > 0)
				g.drawImage(imgCache[IC_HP_FILL], x + 52, y + 42, (int) (78 * (curHP / (float) maxHP)), 10, null);
			long unix = (long) ProfileManager.getField(PlusProfile.FIELD_MODIFY_DATE);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(unix * 1000);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
			dateFormat.setCalendar(cal);
			FrontUtils.drawString(g, dateFormat.format(cal.getTime()), x + 4, y + 58);
			int map = 0;
			map = (int) ProfileManager.getField(NormalProfile.FIELD_MAP);
			MapInfo mi = ExeData.getMapInfo(map);
			FrontUtils.drawString(g, mi.getMapName(), x + 4, y + 76);
			long costume = ((Boolean) ProfileManager.getField(NormalProfile.FIELD_EQUIPS, 6) ? 1 : 0);
			if (section > 2)
				costume += 10;
			int diff = (Short) ProfileManager.getField(PlusProfile.FIELD_DIFFICULTY);
			while (diff > 5)
				diff -= 5;
			if (diff % 2 == 1)
				diff--;
			costume += diff;
			int xPixel = x + 228, yPixel = y + 67;
			int sourceX1 = 0;
			int sourceY1 = (int) (64 * costume);
			EntityExtras pe = null;
			try {
				pe = MCI.getPlayerExtras(xPixel, yPixel, false, costume);
			} catch (NoSuchMethodException e) {
				Main.LOGGER.error("Failed to get player extras: not defined in MCI", e);
			}
			int sourceX2 = sourceX1 + 32, sourceY2 = sourceY1 + 32;
			if (pe != null) {
				Rectangle pf = pe.getFrameRect();
				Point po = pe.getOffset();
				sourceX1 = pf.x;
				sourceY1 = pf.y;
				sourceX2 = pf.width;
				sourceY2 = pf.height;
				xPixel += po.x;
				yPixel += po.y;
			}
			g.drawImage(ExeData.getImage(ExeData.getMyChar()), xPixel, yPixel, xPixel + Math.abs(sourceX2 - sourceX1),
					yPixel + Math.abs(sourceY2 - sourceY1), sourceX1, sourceY1, sourceX2, sourceY2, null);
			ProfileManager.callMethod(PlusProfile.METHOD_POP_ACTIVE_FILE);
		}

		private void updateImageCache() {
			BufferedImage tb = ExeData.getImage(ExeData.getTextBox());
			imgCache = new BufferedImage[IC_LENGTH];
			for (int i = IC_NUMBERS; i <= IC_NUMBERS_LAST; i++)
				imgCache[i] = tb.getSubimage(16 * (i - IC_NUMBERS), 112, 16, 16);
			imgCache[IC_HP_BAR] = tb.getSubimage(0, 80, 128, 16);
			imgCache[IC_HP_FILL] = tb.getSubimage(0, 50, 78, 10);
			BufferedImage ai = ExeData.getImage(ExeData.getArmsImage());
			int ystart = MCI.getInteger("Game.ArmsImageYStart", 0), size = MCI.getInteger("Game.ArmsImageSize", 32);
			for (int i = IC_WEPS; i <= IC_WEPS_LAST; i++)
				imgCache[i] = ai.getSubimage(size * (i + 1 - IC_WEPS), ystart, size, size);
		}

		@Override
		public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
			if (srcSec == -1)
				srcSec = section;
			else if (dstSec == -1)
				dstSec = section;
		}

	}

	private BaseDialog dialog;
	private List<Component> comps;
	private int mode, curSec = -1, srcSec = -1, dstSec = -1;

	private static final int MODE_NORMAL = 0, MODE_DELETE = 1, MODE_COPY = 2, MODE_PASTE = 3;

	private void addComponent(Component comp) {
		comps.add(comp);
	}

	public PlusSlots(BaseDialog dialog) {
		super("PlusSlots", 0, 0, 536, 398);
		this.dialog = dialog;
		curSec = -1;
		curSec = (int) ProfileManager.callMethod(PlusProfile.METHOD_GET_ACTIVE_FILE);
		comps = new ArrayList<>();
		addComponent(new Label("Normal Files", 4, 2));
		addComponent(new PlusSlot(0, 4, 20));
		addComponent(new PlusSlot(1, 4, 126));
		addComponent(new PlusSlot(2, 4, 232));
		addComponent(new Line(268, 4, 0, 329));
		addComponent(new Label("Curly Story", 272, 2));
		addComponent(new PlusSlot(3, 272, 20));
		addComponent(new PlusSlot(4, 272, 126));
		addComponent(new PlusSlot(5, 272, 232));
		addComponent(new Label(() -> {
			switch (mode) {
			case MODE_NORMAL:
				return "Click on a file to load it";
			case MODE_DELETE:
				return "Click on a file to delete it";
			case MODE_COPY:
				return "Click on a file to copy it";
			case MODE_PASTE:
				return "Click on a file to paste file " + (srcSec + 1) + " to";
			}
			return "Unknown mode " + mode;
		}, 268, 338, true));
		Button delBtn = new Button(() -> {
			if (mode == MODE_DELETE)
				return "Cancel Delete";
			return "Delete File";
		}, 4, 358, 262, 16, () -> {
			if (mode == MODE_DELETE) {
				mode = MODE_NORMAL;
				srcSec = -1;
			} else
				mode = MODE_DELETE;
		});
		delBtn.setEnabled(() -> {
			return mode == MODE_NORMAL || mode == MODE_DELETE;
		});
		addComponent(delBtn);
		Button cpyBtn = new Button(() -> {
			if (mode == MODE_COPY || mode == MODE_PASTE)
				return "Cancel Copy";
			return "Copy File";
		}, 270, 358, 262, 16, () -> {
			if (mode == MODE_COPY || mode == MODE_PASTE) {
				mode = MODE_NORMAL;
				srcSec = -1;
				dstSec = -1;
			} else
				mode = MODE_COPY;
		});
		cpyBtn.setEnabled(() -> {
			return mode == MODE_NORMAL || mode == MODE_COPY || mode == MODE_PASTE;
		});
		addComponent(cpyBtn);
		Button savBtn = new Button("Save Changes", 137, 378, 262, 16, () -> {
			try {
				ProfileManager.save();
			} catch (IOException e1) {
				Main.LOGGER.error("Failed to save profile", e1);
				JOptionPane.showMessageDialog(Main.window, "An error occured while saving the profile file:\n" + e1,
						"Could not save profile file!", JOptionPane.ERROR_MESSAGE);
			}
		});
		savBtn.setEnabled(() -> {
			return mode == MODE_NORMAL && ProfileManager.isModified();
		});
		addComponent(savBtn);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		for (Component comp : comps)
			comp.render(g, viewport);
	}

	private void deleteFile() {
		if (srcSec == curSec)
			SaveEditorPanel.panel.setGotProfile(false);
		ProfileManager.callMethod(PlusProfile.METHOD_DELETE_FILE, srcSec);
		mode = MODE_NORMAL;
		srcSec = -1;
	}

	private void pasteFile() {
		ProfileManager.callMethod(PlusProfile.METHOD_CLONE_FILE, srcSec, dstSec);
		mode = MODE_NORMAL;
		srcSec = -1;
		dstSec = -1;
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		for (Component comp : comps) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			if (FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh)) {
				comp.onClick(x, y, shiftDown, ctrlDown);
				comp.updateHover(x, y, true, shiftDown, ctrlDown);
				break;
			}
		}
		if (srcSec != -1) {
			boolean exists;
			switch (mode) {
			case MODE_NORMAL:
				exists = (boolean) ProfileManager.callMethod(PlusProfile.METHOD_FILE_EXISTS, srcSec);
				if (!exists)
					ProfileManager.callMethod(PlusProfile.METHOD_NEW_FILE, srcSec);
				ProfileManager.callMethod(PlusProfile.METHOD_SET_ACTIVE_FILE, srcSec);
				SaveEditorPanel.panel.setGotProfile(true);
				GeneralPanel.modifyDate
						.setTimeInMillis((long) ProfileManager.getField(PlusProfile.FIELD_MODIFY_DATE) * 1000);
				dialog.requestClose();
				break;
			case MODE_DELETE:
				exists = (boolean) ProfileManager.callMethod(PlusProfile.METHOD_FILE_EXISTS, srcSec);
				if (exists) {
					SaveEditorPanel.panel.addDialogBox(new ConfirmDialog("Confirm file deletion",
							"Are you sure you want to delete file " + (srcSec + 1) + "?", (Boolean t) -> {
								if (t)
									deleteFile();
							}));

				}
				break;
			case MODE_COPY:
				mode = MODE_PASTE;
				break;
			case MODE_PASTE:
				if (dstSec < 0 || dstSec == srcSec)
					break;
				exists = (boolean) ProfileManager.callMethod(PlusProfile.METHOD_FILE_EXISTS, dstSec);
				if (exists) {
					SaveEditorPanel.panel.addDialogBox(
							new ConfirmDialog("Confirm file overwrite", "Are you sure you want to overwrite file "
									+ (dstSec + 1) + " with file " + (srcSec + 1) + "?", (Boolean t) -> {
										if (t)
											pasteFile();
									}));

				} else
					pasteFile();
				break;
			default:
				Main.LOGGER.error("Unknown mode " + mode);
				break;
			}
		}
	}

	@Override
	public void updateHover(int x, int y, boolean hover, boolean shiftDown, boolean ctrlDown) {
		super.updateHover(x, y, hover, shiftDown, ctrlDown);
		for (Component comp : comps) {
			final int rx = comp.getX(), ry = comp.getY(), rw = comp.getWidth(), rh = comp.getHeight();
			boolean hoverC = hover;
			if (hoverC)
				hoverC = FrontUtils.pointInRectangle(x, y, rx, ry, rw, rh);
			comp.updateHover(x, y, hoverC, shiftDown, ctrlDown);
		}
	}

}
