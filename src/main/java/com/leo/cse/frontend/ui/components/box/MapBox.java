package com.leo.cse.frontend.ui.components.box;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.MapInfo;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class MapBox extends DefineBox {

	private Map<Integer, String> map;

	public MapBox(int x, int y, int width, int height, Supplier<Integer> vSup, Function<Integer, Integer> update,
			Supplier<Boolean> sSup) {
		super(x, y, width, height, vSup, update, sSup, "Map", "map");
	}

	public MapBox(int x, int y, int width, int height, Supplier<Boolean> sSup) {
		this(x, y, width, height, () -> {
			return (Integer) ProfileManager.getField(NormalProfile.FIELD_MAP);
		}, (Integer t) -> {
			ProfileManager.setField(NormalProfile.FIELD_MAP, t);
			return t;
		}, sSup);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		loadMap();
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
		FrontUtils.drawString(g, map.get(vSup.get()), x + 3, y - 1, !bEnabled);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		String nVal = FrontUtils.showSelectionDialog(Main.window, "Select " + description, map.values(),
				map.get(vSup.get()));
		if (nVal == null)
			return;
		Integer i = FrontUtils.getKey(map, nVal);
		if (i == null) {
			JOptionPane.showMessageDialog(Main.window, "Value \"" + nVal + "\" is unknown!", "Unknown value",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		update.apply(i);
	}

	private void loadMap() {
		map = new HashMap<>();
		if (ExeData.isLoaded()) {
			for (int i = 0; i < ExeData.getMapInfoCount(); i++) {
				MapInfo mi = ExeData.getMapInfo(i);
				map.put(i, mi.getFileName() + " - " + mi.getMapName());
			}
		} else {
			Map<Integer, String> temp = MCI.getAll(type);
			for (Map.Entry<Integer, String> entry : temp.entrySet())
				map.put(entry.getKey(), entry.getKey() + " - " + entry.getValue());
		}
		if (sSup.get())
			map = FrontUtils.sortStringMapByValue(map, t -> {
				int i = t.lastIndexOf('-');
				if (i < 0)
					return t;
				return t.substring(i + 1, t.length() - 1);
			});
		for (Map.Entry<Integer, String> entry : map.entrySet())
			entry.setValue(entry.getKey() + " - " + entry.getValue());
	}

}
