package hu.montlikadani.VoteMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MapTimer extends BukkitRunnable {
	private int timeLeft;

	@Override
	public void run() {
		this.timeLeft = VoteMap.getInstance().getConfig().getInt("time");
		if (this.timeLeft == VoteMap.getInstance().getConfig().getInt("notifications." + Integer.valueOf(timeLeft).intValue())) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(VoteMap.getInstance().defaults(VoteMap.getInstance().getConfig().getString("message")
						.replace("%time-left%", this.timeLeft + "")));
			}
		}

		if (this.timeLeft <= 0) {
			VoteMap.getMapManager().changeMap();
			cancel();
		}
	}
}