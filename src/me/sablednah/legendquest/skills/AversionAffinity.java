package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;
import me.sablednah.legendquest.events.SpeedCheckEvent;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@SkillManifest(name = "AversionAffinity", type = SkillType.PASSIVE, author = "SharksterBoy", version = 1.0D, 
description = "Damage when walking on [harmfullmaterials], heal on [healingmaterials]", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "regenerate", "damage","harmspeed","healspeed" }, dblvarvalues = { 1.0,1.0, 0.0,0.0 }, 
intvarnames = { "regeninterval","damageinterval","healbonus","harmpenalty" }, 
intvarvalues = { 5, 5, 1, 1 }, 
strvarnames = { "harmfullmaterials", "healingmaterials" }, strvarvalues = { "SOUL_SAND","GRASS" })
public class AversionAffinity extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command
		return CommandResult.NOTAVAILABLE;
	}
/*
 * /time set <number | day | night>
/time set 0     || Sets the time to dawn.
/time set day   || Sets the time to 1000.
/time set 6000  || Sets the time to midday
/time set 12000 || Sets the time to dusk
/time set night || Sets the time to 14000.
/time set 18000 || Sets the time to midnight
 */

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		
		Block b = p.getLocation().getBlock();
		if (b.getType()==Material.AIR) {
			b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		}
		
		Integer regeninterval = ((Integer) data.vars.get("regeninterval"));
		Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
		
		double regenerate = ((Double) data.vars.get("regenerate"));
		double damage = ((Double) data.vars.get("damage"));

		String[] list =null; 
				
		String harmfullmaterials = ((String) data.vars.get("harmfullmaterials"));
		ArrayList<Material> harmlist = new ArrayList<Material>();
		list = harmfullmaterials.split("\\s*,\\s*");
		for (String s : list) {
			harmlist.add(Material.matchMaterial(s));
		}

		String healingmaterials = ((String) data.vars.get("healingmaterials"));
		ArrayList<Material> heallist = new ArrayList<Material>();
		list = healingmaterials.split("\\s*,\\s*");
		for (String s : list) {
			heallist.add(Material.matchMaterial(s));
		}
		
		if (damage>0.0D) {
			if (harmlist.contains(b.getType())) { // dark
				if ((lq.players.ticks % ((damageinterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
					getPC(p).damage(damage);
				}
			}
		}
		if (regenerate>0.0D) {
			if (heallist.contains(b.getType())) { // dark
				if ((lq.players.ticks % ((regeninterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
					getPC(p).heal(regenerate);
				}
			}
		}
	}
	
	@EventHandler
	public void statcheck(AbilityCheckEvent event){
		if (!validSkillUser(event.getPc())) {
			return;
		}

		Player p = event.getPc().getPlayer();
		if (p == null) { return; }

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPc());
		Integer healbonus = ((Integer) data.vars.get("healbonus"));
		Integer harmpenalty = ((Integer) data.vars.get("harmpenalty"));

		
		Block b = p.getLocation().getBlock();
		if (b.getType()==Material.AIR) {
			b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
		}

		String[] list =null; 
		
		String harmfullmaterials = ((String) data.vars.get("harmfullmaterials"));
		ArrayList<Material> harmlist = new ArrayList<Material>();
		list = harmfullmaterials.split("\\s*,\\s*");
		for (String s : list) {
			harmlist.add(Material.matchMaterial(s));
		}

		String healingmaterials = ((String) data.vars.get("healingmaterials"));
		ArrayList<Material> heallist = new ArrayList<Material>();
		list = healingmaterials.split("\\s*,\\s*");
		for (String s : list) {
			heallist.add(Material.matchMaterial(s));
		}

		if (harmlist.contains(b.getType())) {	
			event.removePenalty(harmpenalty);
		}
		if (heallist.contains(b.getType())) {
			event.addBonus(healbonus);
		}		
	}
	
	@EventHandler (priority = EventPriority.NORMAL)
	public void speedCheck(SpeedCheckEvent event){

		if (!validSkillUser(event.getPC())) {
			return;
		}

		Player p = event.getPC().getPlayer();
		if (p == null) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(event.getPC());
		SkillPhase phase = data.checkPhase();
		
		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {
			
			double boost = ((Double) data.vars.get("healspeed"));
			double slow = ((Double) data.vars.get("harmspeed"));
	
			Block b = p.getLocation().getBlock();
			if (b.getType()==Material.AIR) {
				b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
			}

			String[] list =null; 
			
			String harmfullmaterials = ((String) data.vars.get("harmfullmaterials"));
			ArrayList<Material> harmlist = new ArrayList<Material>();
			list = harmfullmaterials.split("\\s*,\\s*");
			for (String s : list) {
				harmlist.add(Material.matchMaterial(s));
			}

			String healingmaterials = ((String) data.vars.get("healingmaterials"));
			ArrayList<Material> heallist = new ArrayList<Material>();
			list = healingmaterials.split("\\s*,\\s*");
			for (String s : list) {
				heallist.add(Material.matchMaterial(s));
			}
			
			double speed = event.getSpeed();
			if (slow!=0.0D) {
				if (harmlist.contains(b.getType())) { // dark
					speed -= slow;
				}
			}
			if (boost!=0.0D) {
				if (heallist.contains(b.getType())) { // dark
					speed += boost;
				}
			}
			if (speed<0.05D) {
				speed = 0.05D;
			}
			event.setSpeed(speed);
		}
	}

}