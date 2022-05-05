package com.strangeone101.torrentredirection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.scheduler.BukkitRunnable;

public class RedirectionManager extends BukkitRunnable {
	
	private List<RedirectedTorrent> redirectedTorrents = new CopyOnWriteArrayList<RedirectedTorrent>();

	@Override
	public void run() {
		for (RedirectedTorrent torrent : redirectedTorrents) {
			torrent.run();
			
			if (torrent.hasEnded()) {
				redirectedTorrents.remove(torrent);
			}
		}
	}
	
	/**
	 * Load in a redirected torrent ready for progressing
	 * @param torrent
	 */
	public void addTorrent(RedirectedTorrent torrent) {
		redirectedTorrents.add(torrent);
	}
}
