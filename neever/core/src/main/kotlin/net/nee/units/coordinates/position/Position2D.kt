package net.nee.units.coordinates.position

import net.nee.units.coordinates.vector.Vector
import net.nee.units.coordinates.vector.Vector2D

data class Position2D(
	val x: Double,
	val z: Double
) : Position<Position2D>() {
	fun toPosition3D(y: Double) = Position3D(x, y, z)

	/**
	 * Compares this object with the specified object for order. Returns zero if this object is equal
	 * to the specified [other] object, a negative number if it's less than [other], or a positive number
	 * if it's greater than [other].
	 */
	override fun compareTo(other: Position2D) = when {
		x > other.x && z > other.z -> 1
		x < other.x && z < other.z -> -1
		else -> 0
	}

	override fun to(other: Position2D) =
		Vector2D(other.x - x, other.z - z)
}