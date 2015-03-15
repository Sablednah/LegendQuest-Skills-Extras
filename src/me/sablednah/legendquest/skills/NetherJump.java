package me.sablednah.legendquest.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@SkillManifest(
		name = "NetherJump", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
		description = "Teleport to nether (or back)", 
		consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = { "scalenether" }, intvarvalues = { 1 }, 
		strvarnames = { "worldpairs" }, strvarvalues = {"world|world_nether"}
	)
/* world|world_nether,ZARP|ZARP_nether */
public class NetherJump extends Skill implements Listener {

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

		String w = player.getWorld().getName().toLowerCase();
		
		String wp = ((String) data.vars.get("worldpairs"));
		Integer scalenether = ((Integer) data.vars.get("scalenether"));
		
		String[] list = wp.split("\\s*,\\s*");

		for (String s : list) {
			if (lq.configMain.debugMode) { lq.debug.info(s); }
			
			String[] worlds = s.split("\\s*\\|\\s*");
			
			if (lq.configMain.debugMode) { lq.debug.info(worlds[0]+" / "+worlds[1]); }
			
			if (worlds[0].equalsIgnoreCase(w)){
				double y = player.getLocation().getY();
				if (scalenether==1) {
					if (Bukkit.getWorld(worlds[1]).getEnvironment().equals(Environment.NETHER) && (!Bukkit.getWorld(worlds[0]).getEnvironment().equals(Environment.NETHER))) {
						y=y/2;
					} else if (!(Bukkit.getWorld(worlds[1]).getEnvironment().equals(Environment.NETHER)) && (Bukkit.getWorld(worlds[0]).getEnvironment().equals(Environment.NETHER))) {
						y=y*2;
						if (y>Bukkit.getWorld(worlds[1]).getMaxHeight()-1) {y=Bukkit.getWorld(worlds[1]).getMaxHeight()-1;}
					}
				}
				Location loc = new Location(Bukkit.getWorld(worlds[1]), player.getLocation().getX(), y, player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
				player.teleport(loc, TeleportCause.PLUGIN);
				return CommandResult.SUCCESS;
			} else if (worlds[1].equalsIgnoreCase(w)){
				double y = player.getLocation().getY();
				if (scalenether==1) {
					if (Bukkit.getWorld(worlds[0]).getEnvironment().equals(Environment.NETHER) && (!Bukkit.getWorld(worlds[1]).getEnvironment().equals(Environment.NETHER))) {
						y=y/2;
					} else if (!(Bukkit.getWorld(worlds[0]).getEnvironment().equals(Environment.NETHER)) && (Bukkit.getWorld(worlds[1]).getEnvironment().equals(Environment.NETHER))) {
						y=y*2;
						if (y>Bukkit.getWorld(worlds[0]).getMaxHeight()-1) {y=Bukkit.getWorld(worlds[0]).getMaxHeight()-1;}
					}
				}
				Location loc = new Location(Bukkit.getWorld(worlds[0]), player.getLocation().getX(), y, player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
				player.teleport(loc, TeleportCause.PLUGIN);
				return CommandResult.SUCCESS;
			} 
		}		
		return CommandResult.FAIL;
	}
}
