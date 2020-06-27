package me.saharnooby.plugins.leadwires.tracker;

import lombok.Data;
import me.saharnooby.plugins.leadwires.chunk.ChunkCoord;

import java.util.Set;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 13:18 27.06.2020
 */
@Data
public final class PlayerDebugInfo {

	private final Set<UUID> sentWires;
	private final Set<ChunkCoord> visibleChunks;

}
