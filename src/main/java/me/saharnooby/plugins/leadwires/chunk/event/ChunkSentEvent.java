package me.saharnooby.plugins.leadwires.chunk.event;

import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.chunk.ChunkCoord;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author saharNooby
 * @since 10:57 25.03.2020
 */
@Getter
public final class ChunkSentEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	private final Player player;
	private final ChunkCoord coord;

	public ChunkSentEvent(boolean isAsync, @NonNull Player player, @NonNull ChunkCoord coord) {
		super(isAsync);
		this.player = player;
		this.coord = coord;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
