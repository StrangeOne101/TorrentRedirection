package com.strangeone101.torrentredirection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Torrent;

public class RedirectedTorrent implements Runnable {

	private List<TempBlock> trail = new ArrayList<TempBlock>();
	private Torrent torrent;
	private Player player;
	private Location goal;
	
	private boolean ended = false;
	private boolean torrentRedirected = false;
	
	public RedirectedTorrent(Torrent torrent, Player player) {
		this.torrent = torrent;
		this.player = player;
		
		List<Block> launchedBlocks = new ArrayList<Block>();
		for (TempBlock tb : torrent.getLaunchedBlocks()) { //Store all blocks in the trail
			launchedBlocks.add(tb.getBlock());
		}
		Location tempLoc = torrent.getLocation();
		torrent.setLocation(null); //Stops water returning to the player when remove() is called
		torrent.remove(); //We are removing it but then creating it back later. This stops progress() from running
		torrent.setLocation(tempLoc); //Restore the locatino as we need it later
		
		for (Block b : launchedBlocks) { //Re-add all the temp blocks removed from torrent with remove()
			trail.add(new TempBlock(b, Material.WATER));
		}
		
		TorrentRedirection.MANAGER.addTorrent(this);
		
		//System.out.println("Done setting up redirected torrent");
		
	}
	
	@Override
	public void run() {
		//System.out.println("Running redirection");
		double startAngle = player.getEyeLocation().getYaw() + 90 + 45; //180 to correct MC's dumb system, +45 for where we want
		
		double x = Math.cos(Math.toRadians(startAngle)) * torrent.getRadius(); //getRadius() is the ring size
		double z = Math.sin(Math.toRadians(startAngle)) * torrent.getRadius(); 
		
		goal = player.getLocation().clone().add(x, 0, z); //Update the goal
		
		boolean movingToTarget = torrent.getLocation().distanceSquared(goal) > 1.5;
		boolean shouldMove = false;
		
		if (movingToTarget) {
			Vector vec = goal.toVector().subtract(torrent.getLocation().toVector());
			Block b = torrent.getLocation().getBlock();
			
			if (vec.length() > 1) vec = vec.normalize();
			
			torrent.getLocation().add(vec);
			//System.out.println("Moving");
			
			if (b != torrent.getLocation().getBlock()) { //The block its in has changed - time for another temp block
				trail.add(0, new TempBlock(torrent.getLocation().getBlock(), Material.WATER));
				shouldMove = true;
				//System.out.println("Adding to trail");
			}
			
		} else if (!torrentRedirected) {
			
			torrent.setStartAngle(startAngle);
			torrent.setPlayer(player);
			torrent.setLaunch(false);
			torrent.setLaunching(false);
			torrent.setFreeze(false);
			torrent.setFormed(false);
			torrent.setForming(true);
			torrent.setTime(System.currentTimeMillis()); //Restart max alive time
			torrent.setSourceSelected(false);
			
			torrent.start();

			torrentRedirected = true; //So this only triggers once
			//System.out.println("Finished redirection");
		}
		
		//If either the torrent has moved along a block OR the trail is meant to dry up as the torrent has reach the end
		if (shouldMove || !movingToTarget) { 
			if (trail.size() == 0) { //No more trail exists
				this.ended = true;
				//System.out.println("Trail ran out");
			} else {
				TempBlock block = trail.get(trail.size() - 1); //Get the end block
				block.revertBlock(); //Revert the last block in the trail
				trail.remove(trail.size() - 1); //Remove from memory
				//System.out.println("Removing trail");
			}
		}	
		
	}
	
	/**
	 * Whether the ghost is ready for garbage collection
	 * @return
	 */
	public boolean hasEnded() {
		return ended;
	}

}
