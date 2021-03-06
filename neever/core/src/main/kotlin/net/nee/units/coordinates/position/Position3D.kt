package net.nee.units.coordinates.position

import net.nee.units.coordinates.vector.Vector2D
import net.nee.units.coordinates.vector.Vector3D

/**
 * @param x Positive: east, negative: west
 * @param y Height
 * @param z Positive: south, negative: north
 */
open class Position3D(
	val x: Double,
	val y: Double,
	val z: Double
) : Position<Position3D>() {
	fun toPosition2D() = Position2D(x, z)

	/**
	 * Compares this object with the specified object for order. Returns zero if this object is equal
	 * to the specified [other] object, a negative number if it's less than [other], or a positive number
	 * if it's greater than [other].
	 */
	override fun compareTo(other: Position3D): Int = when {
		x > other.x && y > other.y && z > other.z -> 1
		x < other.x && y < other.y && z < other.z -> -1
		else                                      -> 0
	}

	override fun to(other: Position3D) =
		Vector3D(other.x - x, other.y - y, other.z - z)
}