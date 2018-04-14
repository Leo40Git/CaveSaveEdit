package com.leo.cse.frontend.ui.components.box;

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

public class ItemBox extends DefineBox {

	public static final BufferedImage ITEM_BLANK = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);

	public ItemBox(int x, int y, int width, int height, int itemId) {
		super(x, y, width, height, () -> (Integer) ProfileManager.getField(NormalProfile.FIELD_ITEMS, itemId), t -> {
			ProfileManager.setField(NormalProfile.FIELD_ITEMS, itemId, t);
			return t;
		}, "Item", "item " + (itemId + 1));
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
		int item = vSup.get();
		FrontUtils.drawStringCentered(g, item + " - " + MCI.get(type, item), x + width / 2, y + 31, false, !bEnabled);
		if (item == 0)
			return;
		if (!ExeData.isLoaded())
			return;
		int sourceX = (item % 8) * 64;
		int sourceY = (item / 8) * 32;
		g.drawImage(ExeData.getImage(ExeData.getItemImage()), x + width / 2 - 32, y + 1, x + width / 2 + 32, y + 33,
				sourceX, sourceY, sourceX + 64, sourceY + 32, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (ExeData.isLoaded()) {
			if (iSup == null)
				iSup = t -> {
					if (t == 0)
						return ITEM_BLANK;
					int sourceX = (t % 8) * 64;
					int sourceY = (t / 8) * 32;
					return ExeData.getImage(ExeData.getItemImage()).getSubimage(sourceX, sourceY, 64, 32);
				};
		} else
			iSup = null;
		super.onClick(x, y, shiftDown, ctrlDown);
	}

}
