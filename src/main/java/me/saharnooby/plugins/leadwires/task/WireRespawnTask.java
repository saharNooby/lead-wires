package me.saharnooby.plugins.leadwires.task;

import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.LeadWires;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author saharNooby
 * @since 16:11 30.06.2020
 */
@RequiredArgsConstructor
public final class WireRespawnTask implements Runnable {

	private final LeadWires plugin;

	private int tick;

	@Override
	public void run() {
		if (!this.plugin.isContiniousRespawnEnabled()) {
			return;
		}

		if (++this.tick < this.plugin.getContiniousRespawnInterval()) {
			return;
		}

		this.tick = 0;

		for (Player player : Bukkit.getOnlinePlayers()) {
			LeadWires.getInstance().respawnWires(player);
		}
	}

}
