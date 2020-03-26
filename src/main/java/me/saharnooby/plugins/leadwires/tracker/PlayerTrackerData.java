package me.saharnooby.plugins.leadwires.tracker;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 10:23 25.03.2020
 */
@Data
public final class PlayerTrackerData {

	private String world;
	private final Map<UUID, SentWire> sentWires = new HashMap<>();

}
