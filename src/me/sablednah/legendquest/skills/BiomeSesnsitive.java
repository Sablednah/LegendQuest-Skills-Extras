package me.sablednah.legendquest.skills;

import java.util.ArrayList;

import me.sablednah.legendquest.events.AbilityCheckEvent;
import me.sablednah.legendquest.events.SkillTick;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SkillManifest(name = "BiomeSensitive", type = SkillType.PASSIVE, author = "SableDnah", version = 1.0D, 
description = "Multiple effects dependent on biome type", 
consumes = "", manaCost = 0, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 0, 
dblvarnames = { "regenerate", "damage" }, dblvarvalues = { 1.0,1.0 }, 
intvarnames = { "regeninterval","damageinterval","healbonus","harmpenalty" }, 
intvarvalues = { 5, 5, 1, 1 }, 
strvarnames = { "message", "harmfullbiomes", "healingbiomes" }, strvarvalues = { "Tuning to nature...","HELL","FLOWER_FOREST" })

// see https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html

public class BiomeSesnsitive extends Skill implements Listener {
	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) {
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

	@EventHandler
	public void skillTick(SkillTick event) {
		Player p = event.getPlayer();
		if (!validSkillUser(p)) {
			return;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			Biome b = p.getLocation().getBlock().getBiome();
			
			Integer regeninterval = ((Integer) data.vars.get("regeninterval"));
			Integer damageinterval = ((Integer) data.vars.get("damageinterval"));
			
			double regenerate = ((Double) data.vars.get("regenerate"));
			double damage = ((Double) data.vars.get("damage"));
	
			String[] list =null; 
					
			String harmfullbiomes = ((String) data.vars.get("harmfullbiomes"));
			ArrayList<Biome> harmlist = new ArrayList<Biome>();
			list = harmfullbiomes.split("\\s*,\\s*");
			for (String s : list) {
				try {
					harmlist.add(Biome.valueOf(s));
				} catch (IllegalArgumentException  e) {
					lq.logSevere(s + " is not a valid biome type in skill " + data.name);
				}
			}
	
			String healingbiomes = ((String) data.vars.get("healingbiomes"));
			ArrayList<Biome> heallist = new ArrayList<Biome>();
			list = healingbiomes.split("\\s*,\\s*");
			for (String s : list) {
				try {
					heallist.add(Biome.valueOf(s));
				} catch (IllegalArgumentException  e) {
					lq.logSevere(s + " is not a valid biome type in skill " + data.name);
				}
			}
			
			if (damage>0.0D) {
				if (harmlist.contains(b)) { // dark
					if ((lq.players.ticks % ((damageinterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
						getPC(p).damage(damage);
					}
				}
			}
			if (regenerate>0.0D) {
				if (heallist.contains(b)) { // dark
					if ((lq.players.ticks % ((regeninterval*20)/lq.configMain.skillTickInterval)) == 0 ) {
						getPC(p).heal(regenerate);
					}
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
		
		SkillPhase phase = data.checkPhase();

		if (phase.equals(SkillPhase.ACTIVE) || data.type.equals(SkillType.PASSIVE)) {

			Integer healbonus = ((Integer) data.vars.get("healbonus"));
			Integer harmpenalty = ((Integer) data.vars.get("harmpenalty"));
	
			Biome b = p.getLocation().getBlock().getBiome();
	
			String[] list =null; 
			
			String harmfullbiomes = ((String) data.vars.get("harmfullbiomes"));
			ArrayList<Biome> harmlist = new ArrayList<Biome>();
			list = harmfullbiomes.split("\\s*,\\s*");
			for (String s : list) {
				try {
					harmlist.add(Biome.valueOf(s));
				} catch (IllegalArgumentException  e) {
					lq.logSevere(s + " is not a valid biome type in skill " + data.name);
				}
			}
	
			String healingbiomes = ((String) data.vars.get("healingbiomes"));
			ArrayList<Biome> heallist = new ArrayList<Biome>();
			list = healingbiomes.split("\\s*,\\s*");
			for (String s : list) {
				try {
					heallist.add(Biome.valueOf(s));
				} catch (IllegalArgumentException  e) {
					lq.logSevere(s + " is not a valid biome type in skill " + data.name);
				}
			}
	
			if (harmlist.contains(b)) {	
				event.removePenalty(harmpenalty);
			}
			if (heallist.contains(b)) {
				event.addBonus(healbonus);
			}
		}
	}
}