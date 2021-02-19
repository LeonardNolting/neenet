package net.nee.units.coordinates.vector

import kotlin.math.pow

data class Vector2D(
	val x: Double,
	val z: Double
) : Vector<Vector2D>() {
	fun toVector3D(y: Double) = Vector3D(x, y, z)

	override fun plus(other: Vector2D) =
		Vector2D(x + other.x, z + other.z)

	override fun minus(other: Vector2D) =
		Vector2D(x - other.x, z - other.z)

	override fun times(other: Double) =
		Vector2D(x * other, z * other)

	override fun times(other: Int) =
		times(other.toDouble())

	override fun div(other: Double) =
		Vector2D(x / other, z / other)

	override fun div(other: Int) =
		div(other.toDouble())

	override fun dot(other: Vector2D) =
		x * other.x + z * other.z

	override fun cross(other: Vector2D): Vector2D {
		TODO("Not yet implemented")
	}

	override fun distanceTo(other: Vector2D): Double {
		TODO("Not yet implemented")
	}

	override val lengthSquared = x.pow(2) + z.pow(2)
}