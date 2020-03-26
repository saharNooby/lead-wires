package me.saharnooby.plugins.leadwires.chunk.event;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author saharNooby
 * @since 10:57 25.03.2020
 */
@Getter
public final class RespawnSentEvent extends Event {

	private static final HandlerList handlerList = new HandlerList();

	private final Player player;

	public RespawnSentEvent(boolean isAsync, @NonNull Player player) {
		super(isAsync);
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}

}
