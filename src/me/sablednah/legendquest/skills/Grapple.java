package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@SkillManifest(
		name = "Grapple", type = SkillType.ACTIVE, 
author = "SableDnah", version = 1.0D, 
description = "Grapple up buildings upto [range] blocks distance", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = { "range", "ammoqtybasic", "ammoqtyadvanced", "unlimited", "teleport" }, intvarvalues = { 10, 5, 1, 0, 1 }, 
strvarnames = { "holding", "ammobasic", "ammoadvanced", "usemessage" }, 
strvarvalues = {"VINE", "String", "Web", "Phwtp!" }
)

public class Grapple extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	@SuppressWarnings("deprecation")
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
					// p.sendMessage("Target is in safe location...");
					return CommandResult.FAIL;
				}

				Integer teleport = ((Integer) data.vars.get("teleport"));
				Integer unlimited = ((Integer) data.vars.get("unlimited"));
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

				// time to vine it up
				try {
					Set<Material> set = null;
					List<Block> blks = p.getLastTwoTargetBlocks(set, range);
					Block blk = null;
					byte by = (byte) 15;

					if (blks != null && blks.size() > 0) {
						blk = blks.get(0);
						BlockFace bf = blk.getFace(blks.get(1));

						switch (bf) {
							case SOUTH:
								by = (byte) (by | 0x1);
							case WEST:
								by = (byte) (by | 0x2);
							case NORTH:
								by = (byte) (by | 0x4);
							case EAST:
								by = (byte) (by | 0x8);
							case DOWN:
								by = (byte) 15;
							case UP:
								by = (byte) 15;
							default:
								by = (byte) 15;
						}
					}
					if (blk != null) {
						// if (Main.canBuild(blk.getLocation(), p)) {
						if (PluginUtils.canBuild(blk.getLocation(), p)) {
							if ((blk.getType() == Material.AIR) || (blk.getType() == Material.SNOW)) {
								blk.setTypeIdAndData(Material.VINE.getId(), by, false);
								p.getLocation().getWorld().playEffect(p.getLocation(), Effect.BOW_FIRE, 0, 32);
								if (teleport>0) {
									Location tloc = blk.getLocation();
									tloc.setYaw(p.getLocation().getYaw());
									tloc.setPitch(p.getLocation().getPitch());
									p.teleport(tloc);
								}
								lq.getServer().getScheduler().scheduleSyncDelayedTask(lq, new Rot(blk), 100L);
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
							p.sendMessage("Sorry not allowed in safe zone!");
						}
					}
				} catch (Exception e) {
					// ignore exceptions
				}
			}
		}
		return CommandResult.SUCCESS;
	}
	public class Rot implements Runnable {
		Block	b;

		public Rot(Block blk) {
			this.b = blk;
		}

		public void run() {
			this.b.setType(Material.AIR);
		}
	}
}
