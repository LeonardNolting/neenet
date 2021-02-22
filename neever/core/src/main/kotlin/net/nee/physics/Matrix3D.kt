package net.nee.physics

import net.nee.units.coordinates.vector.Vector3D
import kotlin.math.*

/**
 * ```
 * [ x.x  y.x  z.x ]
 * [ x.y  y.y  z.y ]
 * [ x.z  y.z  z.z ]
 * ```
 */
data class Matrix3D(val x: Vector3D, val y: Vector3D, val z: Vector3D) {
	operator fun times(scale: Double) = Matrix3D(x * scale, y * scale, z * scale)

//	constructor(view: RolledView) : this(
//		Vector3D(
//			cos(view.yaw.radians/* + PI/2*/) * cos(view.roll.radians),
//			-sin(view.roll.radians),
//			sin(view.yaw.radians/* + PI/2*/) * cos(view.roll.radians)
//		),
//		Vector3D(
//			-cos(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * sin(view.pitch.radians) - sin(view.yaw.radians/* + PI/2*/) * cos(
//				view.pitch.radians
//			),
//			-cos(view.roll.radians) * sin(view.pitch.radians),
//			-sin(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * sin(view.pitch.radians) + cos(view.yaw.radians/* + PI/2*/) * cos(
//				view.pitch.radians
//			)
//		),
//		Vector3D(
//			-cos(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * cos(view.pitch.radians) + sin(view.yaw.radians/* + PI/2*/) * sin(
//				view.pitch.radians
//			),
//			cos(view.roll.radians) * sin(view.pitch.radians),
//			-sin(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * cos(view.pitch.radians) - cos(view.yaw.radians/* + PI/2*/) * sin(
//				view.pitch.radians
//			)
//		)
//	)
//
//	val rotationAsRolledView: RolledView by lazy {
//		// | cos(yaw)cos(pitch) -cos(yaw)sin(pitch)sin(roll)-sin(yaw)cos(roll) -cos(yaw)sin(pitch)cos(roll)+sin(yaw)sin(roll) |
//		// | sin(yaw)cos(pitch) -sin(yaw)sin(pitch)sin(roll)+cos(yaw)cos(roll) -sin(yaw)sin(pitch)cos(roll)-cos(yaw)sin(roll) |
//		// | sin(pitch)          cos(pitch)sin(roll)                            cos(pitch)sin(roll)                           |
//		val yaw = atan2(x.y, x.x) / PI * 180
//		val pitch = atan2(-x.z, sqrt(y.z * y.z + z.z * z.z)) / PI * 180
////		val roll = atan2(y.z, z.z)
////		val yaw = -atan2(z.x, z.z) / PI * 180
////		while (yaw < 0)
////			yaw += 360
////		val pitch = -asin(z.y / 1) / PI * 180
//		// maybe
//		val roll = atan2(y.z, z.z) / PI * 180
//		RolledView(yaw, pitch, roll)
//	}

	fun transform(vector: Vector3D) =
		x * vector.x + y * vector.y + z * vector.z

//	fun inverseTransform(vector: Vector3D) =
//		x / vector.x + y / vector.y + z / vector.z

	/**
	 * `transform(matrix.transform(vector))` is equivalent to `transform(matrix).transform(vector)`
	 * @return the matrix equivalent to the transformation of first [matrix], then this
	 */
	fun transform(matrix: Matrix3D) =
		Matrix3D(transform(matrix.x), transform(matrix.y), transform(matrix.z))

//	fun fromDirection(vector: Vector3D) =
//		Matrix3D(
//			Vector3D(
//				x.x,
//				y.x,
//				vector.x,
//			),
//			Vector3D(
//				x.y,
//				y.y,
//				vector.y,
//			),
//			Vector3D(
//				x.z,
//				y.z,
//				vector.z,
//			),
//		)

	val determinant by lazy {
		(this.x.x * (this.y.y * this.z.z - this.z.y * this.y.z)
			- this.y.x * (this.x.y * this.z.z - this.z.y * this.x.z)
			+ this.z.x * (this.x.y * this.y.z - this.y.y * this.x.z))
	}

	val inverse by lazy {
		build {
			val invdet = 1 / determinant
			x.x = (this@Matrix3D.y.y * this@Matrix3D.z.z - this@Matrix3D.z.y * this@Matrix3D.y.z) * invdet
			x.y = (this@Matrix3D.x.z * this@Matrix3D.z.y - this@Matrix3D.x.y * this@Matrix3D.z.z) * invdet
			x.z = (this@Matrix3D.x.y * this@Matrix3D.y.z - this@Matrix3D.x.z * this@Matrix3D.y.y) * invdet
			y.x = (this@Matrix3D.y.z * this@Matrix3D.z.x - this@Matrix3D.y.x * this@Matrix3D.z.z) * invdet
			y.y = (this@Matrix3D.x.x * this@Matrix3D.z.z - this@Matrix3D.x.z * this@Matrix3D.z.x) * invdet
			y.z = (this@Matrix3D.y.x * this@Matrix3D.x.z - this@Matrix3D.x.x * this@Matrix3D.y.z) * invdet
			z.x = (this@Matrix3D.y.x * this@Matrix3D.z.y - this@Matrix3D.z.x * this@Matrix3D.y.y) * invdet
			z.y = (this@Matrix3D.z.x * this@Matrix3D.x.y - this@Matrix3D.x.x * this@Matrix3D.z.y) * invdet
			z.z = (this@Matrix3D.x.x * this@Matrix3D.y.y - this@Matrix3D.y.x * this@Matrix3D.x.y) * invdet
		}
	}

	operator fun get(index: Int) =
		when (index) {
			0 -> x
			1 -> y
			2 -> z
			else -> throw IllegalArgumentException("index must be 0 (= x), 1 (= y) or 2 (= z)")
		}

	override fun toString() =
		"""
			[ ${x.x}  ${y.x}  ${z.x} ]
			[ ${x.y}  ${y.y}  ${z.y} ]
			[ ${x.z}  ${y.z}  ${z.z} ]
		""".trimIndent()

	companion object {
		val IDENTITY = Matrix3D(Vector3D(1.0, 0.0, 0.0), Vector3D(0.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))

		fun build(block: Builder.() -> Unit) =
			Builder().apply(block).finish()

		fun fromDirection(direction: Vector3D, up: Vector3D = Vector3D(0.0, 1.0, 0.0)): Matrix3D {
			val xaxis = (up cross direction).normalized

			val yaxis = (direction cross xaxis).normalized

			return Matrix3D(
				Vector3D(
					xaxis.x,
					yaxis.x,
					direction.x,
				),
				Vector3D(
					xaxis.y,
					yaxis.y,
					direction.y,
				),
				Vector3D(
					xaxis.z,
					yaxis.z,
					direction.z,
				)
			)
		}

//		fun fromDirection(vector: Vector3D, up: Vector3D) =
	}

	class Builder {
		val x = Vector3D.Builder()

		var y = Vector3D.Builder()

		var z = Vector3D.Builder()

		operator fun get(index: Int) =
			when (index) {
				0 -> x
				1 -> y
				2 -> z
				else -> throw IllegalArgumentException("index must be 0 (= x), 1 (= y) or 2 (= z)")
			}

		fun finish() = Matrix3D(x.finish(), y.finish(), z.finish())
	}
//			Matrix3D(
//				Vector3D(
//					,
//					,
//					,
//				),
//				Vector3D(
//					,
//					,
//					,
//				),
//				Vector3D(
//					,
//					,
//					,
//				),
//			)
}