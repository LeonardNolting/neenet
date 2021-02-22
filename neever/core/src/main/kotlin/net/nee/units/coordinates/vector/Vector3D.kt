package net.nee.units.coordinates.vector

import net.nee.units.coordinates.position.Position3D
import kotlin.math.pow
import kotlin.properties.Delegates

data class Vector3D(
	val x: Double,
	val y: Double,
	val z: Double
) : Vector<Vector3D>() {
	fun toVector2D() = Vector2D(x, z)

	override fun plus(other: Vector3D) =
		Vector3D(x + other.x, y + other.y, z + other.z)

	override fun minus(other: Vector3D) =
		Vector3D(x - other.x, y - other.y, z - other.z)

	override fun times(other: Double) =
		Vector3D(x * other, y * other, z * other)

	override fun times(other: Int) =
		times(other.toDouble())

	override fun div(other: Double) =
		Vector3D(x / other, y / other, z / other)

	override fun div(other: Int) =
		div(other.toDouble())

	override fun dot(other: Vector3D) =
		x * other.x + y * other.y + z * other.z

	override fun cross(other: Vector3D) = Vector3D(
		y * other.z - z * other.y,
		z * other.x - x * other.z,
		x * other.y - y * other.x
	)

	override fun distanceTo(other: Vector3D): Double {
		return (this - other).length
	}

	infix fun to(other: Vector3D) =
		other - this

	override val lengthSquared = x.pow(2) + y.pow(2) + z.pow(2)

	val normalized by lazy { if (length == 0.0) ZERO else this / length }

	fun withLength(length: Double) = normalized * length

	operator fun get(index: Int) =
		when (index) {
			0 -> x
			1 -> y
			2 -> z
			else -> throw IllegalArgumentException("index must be 0 (= x), 1 (= y) or 2 (= z)")
		}

	companion object {
		val ZERO = Vector3D(0.0, 0.0, 0.0)

		fun build(block: Builder.() -> Unit) =
			Builder().apply(block).finish()
	}

	class Builder {
		var x by Delegates.notNull<Double>()
		var y by Delegates.notNull<Double>()
		var z by Delegates.notNull<Double>()

		operator fun get(index: Int) =
			when (index) {
				0 -> x
				1 -> y
				2 -> z
				else -> throw IllegalArgumentException("index must be 0 (= x), 1 (= y) or 2 (= z)")
			}

		operator fun set(index: Int, value: Double) =
			when (index) {
				0 -> x = value
				1 -> y = value
				2 -> z = value
				else -> throw IllegalArgumentException("index must be 0 (= x), 1 (= y) or 2 (= z)")
			}

		fun finish() = Vector3D(x, y, z)
	}
}