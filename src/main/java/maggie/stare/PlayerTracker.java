package maggie.stare;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

public class PlayerTracker extends Plugin {
	@Override
	public void onLoad() {
		RusherHackAPI.getModuleManager().registerFeature(new PlayerTrackerModule());
		this.getLogger().info("loaded player tracker");
	}
	@Override
	public void onUnload() {
		this.getLogger().info("player tracker unloaded!");
	}
}
