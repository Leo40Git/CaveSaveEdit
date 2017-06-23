package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import com.leo.cse.backend.ExeData;
import com.leo.cse.backend.MapInfo;
import com.leo.cse.backend.MapInfo.PxeEntry;
import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.SaveEditorPanel;

public class MapView extends Component implements IDraggable {

	private static final Color COLOR_NULL = new Color(0, 0, 16);

	private MapInfo mapInfo;
	private int[][][] map;
	private BufferedImage tileset;
	private int setWidth;
	private int camX = 0, camY = 0;
	private int ignoreClick = 0;
	private int lastMap;

	public MapView(int x, int y) {
		super(x, y, 660, 480);
		lastMap = Profile.getMap();
	}

	@Override
	public void render(Graphics g) {
		if (SaveEditorPanel.panel.getLastFocus() == this) {
			g.setColor(Main.lineColor);
			g.drawRect(x - 1, y - 1, 641, height + 1);
		}
		if (!ExeData.isLoaded()) {
			g.setColor(COLOR_NULL);
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.setFont(Resources.fontL);
			FrontUtils.drawStringCentered(g, "NO MOD LOADED!", x + width / 2, y + height / 2, true);
			return;
		}
		mapInfo = ExeData.getMapInfo(Profile.getMap());
		if (mapInfo.hasMissingAssets()) {
			g.setColor(COLOR_NULL);
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.setFont(Resources.fontL);
			FrontUtils.drawStringCentered(g, "MISSING ASSETS!", x + width / 2, y + height / 2, true);
			g.setFont(Resources.font);
			FrontUtils.drawStringCentered(g, "The following assets failed to load:\n" + mapInfo.getMissingAssets(),
					x + width / 2, y + height / 2 + 30, true);
			return;
		}
		map = mapInfo.getMap();
		tileset = ExeData.getImage(mapInfo.getTileset());
		setWidth = tileset.getWidth() / 32;
		BufferedImage surf = new BufferedImage(640, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = (Graphics2D) surf.getGraphics();
		sg.setColor(COLOR_NULL);
		sg.fillRect(0, 0, 640, height);
		drawBackground(sg);
		if (Profile.getMap() != lastMap || ignoreClick == 0)
			getCamCoords();
		lastMap = Profile.getMap();
		sg.translate(-camX, -camY);
		drawTiles(sg, 1);
		drawTiles(sg, 2);
		drawEntities(sg);
		drawMyChar(sg);
		sg.translate(camX, camY);
		final String camCoords = "CameraPos:\n(" + camX / 32 + "," + camY / 32 + ")\nExactCPos:\n("
				+ (int) (camX / 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)) + ","
				+ (int) (camY / 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)) + ")";
		final String instruct = "Move player by\nclicking/dragging\nOR\nwith WASD/arrow keys";
		final String mod = "Mod key effects:\nNone - 1 tile\nShift - 1/2 tile\nCtrl - 1/4 tile\nCtrl+Shift - 1 pixel";
		final String click = "Click on the\nhighlighted bit\nto focus on the\nmap view without\nchanging your\nposition!";
		g.setFont(Resources.fontS);
		g.setColor(Main.lineColor);
		FrontUtils.drawString(g, camCoords + "\n\n" + instruct + "\n" + mod + "\n\n" + click, x + 642, y);
		Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 63);
		g.setColor(lc2);
		g.fillRect(x + 640, y, 20, height);
		g.drawImage(surf, x, y, null);
	}

	private void drawBackground(Graphics g) {
		if (mapInfo.getScrollType() == 3 || mapInfo.getScrollType() == 4)
			return;
		BufferedImage bg = ExeData.getImage(mapInfo.getBgImage());
		int iw = bg.getWidth(null);
		int ih = bg.getHeight(null);
		for (int x = 0; x < 640; x += iw) {
			for (int y = 0; y < height; y += ih) {
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
				if (mapInfo.calcPxa(tile) == 0x43) {
					// draw breakable tile
					g.drawImage(ExeData.getImage(ExeData.getNpcSym()), xPixel, yPixel, xPixel + 32, yPixel + 32, 512,
							96, 544, 128, null);
				} else {
					// draw normal tile
					int sourceX = (tile % setWidth) * 32;
					int sourceY = (tile / setWidth) * 32;
					g.drawImage(tileset, xPixel, yPixel, xPixel + 32, yPixel + 32, sourceX, sourceY, sourceX + 32,
							sourceY + 32, null);
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
		while (it.hasNext())
			it.next().draw(g);
	}

	private void drawMyChar(Graphics g) {
		double snap = Math.max(1, 2 / (double) MCI.getInteger("Game.GraphicsResolution", 1));
		int dir = (Profile.getDirection() == 2 ? 1 : 0);
		long costume = 0;
		if (MCI.getSpecial("VarHack"))
			costume = Profile.getVariable(6);
		if (MCI.getSpecial("MimHack"))
			costume = Profile.getMimCostume();
		else
			costume = (Profile.getEquip(6) ? 1 : 0);
		int xPixel = Profile.getX() - 16;
		xPixel /= snap;
		xPixel *= 2;
		int yPixel = Profile.getY() - 16;
		yPixel /= snap;
		yPixel *= 2;
		int sourceY = (int) (64 * costume + 32 * dir);
		g.drawImage(ExeData.getImage(ExeData.getMyChar()), xPixel, yPixel, xPixel + 32, yPixel + 32, 0, sourceY, 32,
				sourceY + 32, null);
	}

	public void getCamCoords() {
		camX = Math.max(0, Math.min((map[0][0].length - 21) * 32, Profile.getX() - width / 2));
		camY = Math.max(0, Math.min((map[0].length - 16) * 32, Profile.getY() - height / 2));
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!ExeData.isLoaded())
			return false;
		if (mapInfo == null)
			return false;
		if (mapInfo.hasMissingAssets())
			return false;
		if (x > this.x + 640)
			return false;
		getCamCoords();
		if (ignoreClick > 0)
			ignoreClick--;
		else {
			Profile.setX((short) (x - this.x + camX));
			Profile.setY((short) (y - this.y + camY));
		}
		return false;
	}

	@Override
	public void onKey(int code, boolean shiftDown, boolean ctrlDown) {
		if (!ExeData.isLoaded())
			return;
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		int px = Profile.getX(), py = Profile.getY();
		int amount = 32;
		if (shiftDown) {
			if (ctrlDown)
				amount = Math.max(1, (int) (2 / (double) MCI.getInteger("Game.GraphicsResolution", 1)));
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
		Profile.setX((short) px);
		Profile.setY((short) py);
		getCamCoords();
	}

	@Override
	public void onDrag(int x, int y) {
		if (!ExeData.isLoaded())
			return;
		if (mapInfo == null)
			return;
		if (mapInfo.hasMissingAssets())
			return;
		Profile.setX((short) (x - this.x + camX));
		Profile.setY((short) (y - 16 - this.y + camY));
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
		getCamCoords();
		ignoreClick = 1;
	}

}
