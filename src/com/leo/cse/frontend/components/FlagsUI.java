package com.leo.cse.frontend.components;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.Defines;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class FlagsUI extends Component {

	private static final int FLAGS_PER_PAGE = 43;

	private int page = 0;
	private Map<Integer, String> flags;
	private List<Map.Entry<Integer, String>> flagList;

	private void getFlags() {
		if (flags == null)
			flags = FrontUtils.sortMapByKey(Defines.getAll("Flag"));
		if (flagList == null)
			flagList = new ArrayList<Map.Entry<Integer, String>>(flags.entrySet());
	}

	public FlagsUI() {
		super(0, 0, Main.WINDOW_SIZE.width, Main.WINDOW_SIZE.height - 33);
	}

	@Override
	public void render(Graphics g) {
		getFlags();
		int yy = 10;
		for (int i = page * FLAGS_PER_PAGE; i < Math.min(flagList.size(), (page + 1) * FLAGS_PER_PAGE); i++) {
			final Map.Entry<Integer, String> entry = flagList.get(i);
			g.drawImage((Profile.getFlag(entry.getKey()) ? Resources.checkboxOffS : Resources.checkboxOnS), 4, yy - 8,
					null);
			g.drawString(FrontUtils.padLeft(Integer.toString(entry.getKey()), "0", 4), 18, yy);
			g.setFont(Resources.fontS);
			g.drawString(entry.getValue(), 44, yy);
			g.setFont(Resources.font);
			yy += 10;
		}
		g.drawLine(0, Main.WINDOW_SIZE.height - 82, 150, Main.WINDOW_SIZE.height - 82);
		g.drawLine(150, Main.WINDOW_SIZE.height - 82, 150, Main.WINDOW_SIZE.height);
		g.drawLine(0, Main.WINDOW_SIZE.height - 64, 150, Main.WINDOW_SIZE.height - 64);
		g.drawLine(0, Main.WINDOW_SIZE.height - 82, 0, Main.WINDOW_SIZE.height);
		g.drawLine(Main.WINDOW_SIZE.width - 150, Main.WINDOW_SIZE.height - 82, Main.WINDOW_SIZE.width,
				Main.WINDOW_SIZE.height - 82);
		g.drawLine(Main.WINDOW_SIZE.width - 150, Main.WINDOW_SIZE.height - 82, Main.WINDOW_SIZE.width - 150,
				Main.WINDOW_SIZE.height);
		g.drawLine(Main.WINDOW_SIZE.width - 150, Main.WINDOW_SIZE.height - 64, Main.WINDOW_SIZE.width,
				Main.WINDOW_SIZE.height - 64);
		g.drawLine(Main.WINDOW_SIZE.width - 7, Main.WINDOW_SIZE.height - 82, Main.WINDOW_SIZE.width - 7,
				Main.WINDOW_SIZE.height);
		FrontUtils.drawString(g, "<<", 75 - (int) g.getFontMetrics().getStringBounds("<<", g).getWidth() / 2,
				Main.WINDOW_SIZE.height - 82);
		FrontUtils.drawString(g, ">>",
				Main.WINDOW_SIZE.width - 75 - (int) g.getFontMetrics().getStringBounds(">>", g).getWidth() / 2,
				Main.WINDOW_SIZE.height - 82);
		final String pageStr = "PAGE " + (page + 1) + "/"
				+ ((flagList.size() / FLAGS_PER_PAGE - (flagList.size() % FLAGS_PER_PAGE < FLAGS_PER_PAGE / 2 ? 1 : 0))
						+ 1);
		FrontUtils.drawString(g, pageStr,
				Main.WINDOW_SIZE.width / 2 - (int) g.getFontMetrics().getStringBounds(pageStr, g).getWidth() / 2,
				Main.WINDOW_SIZE.height - 82);
	}

	@Override
	public void onClick(int x, int y) {
		getFlags();
		if (y >= Main.WINDOW_SIZE.height - 82) {
			final int pageCount = flagList.size() / FLAGS_PER_PAGE
					- (flagList.size() % FLAGS_PER_PAGE < FLAGS_PER_PAGE / 2 ? 1 : 0);
			if (x <= 150) {
				page--;
				if (page < 0)
					page = pageCount;
			} else if (x >= Main.WINDOW_SIZE.width - 150) {
				page++;
				if (page > pageCount)
					page = 0;
			}
		} else {
			int yy = 10;
			for (Map.Entry<Integer, String> entry : flags.entrySet()) {
				if (FrontUtils.pointInRectangle(x, y, 4, yy - 8, 8, 8)) {
					Profile.setFlag(entry.getKey(), !Profile.getFlag(entry.getKey()));
					Main.window.repaint();
					break;
				}
				yy += 10;
			}
		}
	}

}
