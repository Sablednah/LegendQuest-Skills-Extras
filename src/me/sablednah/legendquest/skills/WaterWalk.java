package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

// set skill defaults
@SkillManifest(name = "WaterWalk", type = SkillType.PASSIVE, author = "SableDnah", version = 1.2D, 
	description = "Walk on [material], turning it to [newmaterial].", 
	consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 10000, cooldown = 0, 
	dblvarnames = { }, dblvarvalues = { }, 
	intvarnames = { "time" }, intvarvalues = { 0 }, 
	strvarnames = { "message", "material", "newmaterial" }, strvarvalues = { "WaterWalk Activated","WATER,STATIONARY_WATER","ICE"} //ICE ICE BABY
)
public class WaterWalk extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	// this format lets the skill be used activley via /skill<name> or as a passsive skill.
	public CommandResult onCommand(Player p) { // does not require command
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}
		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		if (data.type == SkillType.PASSIVE) { // does not require command so reject
			return CommandResult.NOTAVAILABLE;
		}

		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void doMove(PlayerMoveEvent event) {
		
		// stop if the players block hasn't changed
		if (event.getTo().getBlockX() == event.getFrom().getBlockX() && 
			event.getTo().getBlockY() == event.getFrom().getBlockY() && 
			event.getTo().getBlockZ() == event.getFrom().getBlockZ()) { 
			return; // not changed block! 
		}
		
		//get player
		Player p = (Player) event.getPlayer();
		
		// check if the player has this skill.
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		// get phase
		SkillPhase phase = data.checkPhase();

		// chceck if passive or enabled and in duration
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			//  read in vars: info 
			Integer time = ((Integer) data.vars.get("time"));
			String material = ((String) data.vars.get("material"));
			String newmaterial = ((String) data.vars.get("newmaterial"));
		
			//turn material into an array of material types
			ArrayList<Material> materials = new ArrayList<Material>();
			if (material == null) {
				material = "";
//				materials.add(null);
//				materials.add(Material.AIR);
//			} else {
//				if (material.isEmpty()) {
//					materials.add(null);
//					materials.add(Material.AIR);
//				}
			}
			String[] list = material.split("\\s*,\\s*");
			for (String s : list) {
				Material mat = null;
				try {
					mat = Material.matchMaterial(s);
					materials.add(mat);
				} catch (IllegalArgumentException e) {
					lq.logSevere(s + " is not a valid material for " + data.name);
				}
			}			

			// turn newmaterial string into Material datatype
			Material newmat = null;
			try {
				newmat = Material.matchMaterial(newmaterial);
			} catch (IllegalArgumentException e) {
				lq.logSevere(newmaterial + " is not a valid material for " + data.name);
			}
			
			// start on block bellow feet
			Location l = event.getTo().clone().add(0, -1, 0);

			// making this bigger would increse the radius (well square size)
			int range =1;
			
			// loop through all blocks within radius (but not above)			
			for(int x = l.getBlockX()-range;x<=l.getBlockX()+range;x++) {
				for(int z = l.getBlockZ()-range;z<=l.getBlockZ()+range;z++) {
					for(int y = l.getBlockY()-range;y<=l.getBlockY();y++) {						
	                    Location loc = new Location(l.getWorld(), x, y, z);
	                    // check the block is in the list of types
	                    if (materials.contains(loc.getBlock().getType())) {
	                    	//get the block
	                    	Block b =loc.getBlock();
	                    	// if time is above 0 schedule a task to put it back
							if (time>0) {
								lq.getServer().getScheduler().runTaskLater(lq, new ReplaceMaterial(loc,b.getType()), (time*20));
							}
							// change block to newmaterial
	                        b.setType(newmat);
	                        // if time is >0 set a metadata to not allow breaking till it turns back
	                        if (time>0) {
	                        	b.setMetadata("waterwalk", new FixedMetadataValue(lq, "true"));
	                        }
	                    }
					}
				}				
			}
		}
	}

	// protect block if its been flagged as unbrakable
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		String name = getMetaString(b, "waterwalk");
		if (name != null && name.equalsIgnoreCase("true")) {
			event.setCancelled(true);
		}
	}
	
	// function to reset block type afer interval.
	public class ReplaceMaterial implements Runnable {
		public Location l;
		public Material m;
		public ReplaceMaterial(Location l, Material m){
			this.l=l;
			this.m=m;
		}
		public void run() {
			l.getBlock().setType(m);
			//clear metadata ro allow block to be broken again
			l.getBlock().setMetadata("waterwalk", new FixedMetadataValue(lq, ""));
		}
	}
	
	// utility function to read matadata value.
	public String getMetaString(Metadatable object, String label) {
		List<MetadataValue> values = object.getMetadata(label);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asString();
			}
		}
		return "";
	}

}
