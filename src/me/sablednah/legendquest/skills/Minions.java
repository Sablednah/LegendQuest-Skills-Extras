package me.sablednah.legendquest.skills;

import java.util.HashSet;
import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.Utils;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@SkillManifest(name = "Minions", type = SkillType.ACTIVE, author = "SableDnah", version = 2.1D, 
description = "Summon [qty]x[type] zombie(s)", 
consumes = "", manaCost = 5, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 100000, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = { "qty" }, intvarvalues = { 1 }, 
strvarnames = { "type" }, strvarvalues = { "generic" }
)
public class Minions extends Skill {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */ }

	public CommandResult onCommand(Player p) { 
		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);
		String type = ((String) data.vars.get("type"));
		Integer qty = ((Integer) data.vars.get("qty"));
		try {
			@SuppressWarnings("deprecation")
			Block block = p.getTargetBlock((HashSet<Byte>) null, 100);
			Location bl = block.getLocation();

		    if (type.equals("any"))
		        type = null;
		      else {
		        type = type + ".yml";
		      }

		      ZombieMod zm = (ZombieMod)lq.getServer().getPluginManager().getPlugin("ZombieMod");
		      PutredineImmortui z = new PutredineImmortui(zm, type);
		      z.owner = p.getName();
		      if (z.owner.charAt(z.owner.length() - 1) == 's')
		        z.commonName = (z.owner + "' " + z.commonName);
		      else {
		        z.commonName = (z.owner + "'s  " + z.commonName);
		      }
			
			for(int i=1; i<=qty; i++){
				//slightly randomise position so they don't do that weird stacking effect!
				Location bl2 = bl.clone();
				double xmod = (Math.random()-0.5D)*(i-1);
				double zmod = (Math.random()-0.5D)*(i-1);
				bl2.setX(bl2.getX()+xmod);
				bl2.setZ(bl2.getZ()+zmod);
				bl2.setY(bl2.getY()+1.1);
				
		      Utils.spawnZombie(z, bl2, zm);
		      bl2.getWorld().playEffect(bl2.add(0.0D, 1.0D, 0.0D), Effect.SMOKE, 3);
			}
		} catch (IllegalArgumentException exp) {
			lq.debug.warning("'"+type + "' is not a valid zombie type for skill 'SumMinionsmon'");
			return CommandResult.FAIL;
		}
		return CommandResult.SUCCESS;
	}

}
