package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.data.CSData;

public class ItemBox extends DefineBox {

	public ItemBox(int x, int y, int width, int height, int itemId) {
		super(x, y, width, height, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getItem(itemId);
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.setItem(itemId, t);
				return t;
			}
		}, "Item", "item " + (itemId + 1));
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);
		int item = vSup.get();
		FrontUtils.drawStringCentered(g, item + " - " + MCI.get(type, item), x + width / 2, y + 32);
		if (item == 0)
			return;
		if (!CSData.isLoaded())
			return;
		int sourceX = (item % 8) * 64;
		int sourceY = (item / 8) * 32;
		g.drawImage(CSData.getItemImage(), x + width / 2 - 32, y + 1, x + width / 2 + 32, y + 33, sourceX, sourceY,
				sourceX + 64, sourceY + 32, null);
	}

}
