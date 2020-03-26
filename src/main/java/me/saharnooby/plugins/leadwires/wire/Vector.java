package me.saharnooby.plugins.leadwires.wire;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Location;

/**
 * A 3-dimensional floating-point vector.
 * @author saharNooby
 * @since 21:17 24.03.2020
 */
@Data
public final class Vector {

	private final double x;
	private final double y;
	private final double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a vector from a location, taking x, y, and z coordinates from it.
	 * @param loc Location.
	 */
	public Vector(@NonNull Location loc) {
		this(loc.getX(), loc.getY(), loc.getZ());
	}

	/**
	 * Adds each component of the specified vector to each component of this vector and returns a new vector containing the result.
	 * @param v Vector to add.
	 * @return New vector containing the result.
	 */
	public Vector add(@NonNull Vector v) {
		return new Vector(this.x + v.x, this.y + v.y, this.z + v.z);
	}

}
