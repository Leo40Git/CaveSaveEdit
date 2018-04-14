package com.leo.cse.frontend.ui.components.special;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.exe.MapInfo.PxeEntry;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.ProfileListener;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.MCI.EntityExtras;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;
import com.leo.cse.frontend.ui.components.Component;
import com.leo.cse.frontend.ui.components.IDraggable;

public class MapView extends Component implements IDraggable, ProfileListener {

	private static final Color COLOR_NULL = new Color(0, 0, 16);

	private MapInfo mapInfo;
	private int[][][] map;
	private BufferedImage tileset;
	private int setWidth;
	private int camX = 0, camY = 0;
	private int ignoreClick = 0;
	private int lastMap;
	private Supplier<Boolean> gSup;
	private Short[] playerPos;
	private short playerHoverX, playerHoverY;

	private int getMap() {
		Object obj = null;
		if (ProfileManager.isLoaded())
			obj = ProfileManager.getField(NormalProfile.FIELD_MAP);
		if (obj == null)
			return -1;
		return (Integer) obj;
	}

	public MapView(int x, int y, Supplier<Boolean> gSup) {
		super("MapView", x, y, 640, 480);
		lastMap = getMap();
		this.gSup = gSup;
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		boolean notLoaded = false;
		if (!ExeData.isLoaded()) {
			g.setColor(COLOR_NULL);
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.setFont(Resources.fontL);
			FrontUtils.drawStringCentered(g, "NO MOD LOADED!", x + width / 2, y + height / 2, true, false);
			notLoaded = true;
		}
		final String camCoords = "CameraPos:\n(" + camX / 32 + "," + camY / 32 + ")\nExactCPos:\n("
				+ (int) (camX / 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)) + ","
				+ (int) (camY / 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)) + ")";
		final String instruct = "Move player by\nclicking/dragging\nOR\nwith WASD/arrow keys";
		final String mod = "Mod key effects:\nNone - 1 tile\nShift - 1/2 tile\nCtrl - 1/4 tile\nCtrl+Shift - 1 pixel";
		g.setFont(Resources.fontS);
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, camCoords + "\n\n" + instruct + "\n" + mod, x + 642, y);
		if (notLoaded)
			return;
		if (SaveEditorPanel.panel.getLastFocus() == this) {
			g.setColor(Main.lineColor);
			g.drawRect(x - 1, y - 1, width + 1, height + 1);
		}
		mapInfo = ExeData.getMapInfo(getMap());
		if (mapInfo.hasMissingAssets()) {
			g.setColor(COLOR_NULL);
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.setFont(Resources.fontL);
			FrontUtils.drawStringCentered(g, "MISSING ASSETS!", x + width / 2, y + height / 2, true, false);
			g.setFont(Resources.font);
			FrontUtils.drawStringCentered(g, "The following assets failed to load:\n" + mapInfo.getMissingAssets(),
					x + width / 2, y + height / 2 + 30, true, false);
			return;
		}
		map = mapInfo.getMap();
		tileset = ExeData.getImage(mapInfo.getTileset());
		setWidth = tileset.getWidth() / 32;
		if (playerPos == null)
			updatePlayerPos();
		BufferedImage surf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = (Graphics2D) surf.getGraphics();
		sg.setColor(COLOR_NULL);
		sg.fillRect(0, 0, width, height);
		if (getMap() != lastMap || ignoreClick == 0)
			updateCamCoords();
		lastMap = getMap();
		drawBackground(sg);
		sg.translate(-camX, -camY);
		drawTiles(sg, 0);
		drawEntities(sg);
		drawMyChar(sg, DRAWTYPE_NORMAL);
		drawTiles(sg, 1);
		drawMyChar(sg, DRAWTYPE_TRANS);
		if (hover && ignoreClick != 2)
			drawMyChar(sg, DRAWTYPE_HOVER);
		if (gSup.get())
			drawGrid(sg);
		sg.translate(camX, camY);
		g.drawImage(surf, x, y, null);
	}

	private void drawBackground(Graphics g) {
		int scrollType = mapInfo.getScrollType();
		if (scrollType == 3 || scrollType == 4)
			return;
		BufferedImage bg = ExeData.getImage(mapInfo.getBgImage());
		int iw = bg.getWidth(null);
		int ih = bg.getHeight(null);
		for (int x = 0; x < map[0][0].length * 32; x += iw) {
			for (int y = 0; y < map[0].length * 32; y += ih) {
				g.drawImage(bg, x, y, iw, ih, null);
			}
		}
	}

	private void drawTiles(Graphics g, int l) {
		int xx = 0;
		int yy = 0;
		for (int i = 0; i < map[l].length; i++) {
			for (int j = 0; j < map[l][i].length; j++) {
				int xPixel = xx * 32 - 16;
				int yPixel = yy * 32 - 16;
				int tile = map[l][i][j];
				int pxa = mapInfo.calcPxa(tile);
				if (pxa >= 0x20 && pxa <= 0x3F || pxa >= 0x81 && pxa <= 0xFF) {
					// no draw
					continue;
				}
				// draw normal tile
				int sourceX = (tile % setWidth) * 32;
				int sourceY = (tile / setWidth) * 32;
				g.drawImage(tileset, xPixel, yPixel, xPixel + 32, yPixel + 32, sourceX, sourceY, sourceX + 32,
						sourceY + 32, null);
				if (pxa == 0x43) {
					// draw breakable tile on top of normal tile
					g.drawImage(ExeData.getImage(ExeData.getNpcSym()), xPixel, yPixel, xPixel + 32, yPixel + 32, 512,
							96, 544, 128, null);
				}
				xx++;
			}
			xx = 0;
			yy++;
		}
	}

	private void drawEntities(Graphics2D g) {
		Iterator<PxeEntry> it = mapInfo.getPxeIterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			PxeEntry e = it.next();
			short flags = e.getFlags();
			short flagID = e.getFlagID();
			if ((flags & 0x0800) != 0) {
				// Appear once flagID set
				if (!(boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, flagID))
					return;
			}
			if ((flags & 0x4000) != 0) {
				// No Appear if flagID set
				if ((boolean) ProfileManager.getField(NormalProfile.FIELD_FLAGS, flagID))
					return;
			}
			BufferedImage srcImg;
			int tilesetNum = e.getInfo().getTileset();
			if (tilesetNum == 0) // title
				srcImg = ExeData.getImage(ExeData.getTitle());
			else if (tilesetNum == 1) // PIXEL
				srcImg = ExeData.getImage(ExeData.getPixel());
			else if (tilesetNum == 6) // fade
				srcImg = ExeData.getImage(ExeData.getFade());
			else if (tilesetNum == 2) // map tileset
				srcImg = ExeData.getImage(mapInfo.getTileset());
			else if (tilesetNum == 8) // itemimage
				srcImg = ExeData.getImage(ExeData.getItemImage());
			else if (tilesetNum == 11) // arms
				srcImg = ExeData.getImage(ExeData.getArms());
			else if (tilesetNum == 12) // armsimage
				srcImg = ExeData.getImage(ExeData.getArmsImage());
			else if (tilesetNum == 14) // stageimage
				srcImg = ExeData.getImage(ExeData.getStageImage());
			else if (tilesetNum == 15) // loading
				srcImg = ExeData.getImage(ExeData.getLoading());
			else if (tilesetNum == 16) // npc myChar
				srcImg = ExeData.getImage(ExeData.getMyChar());
			else if (tilesetNum == 17) // bullet
				srcImg = ExeData.getImage(ExeData.getBullet());
			else if (tilesetNum == 19) // caret
				srcImg = ExeData.getImage(ExeData.getCaret());
			else if (tilesetNum == 20) // npc sym
				srcImg = ExeData.getImage(ExeData.getNpcSym());
			else if (tilesetNum == 21) // map npc sheet 1
				srcImg = ExeData.getImage(mapInfo.getNpcSheet1());
			else if (tilesetNum == 22) // map npc sheet 2
				srcImg = ExeData.getImage(mapInfo.getNpcSheet2());
			else if (tilesetNum == 23) // npc regu
				srcImg = ExeData.getImage(ExeData.getNpcRegu());
			else if (tilesetNum == 26) // textbox
				srcImg = ExeData.getImage(ExeData.getTextBox());
			else if (tilesetNum == 27) // face
				srcImg = ExeData.getImage(ExeData.getFace());
			else if (tilesetNum == 28) // map background
				srcImg = ExeData.getImage(mapInfo.getBgImage());
			else
				srcImg = null;
			EntityExtras ee;
			try {
				ee = MCI.getEntityExtras(e);
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
				return;
			}
			if (ee == null)
				continue;
			Rectangle frameRect = ee.getFrameRect();
			if (frameRect.x < 0 || frameRect.y < 0 || frameRect.width < 0 || frameRect.height < 0)
				continue;
			Point offset = ee.getOffset();
			if (srcImg != null) {
				int srcX = frameRect.x;
				int srcY = frameRect.y;
				int srcX2 = frameRect.width;
				int srcY2 = frameRect.height;
				Rectangle dest = e.getDrawArea();
				int dstX = dest.x + offset.x;
				int dstY = dest.y + offset.y;
				g.drawImage(srcImg, dstX, dstY, dstX + dest.width, dstY + dest.height, srcX, srcY,
						srcX2, srcY2, null);
			}
		}
	}

	private static final int DRAWTYPE_NORMAL = 0, DRAWTYPE_TRANS = 1, DRAWTYPE_HOVER = 2;

	private void drawMyChar(Graphics2D g, int drawType) {
		double snap = Math.max(1, 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1));
		int dir = 0;
		long costume = 0;
		dir = ((Integer) ProfileManager.getField(NormalProfile.FIELD_DIRECTION) == 2 ? 1 : 0);
		if (MCI.getSpecial("VarHack"))
			costume = (Integer) ProfileManager.getField(NormalProfile.FIELD_VARIABLES, 6);
		else if (MCI.getSpecial("MimHack"))
			costume = (Integer) ProfileManager.getField(NormalProfile.FIELD_MIM_COSTUME);
		else {
			costume = ((Boolean) ProfileManager.getField(NormalProfile.FIELD_EQUIPS, 6) ? 1 : 0);
			if (ProfileManager.getType() == PlusProfile.class) {
				int curSec = (Integer) ProfileManager.callMethod(PlusProfile.METHOD_GET_ACTIVE_FILE);
				if (curSec > 2)
					costume += 10;
				int diff = (Short) ProfileManager.getField(PlusProfile.FIELD_DIFFICULTY);
				while (diff > 5)
					diff -= 5;
				if (diff % 2 == 1)
					diff--;
				costume += diff;
			}
		}
		int xPixel = playerPos[0];
		if (drawType == DRAWTYPE_HOVER)
			xPixel = playerHoverX;
		xPixel -= 16;
		xPixel -= (xPixel % snap);
		int yPixel = playerPos[1];
		if (drawType == DRAWTYPE_HOVER)
			yPixel = playerHoverY;
		yPixel -= (drawType == DRAWTYPE_HOVER ? 32 : 16);
		yPixel -= (yPixel % snap);
		int sourceX1 = 0;
		int sourceY1 = (int) (64 * costume + 32 * dir);
		Composite oc = g.getComposite();
		float alpha = 1;
		switch (drawType) {
		case DRAWTYPE_TRANS:
			alpha = 0.5f;
			break;
		case DRAWTYPE_HOVER:
			alpha = 0.25f;
			break;
		default:
			alpha = 1;
			break;
		}
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g.setComposite(ac);
		EntityExtras pe = null;
		try {
			pe = MCI.getPlayerExtras(xPixel, yPixel, (dir == 1 ? true : false), costume);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		int sourceX2 = 32, sourceY2 = sourceY1 + 32;
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
		g.setComposite(oc);
	}

	private void drawGrid(Graphics2D g) {
		g.setColor(Main.lineColor);
		Composite oc = g.getComposite();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		g.setComposite(ac);
		int w = map[0][0].length * 32, h = map[0].length * 32;
		int k;
		for (k = 0; k < map[0].length; k++)
			g.drawLine(0, k * 32 + 16, w, k * 32 + 16);
		for (k = 0; k < map[0][0].length; k++)
			g.drawLine(k * 32 + 16, 0, k * 32 + 16, h);
		g.setComposite(oc);
	}

	public void updateCamCoords() {
		camX = Math.max(0, Math.min((map[0][0].length - 21) * 32, playerPos[0] - width / 2));
		camY = Math.max(0, Math.min((map[0].length - 16) * 32, playerPos[1] - height / 2));
	}

	public void updatePlayerPos() {
		playerPos = (Short[]) ProfileManager.getField(NormalProfile.FIELD_POSITION);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!ExeData.isLoaded())
			return;
		if (SaveEditorPanel.panel.getLastFocus() != this) {
			updateCamCoords();
			return;
		}
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		updateCamCoords();
		if (ignoreClick > 0)
			ignoreClick--;
		else {
			Short[] pos = new Short[2];
			pos[0] = (short) (x - this.x + camX);
			pos[1] = (short) (y - this.y + camY);
			ProfileManager.setField(NormalProfile.FIELD_POSITION, pos);
			updatePlayerPos();
		}
	}

	@Override
	public void onKey(int code, boolean shiftDown, boolean ctrlDown) {
		if (!ExeData.isLoaded())
			return;
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		updateCamCoords();
		int px = playerPos[0], py = playerPos[1];
		int amount = 32;
		if (shiftDown) {
			if (ctrlDown)
				amount = Math.max(1, (int) (2 / (double) ExeData.getGraphicsResolution()));
			else
				amount = 16;
		} else if (ctrlDown)
			amount = 8;
		if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W)
			py -= amount;
		else if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S)
			py += amount;
		else if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A)
			px -= amount;
		else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D)
			px += amount;
		else
			return;
		px = Math.max(0, Math.min(map[0][0].length * 32, px));
		py = Math.max(0, Math.min(map[0].length * 32, py));
		Short[] pos = new Short[2];
		pos[0] = (short) px;
		pos[1] = (short) py;
		ProfileManager.setField(NormalProfile.FIELD_POSITION, pos);
		updatePlayerPos();
		updateCamCoords();
	}

	@Override
	public void onDrag(int x, int y) {
		if (!ExeData.isLoaded())
			return;
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		playerPos[0] = (short) (x - this.x + camX);
		playerPos[0] = (short) Math.max(0, Math.min(map[0][0].length * 32, playerPos[0]));
		playerPos[1] = (short) (y - 16 - this.y + camY);
		playerPos[1] = (short) Math.max(0, Math.min(map[0].length * 32, playerPos[1]));
		ignoreClick = 2;
	}

	@Override
	public void onDragEnd(int px, int py) {
		if (!ExeData.isLoaded())
			return;
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		ProfileManager.setField(NormalProfile.FIELD_POSITION, playerPos);
		updateCamCoords();
		ignoreClick = 1;
		updateHover(px, py, true);
	}

	@Override
	public void updateHover(int x, int y, boolean hover) {
		super.updateHover(x, y, hover);
		playerHoverX = (short) (x - this.x + camX);
		playerHoverY = (short) (y - this.y + camY);
	}

	@Override
	public void onChange(String field, int id, Object oldValue, Object newValue) {
		Short newShort = 0;
		if (newValue instanceof Short)
			newShort = (Short) newValue;
		switch (field) {
		case NormalProfile.FIELD_POSITION:
			if (newValue instanceof Short[]) {
				playerPos = (Short[]) newValue;
			}
			break;
		case NormalProfile.FIELD_X_POSITION:
			playerPos[0] = newShort;
			break;
		case NormalProfile.FIELD_Y_POSITION:
			playerPos[1] = newShort;
			break;
		default:
			break;
		}
	}

}
