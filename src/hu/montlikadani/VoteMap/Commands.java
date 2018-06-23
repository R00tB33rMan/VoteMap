package hu.montlikadani.VoteMap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Commands implements CommandExecutor, TabCompleter {

	private VoteMap plugin;

	public Commands(VoteMap plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("votemap")) {
				if (args.length == 0) {
					if (!sender.hasPermission(Permissions.PINFO) && plugin.getConfig().getBoolean("default-can-see-plugin-information") != true) {
						sender.sendMessage(plugin.defaults(plugin.messages.getString("no-permission").replace("%perm%", "votemap.plugininfo")));
						return true;
					}
					sender.sendMessage("§e§l[§6Vote§aMap§b§l Info§e§l]");
					sender.sendMessage("§5Version:§a 1.0");
					sender.sendMessage("§5Author, created by:§a montlikadani");
					sender.sendMessage("§5Commands:§8 /§7" + label + "§a help");
					sender.sendMessage("§4In case of an error, write here:§e §nhttps://github.com/montlikadani/VoteMap/issues");
					return true;
				} else if (args[0].equalsIgnoreCase("help")) {
					if (!sender.hasPermission(Permissions.HELP)) {
						sender.sendMessage(plugin.defaults(plugin.messages.getString("no-permission").replace("%perm%", "votemap.help")));
						return true;
					}
					if (args.length > 1) {
						if (plugin.getConfig().getBoolean("unknown-command-enable")) {
							sender.sendMessage(plugin.defaults(plugin.getConfig().getString("unknown-command").replace("%command%", label)));
							return true;
						}
					}
					for (String msg : plugin.messages.getStringList("chat-messages")) {
						sender.sendMessage(plugin.colorMsg(msg.replace("%command%", label).replace("%prefix%", plugin.messages.getString("prefix"))));
					}
					return true;
				} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					if (!sender.hasPermission(Permissions.RELOAD)) {
						sender.sendMessage(plugin.defaults(plugin.messages.getString("no-permission").replace("%perm%", "votemap.reload")));
						return true;
					}
					if (args.length > 1) {
						if (plugin.getConfig().getBoolean("unknown-command-enable")) {
							sender.sendMessage(plugin.defaults(plugin.getConfig().getString("unknown-command").replace("%command%", label)));
							return true;
						}
					}
					plugin.createFiles();
					sender.sendMessage(plugin.defaults(plugin.messages.getString("reload-config")));
					return true;
				} else if (args[0].equalsIgnoreCase("disable")) {
					if (!(sender.hasPermission(Permissions.PDISABLE) && sender.isOp())) {
						sender.sendMessage(plugin.defaults(plugin.messages.getString("no-permission").replace("%perm%", "votemap.plugindisable + op")));
						return true;
					}
					if (args.length > 1) {
						if (plugin.getConfig().getBoolean("unknown-command-enable")) {
							sender.sendMessage(plugin.defaults(plugin.getConfig().getString("unknown-command").replace("%command%", label)));
							return true;
						}
					}
					plugin.getServer().getPluginManager().disablePlugin(plugin);
					return true;
				} else {
					sender.sendMessage(plugin.defaults(plugin.messages.getString("unknown-sub-command").replace("%subcmd%", args[0])));
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			plugin.throwMsg();
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission(Permissions.TABCOMP)) {
			List<String> list = new ArrayList<String>();
			if (args.length == 1) {
				list.add("help");
			}
			if (args.length == 1) {
				list.add("reload");
			}
			if (args.length == 1) {
				list.add("disable");
			}
			return list;
		}
		return null;
	}
}