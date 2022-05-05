package com.strangeone101.torrentredirection;

import com.projectkorra.projectkorra.configuration.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TorrentRedirection extends JavaPlugin {
	
	public static RedirectionManager MANAGER;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new TorrentListener(), this);

		ConfigManager.defaultConfig.get().addDefault("Abilities.Water.Torrent.RedirectDistance", 5.0);
		ConfigManager.defaultConfig.get().addDefault("Abilities.Water.Torrent.RedirectWidth", 2.5);
		ConfigManager.defaultConfig.save();

		MANAGER = new RedirectionManager();
		MANAGER.runTaskTimer(this, 0, 1); //Start the manager task
		
		getLogger().info("TorrentRedirection ready for take off! (Just kidding. Done setting up.)");
	}

}
