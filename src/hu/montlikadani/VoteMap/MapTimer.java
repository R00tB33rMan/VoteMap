package hu.montlikadani.VoteMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MapTimer {
	private int timeLeft;
	private int maxTime;

	public MapTimer(int maxTime) {
		this.maxTime = maxTime;
		this.timeLeft = maxTime;
	}

	public void tick() {
		--this.timeLeft;
		if (this.timeLeft % 60 == 0 && this.timeLeft != 0) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(VoteMap.getInstance().defaults(VoteMap.getInstance().messages.getString("time-left")
						.replace("%time-left%", this.secondsToMinutes(this.timeLeft))));
			}
		}

		if (this.timeLeft <= 10 && this.timeLeft >= 1) {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(VoteMap.getInstance().defaults(VoteMap.getInstance().messages.getString("time-left")
						.replace("%time-left%", this.secondsToMinutes(this.timeLeft))));
			}
		}

		if (this.timeLeft <= 0) {
			VoteMap.getMapManager().changeMap();
			this.timeLeft = this.maxTime;
		}
	}

	private String secondsToMinutes(int secs) {
		int minutes = secs / 60;
		int seconds = secs % 60;
		if (seconds == 0 && minutes != 0) {
			return minutes + " Minutes";
		} else {
			return minutes != 0 ? minutes + " Minutes and " + seconds + " Seconds" : seconds + " Seconds";
		}
	}

	public int getTimeLeft() {
		return this.timeLeft;
	}
}