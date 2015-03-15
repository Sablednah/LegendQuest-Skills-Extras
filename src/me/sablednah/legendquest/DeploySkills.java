package me.sablednah.legendquest;

/*
    >>\.
   /_  )`.
  /  _)`^)`.   _.---. _
 (_,' \  `^-)""      `.\
      |              | \
      \              / |
      / \  /.___.'\  (\ (_
     < ,"||     \ |`. \`-'
      \\ ()      )|  )/
      |_>|>     /_] //
        /_]        /_]
 */

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;

public class DeploySkills extends JavaPlugin {

	public Logger	logger;

	public void onDisable() {
	}

	@Override
	public void onEnable() {
		this.logger = getLogger();
		String pluginFolder = this.getDataFolder().getParentFile().getAbsolutePath() + File.separator + "LegendQuest" + File.separator + "skills" + File.separator;

		File pluginJar = this.getFile();
		File output = new File(pluginFolder + File.separator + "LQSkillsExtras.jar");

		boolean copy = FileUtil.copy(pluginJar, output);
		if(copy) {
			logger.info("Skill extras file copied to /plugins/LegendQuest/skills/");
		} else {
			logger.warning("Error: default skills file NOT copied to /plugins/LegendQuest/skills/");
		}
	}
}
