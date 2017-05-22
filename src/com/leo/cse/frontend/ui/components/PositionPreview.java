package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.data.CSData;
import com.leo.cse.frontend.data.MapInfo;

public class PositionPreview extends Component {

	private static final Color COLOR_NULL = new Color(0, 0, 16);

	private Supplier<Integer> mSup;
	private MapInfo mapInfo;
	private int[][][] map;
	private BufferedImage tileset;
	private int setWidth;

	public PositionPreview(int x, int y, Supplier<Integer> mSup) {
		super(x, y, 640, 480);
		this.mSup = mSup;
	}

	@Override
	public void render(Graphics g) {
		if (!CSData.isLoaded()) {
			g.setColor(COLOR_NULL);
			g.fillRect(x, y, width, height);
			g.setColor(Color.white);
			g.setFont(Resources.fontL);
			FrontUtils.drawStringCentered(g, "NO MOD LOADED!", x + width / 2, y + height / 2, true);
			return;
		}
		mapInfo = CSData.getMapInfo(mSup.get());
		map = mapInfo.getMap();
		tileset = CSData.getImg(mapInfo.getTileset());
		setWidth = tileset.getWidth() / 32;
		BufferedImage surf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D sg = (Graphics2D) surf.getGraphics();
		sg.setColor(COLOR_NULL);
		sg.fillRect(0, 0, width, height);
		int camX = Math.max(0, Math.min((map[0][0].length - 21) * 32, Profile.getX() - width / 2));
		int camY = Math.max(0, Math.min((map[0].length - 16) * 32, Profile.getY() - height / 2));
		drawBackground(sg);
		sg.translate(-camX, -camY);
		drawTiles(1, sg);
		drawMyChar(sg);
		drawTiles(2, sg);
		sg.translate(camX, camY);
		final String camCoords = "CameraPos: (" + camX / 32 + "," + camY / 32 + ")",
				camCoords2 = "ExactCPos: (" + camX + "," + camY + ")";
		sg.setFont(Resources.fontS);
		final FontMetrics fm = sg.getFontMetrics();
		sg.setColor(Color.black);
		sg.fillRect(0, 0, Math.max(fm.stringWidth(camCoords), fm.stringWidth(camCoords2)), fm.getHeight() * 2);
		sg.setColor(Color.white);
		sg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		FrontUtils.drawString(sg, camCoords, 0, -4);
		FrontUtils.drawString(sg, camCoords2, 0, -4 + fm.getHeight());
		g.drawImage(surf, x, y, null);
	}

	private void drawBackground(Graphics g) {
		if (mapInfo.getScrollType() == 3 || mapInfo.getScrollType() == 4)
			return;
		BufferedImage bg = CSData.getImg(mapInfo.getBgImage());
		int iw = bg.getWidth(null);
		int ih = bg.getHeight(null);
		for (int x = 0; x < width; x += iw) {
			for (int y = 0; y < height; y += ih) {
				g.drawImage(bg, x, y, iw, ih, null);
			}
		}
	}

	private void drawTiles(int layer, Graphics g) {
		int[][] data = map[layer];
		int xx = 0;
		int yy = 0;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				int xPixel = xx * 32 - 16;
				int yPixel = yy * 32 - 16;
				int tile = data[i][j];
				int sourceX = (tile % setWidth) * 32;
				int sourceY = (tile / setWidth) * 32;
				g.drawImage(tileset, xPixel, yPixel, xPixel + 32, yPixel + 32, sourceX, sourceY, sourceX + 32,
						sourceY + 32, null);
				xx++;
			}
			xx = 0;
			yy++;
		}
	}

	private void drawMyChar(Graphics g) {
		int dir = (Profile.getDirection() == 2 ? 1 : 0);
		long costume = 0;
		if (Defines.getSpecial("VarHack"))
			costume = Profile.getVariable(6);
		if (Defines.getSpecial("MimHack"))
			costume = Profile.getMimCostume();
		else
			costume = (Profile.getEquip(6) ? 1 : 0);
		int xPixel = Profile.getX() - 16;
		int yPixel = Profile.getY() - 16;
		int sourceY = (int) (64 * costume + 32 * dir);
		g.drawImage(CSData.getMyChar(), xPixel, yPixel, xPixel + 32, yPixel + 32, 0, sourceY, 32, sourceY + 32, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {

	}

}
