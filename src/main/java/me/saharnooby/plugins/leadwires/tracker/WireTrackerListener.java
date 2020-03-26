package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkSentEvent;
import me.saharnooby.plugins.leadwires.chunk.event.ChunkUnloadSentEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author saharNooby
 * @since 10:21 25.03.2020
 */
@RequiredArgsConstructor
public final class WireTrackerListener implements Listener {

	private final WireTracker tracker;

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		this.tracker.checkPlayer(e.getPlayer());
	}

	@EventHandler
	public void onChunkSent(ChunkSentEvent e) {
		checkInMainThread(e, e.getPlayer());
	}

	@EventHandler
	public void onChunkUnloadSent(ChunkUnloadSentEvent e) {
		checkInMainThread(e, e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.tracker.removePlayer(e.getPlayer());
	}

	private void checkInMainThread(@NonNull Event event, @NonNull Player player) {
		if (event.isAsynchronous()) {
			Bukkit.getScheduler().runTask(LeadWires.getInstance(), () -> this.tracker.checkPlayer(player));
		} else {
			this.tracker.checkPlayer(player);
		}
	}

}
