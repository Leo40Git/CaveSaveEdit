package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.data.CSData;

public class WarpBox extends DefineBox {

	public WarpBox(int x, int y, int width, int height, int warpId) {
		super(x, y, width, height, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getWarp(warpId).getId();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.getWarp(warpId).setId(t);
				return t;
			}
		}, "Warp", "warp " + warpId);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);
		int warp = vSup.get();
		FrontUtils.drawStringCentered(g, warp + " - " + MCI.get(type, warp), x + width / 2, y + 32);
		if (warp == 0)
			return;
		if (!CSData.isLoaded())
			return;
		g.drawImage(CSData.getStageImage(), x + width / 2 - 32, y + 1, x + width / 2 + 32, y + 33, 64 * warp, 0,
				64 * (warp + 1), 32, null);
	}

}