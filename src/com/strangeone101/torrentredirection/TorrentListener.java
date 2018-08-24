package com.strangeone101.torrentredirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.waterbending.Torrent;

public class TorrentListener implements Listener {
	
	public static double REDIRECT_DISTANCE = 5.0;
	public static double REDIRECT_WIDTH = 2.5;
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		if (!e.isSneaking()) return;
		
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(e.getPlayer());
		
		if (bPlayer != null && bPlayer.getBoundAbility() != null && bPlayer.getBoundAbility().getName().equalsIgnoreCase("torrent")) {
			//System.out.println("Got to here0");
			Collection<Torrent> torrents = CoreAbility.getAbilities(e.getPlayer(), Torrent.class); //Get torrents they own
			
			for (Torrent torrent : torrents) {
				if (torrent.isLaunch() || torrent.isFreeze() || torrent.isSettingUp()) continue; //If they are already launched
				
				//They are currently using a torrent that isn't launched, so don't let them try to redirect other torrents
				if (torrent.isFormed() || torrent.isForming()) return; 
			}
			
			//System.out.println("Got to here1");
			
			Vector dir = e.getPlayer().getEyeLocation().getDirection();
			
			for (double d = 0; d < REDIRECT_DISTANCE; d += 2.0) {
				Location loc = e.getPlayer().getEyeLocation().clone().add(dir.clone().normalize().multiply(d));
				
				if (GeneralMethods.isSolid(loc.getBlock())) {
					//System.out.println("Is solid 1");
					return;
				}
				
				Collection<Torrent> foundTorrents = getTorrentWithinRadius(loc, REDIRECT_WIDTH, e.getPlayer());
				
				if (foundTorrents.size() > 0) {
					//System.out.println("Redirecting torrent");
					new RedirectedTorrent(foundTorrents.iterator().next(), e.getPlayer());
					//System.out.println("Created redirected torrent");
					return;
				}
				
				//Test one block ahead as well - since we do things in blocks of 2
				if (GeneralMethods.isSolid(e.getPlayer().getEyeLocation().clone().add(dir.clone().normalize().multiply(d + 1)).getBlock())) {
					//System.out.println("Is solid 2");
					return;
				}
			}
			
		}
	}
	
	/**
	 * Gets all torrent abilities within the specified area
	 * @param loc The location
	 * @param radius The radius to check
	 * @param player The player trying to redirect the torrents
	 * @return
	 */
	public Collection<Torrent> getTorrentWithinRadius(Location loc, double radius, Player player) {
		List<Torrent> foundTorrents = new ArrayList<Torrent>();
		
		for (Torrent torrent : CoreAbility.getAbilities(Torrent.class)) {
			if (torrent.getLocation() == null || torrent.getLocation().getWorld() != player.getEyeLocation().getWorld() 
					|| torrent.getPlayer() == player) continue; //Skip all torrents in different worlds or ones owned by the player
			if (torrent.isSettingUp() || !torrent.isLaunching()) continue; //Skip all torrents that are still spinning
			
			if (torrent.getLocation().distanceSquared(loc) <= radius * radius) { //If torrent is within area that is allowed to be detected
				foundTorrents.add(torrent);
			}
		}
		
		return foundTorrents;
	}

}
