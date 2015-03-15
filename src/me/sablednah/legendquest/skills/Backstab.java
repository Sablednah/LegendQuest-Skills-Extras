package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.events.CombatHitCheck;
import me.sablednah.legendquest.events.CombatModifiers;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(name = "Backstab", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "You have a [chance]% chance to deal [bonus] damage when attacking from behind!", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "chance", "bonus", "sneakbonus" }, dblvarvalues = { 100.00, 1.5D, 2.0D }, 
intvarnames = { "angle" }, intvarvalues = { 90 }, 
strvarnames = { "weapons" }, strvarvalues = { "WOOD_SWORD,STONE_SWORD,IRON_SWORD,GOLD_SWORD,DIAMOND_SWORD" }
)

public class Backstab extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player player) {
		return CommandResult.NOTAVAILABLE;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void hitCheck(CombatHitCheck event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);
			Integer angle = (Integer) data.vars.get("angle");

			if (isBehind(event.getDamager(), event.getVictim(), angle)) {
				int dodge = event.getDodgeChance();
				if (dodge > 20) {
					dodge = dodge + 2;
				} else {
					dodge = 20;
				}
				event.setDodgeChance(dodge);
			}
		}
	}

	@EventHandler
	public void doDmg(CombatModifiers event) {
		// System.out.print("WeaponMaster skill: processing...");
		if ((event.getDamager() instanceof Player)) {
			Player p = (Player) event.getDamager();
			if (!validSkillUser(p)) {
				return;
			}
			// System.out.print("WeaponMaster skill: valid player..." + p.getUniqueId()+toString());

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);

			Integer angle = (Integer) data.vars.get("angle");
			Double bonus = (Double) data.vars.get("bonus");
			Double sneakbonus = (Double) data.vars.get("sneakbonus");
			String w = ((String) data.vars.get("weapons"));

			ArrayList<Material> weapons = new ArrayList<Material>();
//System.out.print("WeaponMaster skill: " + w);
			String[] list = w.split("\\s*,\\s*");
			for (String s : list) {
//System.out.print("WeaponMaster skill: adding - " + s);
				weapons.add(Material.matchMaterial(s));
			}
			if (p.getItemInHand().getType() != null) {
//System.out.print("WeaponMaster skill: looking for - " + p.getItemInHand().getType());
				if ((weapons.contains(p.getItemInHand().getType())) && (event.getVictim() instanceof LivingEntity)) {
					double chance = ((Double) data.vars.get("chance")) / 100.0D;
					if (Math.random() <= chance) {
//System.out.print("WeaponMaster skill: chance- " + chance);
						if (isBehind(event.getDamager(), event.getVictim(), angle)) {
							int dmg = event.getPower();
							if (p.isSneaking()) {
								dmg = (int) (dmg * sneakbonus);
							} else {
								dmg = (int) (dmg * bonus);
							}
							event.setPower(dmg);
							p.sendMessage("Backstab!");
							if (event.getVictim() instanceof Player) {
								((Player) event.getVictim()).sendMessage("Backstabed!");
							}
						}
					}
				}
			}
		}
	}

	public boolean isBehind(Entity damager, Entity victim, int aceptableAngle) {
		double angle = Math.toDegrees(victim.getLocation().getDirection().angle(damager.getLocation().getDirection()));
		angle = Math.abs(angle);
		angle = angle % 360;
//System.out.print("Angle: " + angle);
		if (angle < (aceptableAngle/2)) {
			return true;
		}
		return false;
	}
}