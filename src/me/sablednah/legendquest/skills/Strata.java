package me.sablednah.legendquest.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@SkillManifest(
		name = "Strata", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
		description = "Analyse chunk topology", 
		consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = {}, intvarvalues = {}, 
		strvarnames = {}, strvarvalues = {}
	)

//		super(plugin, "Strata");
//setDescription("Analyse chunk topology");

public class Strata extends Skill implements Listener {

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
//		SkillDataStore data = this.getPlayerSkillData(p);

		Chunk c = player.getLocation().getChunk();

		Map<Material, Integer> loot = new HashMap<Material, Integer>();
		Integer cnt;

		for (int x = 0; x < 16; x++) { // loop through all of the blocks in the chunk that are lower than maxHeight
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < player.getWorld().getMaxHeight(); y++) {
					cnt = loot.get(c.getBlock(x, y, z).getType());
					if (cnt == null) {
						cnt = 0;
					}
					loot.put(c.getBlock(x, y, z).getType(), cnt + 1);
				}
			}
		}

		for (Entry<Material, Integer> entry : loot.entrySet()) {
			Material x = entry.getKey();
			switch (x) {
				case GOLD_ORE:
					player.sendMessage("Found " + entry.getValue() + " Gold.");
					break;
				case IRON_ORE:
					player.sendMessage("Found " + entry.getValue() + " Iron.");
					break;
				case COAL:
					player.sendMessage("Found " + entry.getValue() + " Coal.");
					break;
				case LAPIS_ORE:
					player.sendMessage("Found " + entry.getValue() + " Lapis.");
					break;
				case DIAMOND_ORE:
					player.sendMessage("Found " + entry.getValue() + " Diamond.");
					break;
				case REDSTONE_ORE:
					player.sendMessage("Found " + entry.getValue() + " Redstone.");
					break;
				case EMERALD_ORE:
					player.sendMessage("Found " + entry.getValue() + " Emerald.");
					break;
			}
		}
		return CommandResult.SUCCESS;
	}
}