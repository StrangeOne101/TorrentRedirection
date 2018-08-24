package com.strangeone101.torrentredirection;

import org.bukkit.plugin.java.JavaPlugin;

public class TorrentRedirection extends JavaPlugin {
	
	public static RedirectionManager MANAGER;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new TorrentListener(), this);
		
		MANAGER = new RedirectionManager();
		MANAGER.runTaskTimer(this, 0, 1); //Start the manager task
		
		getLogger().info("TorrentRedirection ready for take off! (Just kidding. Done setting up.)");
	}

}
