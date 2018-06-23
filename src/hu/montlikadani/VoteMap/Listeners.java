package hu.montlikadani.VoteMap;

import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Listeners implements Listener {

	private VoteMap plugin;

	public Listeners(VoteMap plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		VoteMap.getMapManager().respawnPlayer(p);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(VoteMap.getMapManager().getCurrentWorld().getSpawnLocation());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (plugin.getConfig().getBoolean("check-update") && p.isOp() && p.hasPermission("votemap.checkupdate")) {
			VoteMap plu = VoteMap.getPlugin(VoteMap.class);
			String[] nVersion;
			String[] cVersion;
			String lineWithVersion;
			URL githubUrl;
			try {
				githubUrl = new URL("https://raw.githubusercontent.com/montlikadani/VoteMap/master/plugin.yml");
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
				cVersion = plu.getDescription().getVersion().split("\\.");
				double currentVersionNumber = Double.parseDouble(cVersion[0] + "." + cVersion[1]);
				if (newestVersionNumber > currentVersionNumber) {
					p.sendMessage(plugin.colorMsg("&8&m&l--------------------------------------------------\n"
							+ plugin.messages.getString("prefix") + "&a A new update is available!&4 Version:&7 " + versionString
							+ "\n&6Download:&c &nhttps://github.com/montlikadani/VoteMap/releases"
							+ "\n&8&m&l--------------------------------------------------"));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				plugin.logConsole(Level.WARNING, "Failed to compare versions. " + ex + " Please report it here:\nhttps://github.com/montlikadani/VoteMap/issues");
			}
		}
	}
}
