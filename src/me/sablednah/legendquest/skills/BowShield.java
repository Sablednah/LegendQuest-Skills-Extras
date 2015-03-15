package me.sablednah.legendquest.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SkillManifest(name = "BowShield", type = SkillType.ACTIVE, author = "SableDnah", version = 1.1D, 
description = "Reduce Ranged Damage by [reduction]% and/or [soak].", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "reduction","reductionchance","soakchance" }, dblvarvalues = { 100.0,100.0,100.0 }, 
intvarnames = { "override", "soak" }, intvarvalues = { 0, 1 }, 
strvarnames = { "message" }, strvarvalues = { "Shield Activated" }
)
public class BowShield extends Skill implements Listener {

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

		if (data.type == SkillType.PASSIVE) { // does not require command
			return CommandResult.NOTAVAILABLE;
		}

		String message = ((String) data.vars.get("message"));
		p.sendMessage(message);

		return CommandResult.SUCCESS;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void doSoak(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player)) {
			if (!(event.getDamager() instanceof Projectile)) {
				return;
			}

			Player p = (Player) event.getEntity();
			if (!validSkillUser(p)) {
				return;
			}

			// load skill options
			SkillDataStore data = this.getPlayerSkillData(p);
			SkillPhase phase = data.checkPhase();

			if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

				
				double reduction = 1.0D - (((Double) data.vars.get("reduction")) / 100.0D);
				Integer override = ((Integer) data.vars.get("override"));
				Integer soak = ((Integer) data.vars.get("soak"));
	
				double soakchance = ((Double) data.vars.get("soakchance")) / 100.0D;
				double reductionchance = ((Double) data.vars.get("reductionchance")) / 100.0D;

				if (override == 0) {
					if (reduction > 1.0D) {
						reduction = 1.0D;
					}
					if (reduction < 0.0D) {
						reduction = 0.0D;
					}
				}
	
				double dmg = event.getDamage();
				if (Math.random() <= reductionchance) {
					dmg = dmg * reduction;
				}
				if (Math.random() <= soakchance) {
					dmg = dmg - soak;
				}
				if (override == 0) {
					if (dmg < 0) {
						dmg = 0;
					}
				}
				event.setDamage(dmg);
				if (dmg < 0.1) {
					event.setCancelled(true);
				}
			}
		}
	}
}
