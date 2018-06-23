package hu.montlikadani.VoteMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class VoteCmd implements CommandExecutor {

	private VoteMap plugin;

	public VoteCmd(VoteMap plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("vote")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(plugin.defaults(plugin.messages.getString("no-console-to-vote")));
					return true;
				} else {
					Player p = (Player) sender;
					if (!p.hasPermission(Permissions.VOTE)) {
						p.sendMessage(plugin.defaults(plugin.messages.getString("no-permission").replace("%perm%", "votemap.vote")));
						return true;
					}
					Object maps = plugin.getConfig().get("maps");
					if (args.length == 0) {
						p.sendMessage(plugin.defaults(plugin.messages.getString("vote-usage").replace("%maps%", ((ConfigurationSection) maps).getKeys(false).toString()
								.replace("[", "")
								.replace("]", ""))));
						return true;
					}
					if (args.length == 1) {
						String mapName = plugin.getConfig().getString(maps + "." + p.getWorld());
						World m = Bukkit.getWorld(mapName);
						if (m == null) {
							p.sendMessage(plugin.defaults(plugin.messages.getString("map-not-found").replace("%map%", args[0])));
							return true;
						}
						if (plugin.votes.getString("votes." + m.getName() + ".vote-amount") != null) {
							p.sendMessage(plugin.defaults(plugin.messages.getString("no-more-vote-to-world")));
							return true;
						}
						int x = 0;
						plugin.votes.set("votes." + m.getName() + ".vote-amount", x + 1);
						plugin.votes.save(plugin.votes_file);
						p.sendMessage(plugin.defaults(plugin.messages.getString("success-voted").replace("%map%", m.getName())));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			plugin.throwMsg();
		}
		return true;
	}
}
