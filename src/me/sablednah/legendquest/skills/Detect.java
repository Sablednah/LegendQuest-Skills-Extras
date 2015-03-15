package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@SkillManifest(name = "Detect", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Detect ores in facing direction", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = {}, dblvarvalues = {}, 
intvarnames = { "range" }, intvarvalues = { 30 }, 
strvarnames = { "ores" }, strvarvalues = { "DIAMOND_ORE,EMERALD_ORE" }
)
// super(plugin, "Strata");
// setDescription("Analyse chunk topology");
public class Detect extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player player) { // does not require command

		if (!validSkillUser(player)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(player);

		Integer range = ((Integer) data.vars.get("range"));

		ArrayList<Material> ores = new ArrayList<Material>();
		String ore = ((String) data.vars.get("ores"));

		String[] list = ore.split("\\s*,\\s*");
		for (String s : list) {
			ores.add(Material.matchMaterial(s));
		}
		String message = null;
		BlockFace wTargetDirection = getPlayerDirection(player);
		Block wTargetBlock = player.getLocation().getBlock().getRelative(BlockFace.UP);
		if (wTargetDirection == BlockFace.NORTH || wTargetDirection == BlockFace.EAST || wTargetDirection == BlockFace.SOUTH || wTargetDirection == BlockFace.WEST) {
			HashMap<Integer, Material> diamondlocations = new HashMap<Integer, Material>();
			for (int i = 0; i < range; i++) {
				wTargetBlock = wTargetBlock.getRelative(wTargetDirection);
				if (ores.contains(wTargetBlock.getType())) {
					diamondlocations.put(Integer.valueOf(i), wTargetBlock.getType());
				}
			}
			if (diamondlocations.size() > 0) {
				message = "Ores found " + (wTargetDirection.toString().toLowerCase()) + ": ";
				for (Entry<Integer, Material> loc : diamondlocations.entrySet()) {
					message += loc.getValue().name().toLowerCase().replace("_", " ") + ":"+ loc.getKey()  + " ";
				}
			} else {
				message = "No ores found within " + range + " blocks " + (wTargetDirection.toString().toLowerCase()) + " of you (eye level)";
			}
			player.sendMessage(message);
		} else {
			player.sendMessage(ChatColor.RED + "Please face north east south or west");
			return CommandResult.FAIL;
		}
		return CommandResult.SUCCESS;
	}

	public static BlockFace getPlayerDirection(Player player) {
		Location l = player.getEyeLocation();
//        System.out.print("eye:"+l.getYaw());
//       System.out.print("loc:"+player.getLocation().getYaw());
	        double rot = (l.getYaw()) % 360;
	        if (rot < 0) {
	            rot += 360.0;
	        }	        
//	        System.out.print("rot:"+rot);
	        return getDirection(rot);
	   }
	    
	   private static BlockFace getDirection(double rot) {
	        if (0 <= rot && rot < 45.0D) {
	            return BlockFace.SOUTH;
	        } else if (45.0D <= rot && rot < 135.0D) {
	            return BlockFace.WEST;
	        } else if (135.0D <= rot && rot < 225.0D) {
	            return BlockFace.NORTH;
	        } else if (225.0D <= rot && rot < 315.0D) {
	            return BlockFace.EAST;
	        } else if (315.0D <= rot && rot < 360.1D) {
	            return BlockFace.SOUTH;
	        } else {
	            return null;
	        }
	   }
	   
	
}