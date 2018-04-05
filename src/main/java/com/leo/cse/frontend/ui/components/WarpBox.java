package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class WarpBox extends DefineBox {
	
	public static final BufferedImage WARP_BLANK = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);

	public WarpBox(int x, int y, int width, int height, int warpId) {
		super(x, y, width, height, () -> {
			return (Integer) ProfileManager.getField(NormalProfile.FIELD_WARP_ID, warpId);
		}, (Integer t) -> {
			ProfileManager.setField(NormalProfile.FIELD_WARP_ID, warpId, t);
			return t;
		}, "Warp", "warp " + warpId);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		boolean bEnabled = enabled.get();
		if (hover && bEnabled)
			g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		else
			g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		if (!bEnabled) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			FrontUtils.drawCheckeredGrid(g, x + 1, y + 1, width - 1, height - 2);
		}
		g.setColor(Main.lineColor);
		int warp = vSup.get();
		FrontUtils.drawStringCentered(g, warp + " - " + MCI.get(type, warp), x + width / 2, y + 31, false, !bEnabled);
		if (warp == 0)
			return;
		if (!ExeData.isLoaded())
			return;
		g.drawImage(ExeData.getImage(ExeData.getStageImage()), x + width / 2 - 32, y + 1, x + width / 2 + 32, y + 33,
				64 * warp, 0, 64 * (warp + 1), 32, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (ExeData.isLoaded()) {
			if (iSup == null)
				iSup = t -> {
					if (t == 0)
						return WARP_BLANK;
					int sourceX = (t % 8) * 64;
					int sourceY = (t / 8) * 32;
					return ExeData.getImage(ExeData.getStageImage()).getSubimage(sourceX, sourceY, 64, 32);
				};
		} else
			iSup = null;
		super.onClick(x, y, shiftDown, ctrlDown);
	}

}
