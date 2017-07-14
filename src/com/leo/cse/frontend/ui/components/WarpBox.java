package com.leo.cse.frontend.ui.components;

//import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class WarpBox extends DefineBox {

	// private int id;

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
		// id = warpId;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		/*
		if (id != 0 && Profile.getWarp(id - 1).getId() == 0) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			g.fillRect(x, y, width, height - 1);
			return;
		}
		*/
		int warp = vSup.get();
		FrontUtils.drawStringCentered(g, warp + " - " + MCI.get(type, warp), x + width / 2, y + 31);
		if (warp == 0)
			return;
		if (!ExeData.isLoaded())
			return;
		g.drawImage(ExeData.getImage(ExeData.getStageImage()), x + width / 2 - 32, y + 1, x + width / 2 + 32, y + 33,
				64 * warp, 0, 64 * (warp + 1), 32, null);
	}

	@Override
	public boolean onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		/*
		if (id != 0 && Profile.getWarp(id - 1).getId() == 0)
			return false;
		*/
		return super.onClick(x, y, shiftDown, ctrlDown);
	}

}
