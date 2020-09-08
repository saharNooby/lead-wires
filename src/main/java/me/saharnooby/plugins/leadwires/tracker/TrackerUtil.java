package me.saharnooby.plugins.leadwires.tracker;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.chunk.ChunkCoord;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;

import java.util.Set;

/**
 * @author saharNooby
 * @since 17:18 26.03.2020
 */
public final class TrackerUtil {

	private static final Vector ATTACHED_OFFSET;
	private static final Vector HOLDER_OFFSET;

	static {
		if (NMSUtil.getMinorVersion() >= 16) {
			if (NMSUtil.getReleaseVersion() > 1) {
				ATTACHED_OFFSET = new Vector(0, 0, -0.125);
				HOLDER_OFFSET = new Vector(0, 0, 0);
			} else {
				// These offsets are not exact and were chosen manually
				ATTACHED_OFFSET = new Vector(0, -0.12083333333333324, -0.14750000238418542);
				HOLDER_OFFSET = new Vector(0.6999999999999886, 0.15812500000000007, -0.5);
			}
		} else {
			// Exact values obtained from net.minecraft.client.renderer.entity.MobRenderer#renderLeash
			ATTACHED_OFFSET = new Vector(0.0, -0.8083333333333332, -0.16000000238418544);
			HOLDER_OFFSET = new Vector(0.6999999999999886, 0.7050000000000001, -0.5);
		}
	}

	public static Vector posA(@NonNull Wire wire) {
		return wire.getA().add(ATTACHED_OFFSET);
	}

	public static Vector posB(@NonNull Wire wire) {
		return wire.getB().add(HOLDER_OFFSET);
	}

	public static ChunkCoord chunkA(@NonNull Wire wire) {
		return posA(wire).getChunk();
	}

	public static ChunkCoord chunkB(@NonNull Wire wire) {
		return posB(wire).getChunk();
	}

	public static boolean containsBothEnds(@NonNull Set<ChunkCoord> loaded, @NonNull Wire wire) {
		return loaded.contains(chunkA(wire)) && loaded.contains(chunkB(wire));
	}

	public static boolean hasAnyEndInChunk(@NonNull Wire wire, @NonNull ChunkCoord coord) {
		return coord.equals(chunkA(wire)) || coord.equals(chunkB(wire));
	}

}
