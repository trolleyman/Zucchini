package game.audio;

import java.util.HashMap;
import java.util.Map;

public class SourcesLayout {
	/** A hash map relating Filename->number of sources*/
	public Map<String,Integer> fileSourceMap = new HashMap<>();
	
	//there are a max of 250 sources
	public SourcesLayout(){
		fileSourceMap.put("[bgm]Desolation.wav", 0);
		fileSourceMap.put("bullet_impact_body.wav", 10);
		fileSourceMap.put("bullet_impact_wall.wav", 30);
		fileSourceMap.put("bullet_whiz1.wav", 0);
		fileSourceMap.put("bullet_whiz_silent.wav", 0);
		fileSourceMap.put("bullet_whiz2.wav", 30);
		fileSourceMap.put("bullet_whiz3.wav", 0);
		fileSourceMap.put("explosion.wav", 5);
		fileSourceMap.put("footsteps_running.wav", 50);
		fileSourceMap.put("footsteps_walking.wav", 0);
		fileSourceMap.put("grunt1.wav", 0);
		fileSourceMap.put("grunt2.wav", 5);
		fileSourceMap.put("gun_reload[2sec].wav", 5);
		fileSourceMap.put("handgunshot.wav", 50);
		fileSourceMap.put("punch.wav", 5);
		fileSourceMap.put("rocket_reload.wav", 5);
		fileSourceMap.put("rocket-launcher.wav", 5);
	}
	
	
}
