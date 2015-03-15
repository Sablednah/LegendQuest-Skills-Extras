package me.sablednah.legendquest.skills;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Vector;

@SkillManifest(name = "Fireworks", type = SkillType.ACTIVE, author = "SableDnah", version = 1.0D, 
description = "Launch a firework", 
consumes = "", manaCost = 10, levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 0, cooldown = 10000, 
dblvarnames = { "damage" }, dblvarvalues = { 5.0 }, 
intvarnames = { "power", "flicker", "trail" , "colour1", "colour2" }, intvarvalues = { 1, 1, 1, 13, 3 }, 
strvarnames = { "type" }, strvarvalues = { "CREEPER" }
)
public class Fireworks extends Skill implements Listener {

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

		// System.out.print("data: "+getName()+" | "+data.aliasedname+" | "+data.name + " | " +data.description );
		// System.out.print("vars:" + data.vars.toString());

		String fireworktype = ((String) data.vars.get("type"));
		Integer power = ((Integer) data.vars.get("power"));
		boolean flicker = (((Integer) data.vars.get("flicker")) > 0);
		boolean trail = (((Integer) data.vars.get("trail")) > 0);
		Double damage = ((Double) data.vars.get("damage"));

		Color colour1 = getColor(((Integer) data.vars.get("colour1")));
		Color colour2 = getColor(((Integer) data.vars.get("colour2")));

		Type type = Type.valueOf(fireworktype);  // BALL , BALL_LARGE , BURST ,  CREEPER , STAR
		
		Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();       
        FireworkEffect effect = FireworkEffect.builder().flicker(flicker).withColor(colour1).withFade(colour2).with(type).trail(trail).build();
        fwm.addEffect(effect);
        fwm.setPower(power);
        fw.setFireworkMeta(fwm);
        
        Vector direction = p.getLocation().getDirection().normalize().multiply(0.5);
        fw.setVelocity(direction);

		fw.setMetadata("damage", new FixedMetadataValue(lq, damage));
		fw.setMetadata("skillname", new FixedMetadataValue(lq, getName()));

		return CommandResult.SUCCESS;
	}

	@EventHandler
	public void impact(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Firework) {
			Double dmg = getMetaDamage(event.getDamager());
			String name = getMetaSkillname(event.getDamager());
			if (name.equalsIgnoreCase(getName())) {
				double damage = event.getDamage();
				event.setDamage(damage + dmg);
			}
		}
	}

	public Double getMetaDamage(Metadatable object) {
		List<MetadataValue> values = object.getMetadata("damage");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asDouble();
			}
		}
		return 0.0D;
	}

	public String getMetaSkillname(Metadatable object) {
		List<MetadataValue> values = object.getMetadata("skillname");
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asString();
			}
		}
		return "";
	}

	public Color getColor(int i) {
		Color c = null;
		if(i==1){		c=Color.AQUA;		}
		if(i==2){		c=Color.BLACK;		}
		if(i==3){		c=Color.BLUE;		}
		if(i==4){		c=Color.FUCHSIA;	}
		if(i==5){		c=Color.GRAY;		}
		if(i==6){		c=Color.GREEN;		}
		if(i==7){		c=Color.LIME;		}
		if(i==8){		c=Color.MAROON;		}
		if(i==9){		c=Color.NAVY;		}
		if(i==10){		c=Color.OLIVE;		}
		if(i==11){		c=Color.ORANGE;		}
		if(i==12){		c=Color.PURPLE;		}
		if(i==13){		c=Color.RED;		}
		if(i==14){		c=Color.SILVER;		}
		if(i==15){		c=Color.TEAL;		}
		if(i==16){		c=Color.WHITE;		}
		if(i==17){		c=Color.YELLOW;		}
		 
		return c;
	}
}
