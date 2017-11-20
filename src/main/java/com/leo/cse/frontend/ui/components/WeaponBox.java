package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.IProfile.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class WeaponBox extends DefineBox {

	public WeaponBox(int x, int y, int weaponId) {
		super(x, y, 120, 48, new Supplier<Integer>() {
			@Override
			public Integer get() {
				try {
					return (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_ID, weaponId);
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return 0;
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				try {
					ProfileManager.setField(NormalProfile.FIELD_WEAPON_ID, weaponId, t);
					return t;
				} catch (ProfileFieldException e) {
					e.printStackTrace();
				}
				return -1;
			}
		}, "Weapon", "weapon " + (weaponId + 1));
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
		int wep = vSup.get();
		FrontUtils.drawStringCentered(g, wep + " - " + MCI.get(type, wep), x + width / 2, y + 31, false, !bEnabled);
		if (wep == 0)
			return;
		if (!ExeData.isLoaded())
			return;
		int ystart = MCI.getInteger("Game.ArmsImageYStart", 0), size = MCI.getInteger("Game.ArmsImageSize", 32);
		g.drawImage(ExeData.getImage(ExeData.getArmsImage()), x + width / 2 - size / 2, y - (size - 32) + 1,
				x + width / 2 + size / 2, y - (size - 32) + size + 1, wep * size, ystart, (wep + 1) * size,
				ystart + size, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		super.onClick(x, y, shiftDown, ctrlDown);
	}

}
