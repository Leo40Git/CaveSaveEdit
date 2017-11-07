package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class ItemBox extends DefineBox {

	public ItemBox(int x, int y, int width, int height, int itemId) {
		super(x, y, width, height, new Supplier<Integer>() {
			@Override
			public Integer get() {
				try {
					return (Integer) ProfileManager.getField(NormalProfile.FIELD_ITEMS, itemId);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_ITEMS, itemId, t);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "Item", "item " + (itemId + 1));
	}

	@Override
	public void render(Graphics g) {
		if (hover)
			g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		else
			g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		int item = vSup.get();
		FrontUtils.drawStringCentered(g, item + " - " + MCI.get(type, item), x + width / 2, y + 31, false);
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
		super.onClick(x, y, shiftDown, ctrlDown);
	}

}
