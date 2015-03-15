package me.sablednah.legendquest.skills;

import java.util.ArrayList;
import java.util.List;

import me.sablednah.legendquest.utils.plugins.PluginUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

@SkillManifest(
name = "MaterialShield", type = SkillType.ACTIVE, 
author = "SableDnah", version = 1.0D, 
description = "Shield of [material]", 
consumes = "", manaCost = 0, 
levelRequired = 0, skillPoints = 0, 
buildup = 0, delay = 0, duration = 15000, cooldown = 30000, 
dblvarnames = { }, dblvarvalues = { }, 
intvarnames = { "radius", "hollow" }, intvarvalues = { 5, 1 }, 
strvarnames = { "material", "replace" }, strvarvalues = {"GLASS", "AIR,LONG_GRASS,DEAD_BUSH" }
)

public class MaterialShield extends Skill implements Listener {

	public boolean onEnable() {
		return true;
	}

	public void onDisable() { /* nothing to do */
	}

	public CommandResult onCommand(Player p) { // does not require command

		if (!validSkillUser(p)) {
			return CommandResult.FAIL;
		}
		if (!PluginUtils.canBuild(p.getLocation(), p)) {
			p.sendMessage("Target is in safe location...");
			return CommandResult.FAIL;
		}

		// load skill options
		SkillDataStore data = this.getPlayerSkillData(p);

		String material = ((String) data.vars.get("material"));
		Integer radius = ((Integer) data.vars.get("radius"));

		ArrayList<Material> weapons = new ArrayList<Material>();
		String w = ((String) data.vars.get("replace"));
		if (w == null) {
			w = "";
			weapons.add(null);
			weapons.add(Material.AIR);
		} else {
			if (w.isEmpty()) {
				weapons.add(null);
				weapons.add(Material.AIR);
			}
		}
		String[] list = w.split("\\s*,\\s*");
		for (String s : list) {
			if (s.equalsIgnoreCase("hand") || s.equalsIgnoreCase("hands")) {
				weapons.add(null);
				weapons.add(Material.AIR);
			} else {
				Material mat = Material.matchMaterial(s);
				weapons.add(mat);
			}
		}
		
		Integer hollow = ((Integer) data.vars.get("hollow"));

		Location loc = p.getLocation();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        int rSquared = radius * radius;
        int rSquared2 = ((radius-1) * (radius-1));
        if (hollow==0) { rSquared2 = 4;}
//        if (rSquared2<4) {rSquared2=4;}
//       System.out.print("rad:"+rSquared);
//       System.out.print("rad:"+rSquared2);
       
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                for (int y = cy - radius; y <= cy + radius; y++) {
//            	int y = cy;
	                if ((((cx - x) * (cx -x) + (cz - z) * (cz - z)  + (cy - y) * (cy - y)) <= rSquared) && (((cx - x) * (cx -x) + (cz - z) * (cz - z)  + (cy - y) * (cy - y)) >  rSquared2)) {
//	                    System.out.print("x:"+x+" y:"+y+" z:"+z+" r:"+((cx - x) * (cx -x) + (cz - z) * (cz - z)  + (cy - y) * (cy - y)) + " True");
	                    final Location l = new Location(loc.getWorld(), x, y, z);
	                    if (weapons.contains(l.getBlock().getType())) {
	                    	Block b =l.getBlock(); 
	                        lq.getServer().getScheduler().runTaskLater(lq, new ReplaceMaterial(l,b.getType()), (data.duration/50));
	                        b.setType(Material.matchMaterial(material));
	                		b.setMetadata("shield", new FixedMetadataValue(lq, "true"));
	                    }
	                } else {
//	                    System.out.print("x:"+x+" y:"+y+" z:"+z+" r:"+((cx - x) * (cx -x) + (cz - z) * (cz - z)  + (cy - y) * (cy - y)) + " False");
	                }
	            }
            }
        }
		return CommandResult.SUCCESS;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		String name = getMetaString(b, "shield");
		if (name != null && name.equalsIgnoreCase("true")) {
			event.setCancelled(true);
		}
	}
	
	public class ReplaceMaterial implements Runnable {
		public Location l;
		public Material m;
		public ReplaceMaterial(Location l, Material m){
			this.l=l;
			this.m=m;
		}
		public void run() {
			l.getBlock().setType(m);
			l.getBlock().setMetadata("shield", new FixedMetadataValue(lq, ""));
		}
	}
	
	public String getMetaString(Metadatable object, String label) {
		List<MetadataValue> values = object.getMetadata(label);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin() == lq) {
				return value.asString();
			}
		}
		return "";
	}
}
