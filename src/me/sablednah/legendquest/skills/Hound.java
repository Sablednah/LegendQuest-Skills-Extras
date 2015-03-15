package me.sablednah.legendquest.skills;

import java.text.DecimalFormat;
import java.util.Random;

import me.sablednah.legendquest.experience.SetExp;
import me.sablednah.legendquest.playercharacters.PC;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


@SkillManifest(
		name = "Hound", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
		description = "Detailed info on player", 
		consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
		buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
		dblvarnames = {}, dblvarvalues = {}, 
		intvarnames = {"margin"}, intvarvalues = { 50 }, 
		strvarnames = {}, strvarvalues = {}
	)


public class Hound extends Skill implements Listener {

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
		SkillDataStore data = this.getPlayerSkillData(player);

		Integer margin = ((Integer) data.vars.get("margin"));
		String[] args = data.getlastArgs();

/*
		for (String a : args) {
			System.out.print(a);
		}		
*/
	    Player target = null;
	    if (args.length>1){ target = lq.getServer().getPlayer(args[1]);}
	    if (target == null) {
	    	player.sendMessage("Please specify a player");
	      return CommandResult.FAIL;
	    }

	    if (!target.isOnline()) {
	    	player.sendMessage("Please specify an online player");
	      return CommandResult.FAIL;
	    }

	    
	    Random random = new Random();
	    Location location = target.getLocation();
	    int x = location.getBlockX() + random.nextInt(margin);
	    int y = location.getBlockY() + random.nextInt(margin / 10);
	    y = y < 1 ? 1 : y;
	    int z = location.getBlockZ() + random.nextInt(margin);

	    PC pc = getPC(target);
	    
		DecimalFormat df = new DecimalFormat("#.0");

	    player.sendMessage(target.getDisplayName() + " is a "+lq.configLang.statLevelShort+" "+SetExp.getLevelOfXpAmount(pc.currentXP) + " " + pc.race.name + " " + pc.mainClass.name + " " + df.format(pc.getHealth())+"/"+df.format(pc.maxHP)+" "+lq.configLang.statHealth);
	    player.sendMessage("@ aprox: "+location.getWorld().getName()+": "+Integer.valueOf(x)+", "+ Integer.valueOf(y)+", "+Integer.valueOf(z));

	    Location pcLoc = player.getLocation();
	    int pcX = pcLoc.getBlockX();
	    int pcY = pcLoc.getBlockY();
	    int pcZ = pcLoc.getBlockZ();

	    player.sendMessage("You are @ "+location.getWorld().getName()+": "+Integer.valueOf(pcX)+", "+ Integer.valueOf(pcY)+", "+Integer.valueOf(pcZ));

	    player.setCompassTarget(location);
				
		return CommandResult.SUCCESS;
	}
}