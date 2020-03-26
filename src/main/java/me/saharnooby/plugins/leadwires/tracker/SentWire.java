package me.saharnooby.plugins.leadwires.tracker;

import lombok.Data;
import me.saharnooby.plugins.leadwires.wire.Wire;

/**
 * @author saharNooby
 * @since 10:28 25.03.2020
 */
@Data
public final class SentWire {

	private final Wire wire;
	private final int idA;
	private final int idB;

}
