package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BlockIterator;

@SkillManifest(
		name = "FlameOn", type = SkillType.ACTIVE, 
author = "SableDnah", version = 1.0D, 
description = "Flaming instigator!  Flamethrower - range: [range], Damage: [damage]", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = {"damage", "damageadvanced" }, dblvarvalues = { 10.0, 15.0 }, 
intvarnames = { "range", "ammoqtybasic", "ammoqtyadvanced", "fire", "unlimited" }, intvarvalues = { 10, 5, 1, 1, 0 }, 
strvarnames = { "holding", "ammobasic", "ammoadvanced", "usemessage" }, 
strvarvalues = {"BLAZE_ROD", "COAL", "FIREBALL", "Flame on!" }
)

public class FlameOn extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command

		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		ArrayList<Material> weapons = new ArrayList<Material>();
		String w = ((String) data.vars.get("holding"));

		String[] list = w.split("\\s*,\\s*");
		for (String s : list) {
			weapons.add(Material.matchMaterial(s));
		}

		Material handitem = null;
		handitem = p.getItemInHand().getType();

		if (handitem != null) {
			// System.out.print("Vulnerability skill: looking for - " + handitem);
			if ((weapons.contains(handitem))) {

				if (!PluginUtils.canBuild(p.getLocation(), p)) {
					p.sendMessage("Target is in safe location...");
					return CommandResult.FAIL;
				}

				Double damage = ((Double) data.vars.get("damage"));
				Double damageadvanced = ((Double) data.vars.get("damageadvanced"));

				Integer unlimited = ((Integer) data.vars.get("unlimited"));
				Integer fire = ((Integer) data.vars.get("fire"));
				Integer range = ((Integer) data.vars.get("range"));
				Integer ammoqtybasic = ((Integer) data.vars.get("ammoqtybasic"));
				Integer ammoqtyadvanced = ((Integer) data.vars.get("ammoqtyadvanced"));

				String usemessage = ((String) data.vars.get("usemessage"));
				String ammobasic = ((String) data.vars.get("ammobasic"));
				String ammoadvanced = ((String) data.vars.get("ammoadvanced"));

				// ok gun and player perms are ok - now to check for ammo.
				boolean hasAmunition = false;
				boolean advancedAmmo = false;

				if (unlimited > 0) {
					hasAmunition = true;
					advancedAmmo = true;
				} else {
					PlayerInventory i = p.getInventory();
					if (i.contains(Material.matchMaterial(ammoadvanced), ammoqtyadvanced)) {
						hasAmunition = true;
						advancedAmmo = true;
					} else if (i.contains(Material.matchMaterial(ammobasic), ammoqtybasic)) {
						hasAmunition = true;
						advancedAmmo = false;
					}
				}
				if (!hasAmunition) {
					p.sendMessage(ChatColor.RED + "Out of Ammo!");
					return CommandResult.FAIL;
				}

				// time to flame it up
				p.getLocation().getWorld().playEffect(p.getLocation(), Effect.BLAZE_SHOOT, 0, 32);

				Entity kebab;
				List<Entity> nearbyE = p.getNearbyEntities(range + 1, range + 1, range + 1);
				BlockIterator iterator = new BlockIterator(p.getWorld(), p.getLocation().toVector(), p.getEyeLocation().getDirection(), 0, range + 1);

				// flames!!
				while (iterator.hasNext()) {
					Block item1 = iterator.next();
					if (PluginUtils.canBuild(item1.getLocation(), p)) {
						item1.getLocation().getWorld().playEffect(item1.getLocation(), Effect.MOBSPAWNER_FLAMES, 20, 32);
						for (Entity entity : nearbyE) {
							int acc = 2;
							for (int x = -acc; x < acc; x++) {
								for (int z = -acc; z < acc; z++) {
									for (int y = -acc; y < acc; y++) {
										if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item1)) {
											kebab = entity;
											if (kebab != null) {
												kebab.setFireTicks(100);
												if (kebab instanceof LivingEntity) {
													Double d = advancedAmmo ? damageadvanced : damage;
													((LivingEntity) kebab).damage(d, p);
												}
											}
										}
									}
								}
							}
						}
					}
				}

				if (fire > 0) {
					Set<Material> set = null;
					Block blk = p.getLastTwoTargetBlocks(set, range).get(0);
					if (blk != null) {
						if (PluginUtils.canBuild(blk.getLocation(), p)) {
							// Block blk = event.getClickedBlock().getRelative(event.getBlockFace());
							if ((blk.getType() == Material.AIR) || (blk.getType() == Material.SNOW)) {
								blk.setType(Material.FIRE);
							}
						}
					}
				}

				if (unlimited < 1) {
					ItemStack cost = null;
					if (advancedAmmo) {
						cost = new ItemStack(Material.matchMaterial(ammoadvanced), ammoqtyadvanced);
					} else {
						cost = new ItemStack(Material.matchMaterial(ammobasic), ammoqtybasic);
					}
					if (cost != null) {
						p.getInventory().removeItem(cost);
					}
					p.updateInventory();
				}

				if (usemessage.length() > 0) { // tell the player Flame On!!
					String msg = usemessage;
					msg = msg.replaceAll("%P", p.getName());
					msg = ChatColor.translateAlternateColorCodes('&', msg);
					p.sendMessage(msg);
				}
			} else {
				p.sendMessage("To use "+data.name+", you need to hold "+w);
			}
		}
		return CommandResult.SUCCESS;
	}
}
