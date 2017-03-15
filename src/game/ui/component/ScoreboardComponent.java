package game.ui.component;

import game.ColorUtil;
import game.Util;
import game.render.Align;
import game.render.Font;
import game.render.FontBank;
import game.render.IRenderer;
import game.world.PlayerScoreboardInfo;
import game.world.Scoreboard;
import org.joml.Vector4f;

public class ScoreboardComponent extends UIComponent {
	private Scoreboard scoreboard;
	
	public ScoreboardComponent(Scoreboard scoreboard) {
		setScoreboard(scoreboard);
	}
	
	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
	
	
	@Override
	public void update(double dt) {
		
	}
	
	@Override
	public void render(IRenderer r) {
		Font f = r.getFontBank().getFont("emulogic.ttf");
		float titleScale = 1.5f;
		float headingScale = 1.0f;
		float entryScale = 1.0f;
		float scoreboardWidth = r.getWidth() - 2*Util.HUD_PADDING;
		// First x stop
		float x1 = r.getWidth()/2 - scoreboardWidth/2;
		// Fourth x stop
		float x4 = r.getWidth()/2 + scoreboardWidth/2 - f.getWidth("Time  ", headingScale);
		// Third x stop
		float x3 = x4 - 90.0f;
		// Second x stop
		float x2 = x3 - 140.0f;
		
		r.drawText(f, "Scoreboard", Align.TM, false, r.getWidth()/2, r.getHeight() - Util.HUD_PADDING, titleScale, ColorUtil.YELLOW);
		float y = r.getHeight() - Util.HUD_PADDING - f.getHeight(titleScale) - 80.0f;
		
		r.drawText(f, "Name", Align.TL, false, x1, y, headingScale, ColorUtil.YELLOW);
		r.drawText(f, "PK", Align.TM, false, x2, y, headingScale, ColorUtil.YELLOW);
		r.drawText(f, "MK", Align.TM, false, x3, y, headingScale, ColorUtil.YELLOW);
		r.drawText(f, "Time", Align.TL, false, x4, y, headingScale, ColorUtil.YELLOW);
		
		y -= f.getHeight(headingScale);
		y -= 40.0f;
		for (PlayerScoreboardInfo p : scoreboard.getPlayers()) {
			Vector4f color = p.dead ? ColorUtil.RED : ColorUtil.GREEN;
			r.drawText(f, p.name, Align.TL, false, x1, y, entryScale, color);
			r.drawText(f, "" + p.playerKills, Align.TM, false, x2, y, entryScale, color);
			r.drawText(f, "" + p.monsterKills, Align.TM, false, x3, y, entryScale, color);
			r.drawText(f, String.format("%.2f", p.survivalTime), Align.TL, false, x4, y, entryScale, color);
			
			y -= f.getHeight(entryScale);
			y -= 30.0f;
		}
	}
}
