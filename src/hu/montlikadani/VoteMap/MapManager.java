package hu.montlikadani.VoteMap;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MapManager {
	private String currentMap;
	private World currentWorld;
	private VoteMap plugin;

	public MapManager(VoteMap plugin) {
		this.plugin = plugin;
		this.setCurrentWorld((World) Bukkit.getWorlds().get(0));
	}

	public void respawnPlayer(final Player p) {
		long respawnTime = plugin.getConfig().getLong("player-respawn-time");
		if (respawnTime != 0L) {
			p.sendMessage(plugin.defaults(plugin.messages.getString("auto-respawn-in").replace("%seconds%", String.valueOf(respawnTime))));
			Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
				@Override
				public void run() {
					p.spigot().respawn();
					p.teleport(MapManager.this.currentWorld.getSpawnLocation());
				}
			}, respawnTime * 20L);
		}
	}

	public void changeMap() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			ConfigurationSection mapNames = plugin.getConfig().getConfigurationSection("maps." + pl.getWorld().getName());
			int amount = plugin.votes.getInt("votes." + pl.getName() + ".vote-amount");
			Bukkit.getLogger().log(Level.INFO, "Voted maps amount: " + amount);
			if (String.valueOf(amount).length() == plugin.getConfig().getInt("maps." + Bukkit.getWorld(pl.getWorld().getName()) + ".max-votes-to-teleport")) {
				if (((List<World>) mapNames).size() >= 2) {
					changeMap((String) mapNames.get(String.valueOf(amount)));
				} else {
					changeMap((String) mapNames.get(String.valueOf(0)));
				}
			} else {
				changeMap((String) mapNames.get(String.valueOf(Maths.randInt(0, ((List<World>) mapNames).size() - 1))));
			}
		}
	}

	private void changeMap(String newMap) {
		if (Bukkit.getWorld(newMap) == null) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.getPlayer().sendMessage(plugin.defaults(plugin.messages.getString("error-changing-map").replace("%map%", newMap)));
			}
		} else {
			World newWorld = Bukkit.getWorld(newMap);
			this.setCurrentMap(newWorld.getName());
			this.setCurrentWorld(newWorld);
			Bukkit.getLogger().log(Level.INFO, "Choosed world and teleporting players to: " + newWorld.getName());
			if (plugin.getConfig().getBoolean("teleport-players")) {
				Bukkit.broadcastMessage(plugin.defaults(plugin.messages.getString("map-changed-players-teleported").replace("%map%", newMap)));
				for (Player pls : Bukkit.getOnlinePlayers()) {
					pls.setNoDamageTicks(40);
					pls.teleport(this.currentWorld.getSpawnLocation());
				}
			} else {
				for (Player pl : Bukkit.getOnlinePlayers()) {
					pl.sendMessage(plugin.defaults(plugin.messages.getString("map-changed").replace("%map%", newMap)));
				}
			}
			plugin.resetVotes();
		}
	}

	public World getCurrentWorld() {
		return this.currentWorld;
	}

	public void setCurrentWorld(World newWorld) {
		this.currentWorld = newWorld;
	}

	public String getCurrentMap() {
		return this.currentMap;
	}

	public void setCurrentMap(String newMap) {
		this.currentMap = newMap;
	}
}
