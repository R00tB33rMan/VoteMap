package hu.montlikadani.VoteMap;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteMap extends JavaPlugin {

	private static VoteMap instance;

	FileConfiguration config = getConfig();
	FileConfiguration messages, votes;
	private File config_file = new File("plugins/VoteMap/config.yml");
	private File messages_file = new File("plugins/VoteMap/messages.yml");
	private File votes_file = new File("plugins/VoteMap/votes.yml");

	private static MapTimer mapTimer;
	private static MapManager mapManager;
	private int cver = 1;
	private int msver = 1;

	@Override
	public void onEnable() {
		try {
			super.onEnable();
			createFiles();
			if (!getConfig().getBoolean("enabled")) {
				this.getServer().getPluginManager().disablePlugin(this);
				return;
			}
			instance = this;
			Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
			registerCommands();
			runTimer();
			if (getConfig().getBoolean("check-update")) {
				logConsole(Level.INFO, checkVersion());
			}
			if (getConfig().getBoolean("metrics")) {
				@SuppressWarnings("unused")
				Metrics metrics = new Metrics(this);
				logConsole(Level.INFO, "Metrics enabled.");
			}
			if (getConfig().getString("plugin-enable") != null && !getConfig().getString("plugin-enable").equals("")) {
				getServer().getConsoleSender().sendMessage(defaults(getConfig().getString("plugin-enable")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().log(Level.WARNING, "There was an error. Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
		}
	}

	@Override
	public void onDisable() {
		try {
			super.onDisable();
			instance = null;
			getServer().getScheduler().cancelTasks(this);
			if (getConfig().getString("plugin-disable") != null && !getConfig().getString("plugin-disable").equals("")) {
				getServer().getConsoleSender().sendMessage(defaults(getConfig().getString("plugin-disable")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().log(Level.WARNING, "There was an error. Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
		}
	}

	public void createFiles() {
		try {
			if (config_file.exists()) {
				config = YamlConfiguration.loadConfiguration(config_file);
				config.load(config_file);
				reloadConfig();
				saveDefaultConfig();
				if (!getConfig().isSet("config-version") || !getConfig().get("config-version").equals(cver)) {
					logConsole(Level.WARNING, "Found outdated configuration (config.yml)! (Your version: " + getConfig().getString("config-version") + " | Newest version: " + cver + ")");
				}
			} else {
				saveResource("config.yml", false);
				config = YamlConfiguration.loadConfiguration(config_file);
				logConsole(Level.INFO, "The 'config.yml' file successfully created!");
			}
			if (messages_file.exists()) {
				messages = YamlConfiguration.loadConfiguration(messages_file);
				messages.load(messages_file);
				if (!messages.isSet("config-version") || !messages.get("config-version").equals(msver)) {
					logConsole(Level.WARNING, "Found outdated configuration (messages.yml)! (Your version: " + messages.getString("config-version") + " | Newest version: " + msver + ")");
				}
			} else {
				saveResource("messages.yml", false);
				messages = YamlConfiguration.loadConfiguration(messages_file);
				logConsole(Level.INFO, "The 'messages.yml' file successfully created!");
			}
			if (votes_file.exists()) {
				votes = YamlConfiguration.loadConfiguration(votes_file);
				votes.load(votes_file);
			} else {
				saveResource("votes.yml", false);
				votes = YamlConfiguration.loadConfiguration(votes_file);
				logConsole(Level.INFO, "The 'votes.yml' file successfully created!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().log(Level.WARNING, "There was an error. Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
		}
	}

	public void logConsole(Level level, String error) {
		if (getConfig().getBoolean("logconsole")) {
			Bukkit.getLogger().log(level, "[VoteMap] " + error);
		}
		if (getConfig().getBoolean("log-to-file")) {
			try {
				File dataFolder = getDataFolder();
				if (!dataFolder.exists()) {
					dataFolder.mkdir();
				}
				File saveTo = new File(getDataFolder(), "log.txt");
				if (!saveTo.exists()) {
					saveTo.createNewFile();
				}
				FileWriter fw = new FileWriter(saveTo, true);
				PrintWriter pw = new PrintWriter(fw);
				Date dt = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
				String time = df.format(dt);
				pw.println(time + " - " + "[" + level + "] " + error);
				pw.flush();
				pw.close();
			} catch (Exception e) {
				e.printStackTrace();
				throwMsg();
			}
		}
	}

	public static String checkVersion() {
		VoteMap pl = getPlugin(VoteMap.class);
		String[] nVersion;
		String[] cVersion;
		String lineWithVersion;
		try {
			URL githubUrl = new URL("https://raw.githubusercontent.com/montlikadani/VoteMap/master/plugin.yml");
			lineWithVersion = "";
			Scanner websiteScanner = new Scanner(githubUrl.openStream());
			while (websiteScanner.hasNextLine()) {
				String line = websiteScanner.nextLine();
				if (line.toLowerCase().contains("version")) {
					lineWithVersion = line;
					break;
				}
			}
			String versionString = lineWithVersion.split(": ")[1];
			nVersion = versionString.split("\\.");
			double newestVersionNumber = Double.parseDouble(nVersion[0] + "." + nVersion[1]);
			cVersion = pl.getDescription().getVersion().split("\\.");
			double currentVersionNumber = Double.parseDouble(cVersion[0] + "." + cVersion[1]);
			if (newestVersionNumber > currentVersionNumber) {
				return "New version (" + versionString + ") is available at https://github.com/montlikadani/VoteMap/releases";
			} else {
				return "You're running the latest version.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			pl.logConsole(Level.WARNING, "Failed to compare versions. " + e + " Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
		}
		return "Failed to get newest version number.";
	}

	private void registerCommands() {
		getCommand("votemap").setExecutor(new Commands(this));
		getCommand("vote").setExecutor(new VoteCmd(this));
	}

	public void runTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				mapTimer.tick();
			}
		}, 0L, 20L);
	}

	public void resetVotes() {
		try {
			ConfigurationSection maps = getConfig().getConfigurationSection("maps");
			for (int i = 0; i < ((List<String>) maps).size(); i++) {
				World m = Bukkit.getWorld((String) maps.get(String.valueOf(i)));
				votes.set("votes." + m.getName() + ".vote-amount", null);
			}
			votes.save(votes_file);
		} catch (Exception e) {
			e.printStackTrace();
			throwMsg();
		}
	}

	public static MapTimer getMapTimer() {
		return mapTimer;
	}

	public static MapManager getMapManager() {
		return mapManager;
	}

	public void throwMsg() {
		logConsole(Level.WARNING, "There was an error. Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
		return;
	}

	public static VoteMap getInstance() {
		return instance;
	}

	public String defaults(String str) {
		str = str.replace("%prefix%", messages.getString("prefix"));
		str = str.replace("%newline%", "\n");
		return colorMsg(str);
	}

	public String colorMsg(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}