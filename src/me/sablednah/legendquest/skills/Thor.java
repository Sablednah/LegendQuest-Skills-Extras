package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.HashSet;

import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

@SkillManifest(
	name = "Thor", type = SkillType.ACTIVE, 
	author = "SableDnah", version = 1.1D, 
	description = "Earthshaking Blow", 
	consumes = "", manaCost = 0, 
	levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 0, 
	dblvarnames = { "damage" }, dblvarvalues = { 5.0 }, 
	intvarnames = { "radius" }, intvarvalues = { 4 }, 
	strvarnames = { "weapons" }, strvarvalues = { "WOOD_SWORD,STONE_SWORD,IRON_SWORD,GOLD_SWORD,DIAMOND_SWORD" }
)

public class Thor extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
//		System.out.print("Mjolnir strikes");

		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}
//		System.out.print("Thor skill: valid player..." + p.getUniqueId() + toString());

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		ArrayList<Material> weapons = new ArrayList<Material>();
		String w = ((String) data.vars.get("weapons"));

		String[] list = w.split("\\s*,\\s*");
		for (String s : list) {
			weapons.add(Material.matchMaterial(s));
		}

		Material handitem = null;
		handitem = p.getItemInHand().getType();

		if (handitem != null) {
			// System.out.print("Vulnerability skill: looking for - " + handitem);
			if ((weapons.contains(handitem))) {
				Double damage = ((Double) data.vars.get("damage"));
				Integer radius = ((Integer) data.vars.get("radius"));

				p.sendMessage("Mighty Mjölnir Strikes!");

				for (Entity e : p.getNearbyEntities(radius, radius, radius)) {
					if (((e instanceof Player)) && (!e.equals(p))) {
						Player pl = (Player) e;
						pl.damage(damage, p);

						Vector v = pl.getVelocity();
						v.setY(v.getY() + 1.5);
						pl.setVelocity(v);

					} else if ((e instanceof Damageable)) {
						Damageable c = (Damageable) e;
						c.damage(damage, p);

						Vector vc = c.getVelocity();
						vc.setY(vc.getY() + 1.5);
						c.setVelocity(vc);
					}
				}

				@SuppressWarnings("deprecation")
				Block block = p.getTargetBlock((HashSet<Byte>) null, 20); // placed block

				if (PluginUtils.canBuild(block, p)) {

					block.getWorld().strikeLightningEffect(block.getLocation());

					ArrayList<BlockState> blocks = new ArrayList<BlockState>();
					for (int x = (radius); x >= (0 - radius); x--) {
						for (int z = (radius); z >= (0 - radius); z--) {
							for (int y = (radius); y >= 0; y--) {
								Block b = block.getRelative(x, y, z);
								if (block.getLocation().distance(b.getLocation()) < radius) {
									BlockState thisstate = b.getState();
									if (b.getType() != Material.AIR  && b.getType() != Material.BEDROCK) {
										if (PluginUtils.canBuild(b, p)) {
											if (Bukkit.getServer().getPluginManager().isPluginEnabled("CreeperHeal")) {
												blocks.add(thisstate);
												PluginUtils.registerBlock(b);
											} else {
												if (thisstate instanceof Chest || thisstate instanceof BrewingStand || thisstate instanceof CreatureSpawner || 
														thisstate instanceof Dispenser || thisstate instanceof DoubleChest || thisstate instanceof Furnace || thisstate instanceof Hopper ||  
														thisstate instanceof Jukebox || thisstate instanceof NoteBlock || thisstate instanceof Sign || thisstate instanceof Dropper) {
													// skip me
												} else {
													blocks.add(thisstate);
													b.setType(Material.AIR);
												}
											}
										}
									}
								}
							}
						}
					}

					for (BlockState bs : blocks) {
						Material m = bs.getType();
						@SuppressWarnings("deprecation")
						byte d = bs.getData().getData();
						int bsX = bs.getX();
						int bsY = bs.getY();
						int bsZ = bs.getZ();

						double depth = (block.getY() + radius) - bsY + 1;
						double speed = .5 + ((1.00D / depth) * 2); // (1.00D/distance)

						Location l = new Location(block.getWorld(), bsX, bsY, bsZ);

						@SuppressWarnings("deprecation")
						FallingBlock fb = p.getWorld().spawnFallingBlock(l, m, d);
						fb.setVelocity(new Vector(0.00D, speed, 0.00D));
						if (Math.random() > 0.95D) {
							fb.setDropItem(true);
						} else {
							fb.setDropItem(false);
						}
						if ((Bukkit.getServer().getPluginManager().isPluginEnabled("CreeperHeal"))) {
							fb.setMetadata("legendquest", new FixedMetadataValue(lq, getName()));
						}
					}
				}
			}
		}
		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void obBlockLand(EntityChangeBlockEvent e) {
		if ((e.isCancelled())) {
			return;
		}
		if (e.getEntityType() == EntityType.FALLING_BLOCK) {
			String name = getMetaString(e.getEntity(), "legendquest");
			if (name.equalsIgnoreCase(getName())) {
				e.setCancelled(true);
			}
		}
	}
}
