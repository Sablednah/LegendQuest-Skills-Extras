package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


@SkillManifest(
		name = "Hunt", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
		description = "Teleport within [margin] distance of target player", 
		consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = {"margin"}, intvarvalues = { 96 }, 
		strvarnames = {"worldblacklist"}, strvarvalues = {"creative"}
	)


public class Hunt extends Skill implements Listener {

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

		Integer margin = ((Integer) data.vars.get("margin"));
		String[] args = data.getlastArgs();
/*
		for (String a : args) {
			System.out.print(a);
		}		
*/
	    Player target = null;
	    if (args.length>1){ target = lq.getServer().getPlayer(args[1]);}
	    if (target == null) {
	    	player.sendMessage("Please specify a player");
	      return CommandResult.FAIL;
	    }

	    if (!target.isOnline()) {
	    	player.sendMessage("Please specify an online player");
	      return CommandResult.FAIL;
	    }
	    
	    List<String> disabledWorlds = new ArrayList<String>();
		String w = ((String) data.vars.get("worldblacklist"));

		String[] list = w.split("\\s*,\\s*");
		for (String s : list) {
			disabledWorlds.add(s.toLowerCase());
		}
	    
	    if (disabledWorlds.contains(target.getWorld().getName().toLowerCase())) {
	    	player.sendMessage(target.getWorld().getName()+" is not a valid destination");
	      return CommandResult.FAIL;
	    }

	    Location location = target.getLocation();

	    margin = margin > 0 ? margin : 0;
	    int xRadius = (int)(Math.random() * margin);
	    if (Math.random() > 0.5D) {
	      xRadius *= -1;
	    }
	    int x = location.getBlockX() + xRadius;

	    int zRadius = (int)Math.sqrt(margin * margin - xRadius * xRadius);
	    if (Math.random() > 0.5D) {
	      zRadius *= -1;
	    }
	    int z = location.getBlockZ() + zRadius;
	    player.getPlayer().teleport(location.getWorld().getHighestBlockAt(x, z).getLocation());

	    List<Entity> nearbyEntities = target.getNearbyEntities((margin/2.0D), (margin/2.0D) , (margin/2.0D));
	    for (Entity e : nearbyEntities) {
	      if ((e instanceof Player)) {
	        ((Player) e).sendMessage("Hunter has teleported nearby..");
	      }
	    }

	    player.setCompassTarget(location);
				
		return CommandResult.SUCCESS;
	}
}