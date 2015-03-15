package me.sablednah.legendquest.skills;

import java.util.List;

import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.EntityEnderSignal;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@SkillManifest(
	name = "Scry", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
	description = "Use Eye of Ender to find player", 
	consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
	buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
	dblvarnames = {}, dblvarvalues = {}, 
	intvarnames = {}, intvarvalues = {}, 
	strvarnames = {}, strvarvalues = {}
)

public class Scry extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player player) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();

		// cancel ender eye signal and create artificial one pointing to closest player
		if (item != null && item.getType() == Material.EYE_OF_ENDER) {
			if (event.getAction() != null && (event.getAction() == Action.RIGHT_CLICK_AIR)) {
				event.setCancelled(true);
				Player p = event.getPlayer();

				if (!validSkillUser(p)) {
					return;
				}

				// load skill options
				// SkillDataStore data = this.getPlayerSkillData(p);
				Location l = p.getEyeLocation();

				List<Player> targets = p.getWorld().getPlayers();
				Location closest = null;
				double closestDist = 32768.00;
				for (Player target : targets) {
					if (target.isOnline() && target != p) {
						if (target.getEyeLocation().distanceSquared(l) < closestDist) {
							closestDist = target.getEyeLocation().distance(l);
							closest = target.getEyeLocation();
						}
					}
				}

				if (closest != null) {
					System.out.print("Closest: " + closest.getBlockX() + " , " + closest.getBlockY() + " , " + closest.getBlockZ());

					ItemStack ep = new ItemStack(Material.EYE_OF_ENDER, 1);
					p.getInventory().remove(ep);
					net.minecraft.server.v1_8_R2.World paramWorld = ((CraftWorld) p.getWorld()).getHandle();
					EntityEnderSignal localEntityEnderSignal = new EntityEnderSignal(paramWorld, l.getX(), l.getY(), l.getZ());
					// localEntityEnderSignal.a(closest.getChunk().getX(), (int) closest.getY() / 16,
					// closest.getChunk().getZ());
					BlockPosition bp = new BlockPosition(closest.getX(), closest.getBlockY(), closest.getZ());
					localEntityEnderSignal.a(bp);
					paramWorld.addEntity(localEntityEnderSignal);
					l.getWorld().playEffect(l, Effect.BOW_FIRE, 0);
				}
			}
		}
	}
}