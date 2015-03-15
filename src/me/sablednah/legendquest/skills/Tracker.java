package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;


@SkillManifest(
		name = "Tracker", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
		description = "Track nearby entities", 
		consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = { "radius" }, intvarvalues = { 16 }, 
		strvarnames = {}, strvarvalues = {}
	)


public class Tracker extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	@SuppressWarnings("deprecation")
	public CommandResult onCommand(Player player) { // does not require command

		if (!validSkillUser(player)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(player);

		Integer radius = ((Integer) data.vars.get("radius"));

		Integer cnt;
		Map<EntityType, Integer> loot = new HashMap<EntityType, Integer>();

		List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
		for (Entity e : nearbyEntities) {
			cnt = loot.get(e.getType());
			if (cnt == null) {
				cnt = 0;
			}
			loot.put(e.getType(), cnt + 1);
		}

		for (Map.Entry<EntityType, Integer> entry : loot.entrySet()) {

			// player.sendMessage("Found " + entry.getValue() + " " + entry.getKey() );

			switch (entry.getKey()) {
				case ZOMBIE:
					if (lq.getServer().getPluginManager().isPluginEnabled("ZombieMod")) {
						ZombieMod ZM = (ZombieMod) lq.getServer().getPluginManager().getPlugin("ZombieMod");
						Map<String, Integer> zombs = new HashMap<String, Integer>();
						List<Entity> zombies = player.getNearbyEntities(radius, radius, radius);
						for (Entity zomb : zombies) {
							if ((zomb instanceof Zombie)) {
								String genus = "";
								
								PutredineImmortui z = ZM.getZombie(zomb);
								if (z != null) {
									genus = z.commonName;
								} else {
									genus = "Zombie";
								}
	
								cnt = zombs.get(genus);
								if (cnt == null) {
									cnt = 0;
								}
								zombs.put(genus, cnt + 1);
							}
						}
	
						for (Map.Entry<String, Integer> z : zombs.entrySet()) {
							player.sendMessage("Found " + z.getValue() + " " + z.getKey());						
						}
					} else {
						player.sendMessage("Found " + entry.getValue() + " " + entry.getKey().getName());
					}
					break;
				case PLAYER:
					List<Entity> closeby = player.getNearbyEntities(radius, radius, radius);
					List<String> players = new ArrayList<String>();
					for (Entity p : closeby) {
						if ((p instanceof Player)) {
							Player pl = (Player) p;
							players.add(pl.getDisplayName());
						}
					}
					String joinedResult = StringUtils.join(players, ", ");
					player.sendMessage("Found Players: " + joinedResult);
					break;
				default:
					player.sendMessage("Found " + entry.getValue() + " " + entry.getKey().getName());
					break;
			}
		}

		
		return CommandResult.SUCCESS;
	}
}