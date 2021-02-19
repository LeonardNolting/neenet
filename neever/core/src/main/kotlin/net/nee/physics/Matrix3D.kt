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
	constructor(view: RolledView) : this(
		Vector3D(
			cos(view.yaw.radians/* + PI/2*/) * cos(view.roll.radians),
			-sin(view.roll.radians),
			sin(view.yaw.radians/* + PI/2*/) * cos(view.roll.radians)
		),
		Vector3D(
			-cos(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * sin(view.pitch.radians) - sin(view.yaw.radians/* + PI/2*/) * cos(view.pitch.radians),
			-cos(view.roll.radians) * sin(view.pitch.radians),
			-sin(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * sin(view.pitch.radians) + cos(view.yaw.radians/* + PI/2*/) * cos(view.pitch.radians)
		),
		Vector3D(
			-cos(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * cos(view.pitch.radians) + sin(view.yaw.radians/* + PI/2*/) * sin(view.pitch.radians),
			cos(view.roll.radians) * sin(view.pitch.radians),
			-sin(view.yaw.radians/* + PI/2*/) * sin(view.roll.radians) * cos(view.pitch.radians) - cos(view.yaw.radians/* + PI/2*/) * sin(view.pitch.radians)
		)
	)

	val rotationAsRolledView: RolledView by lazy {
		// | cos(yaw)cos(pitch) -cos(yaw)sin(pitch)sin(roll)-sin(yaw)cos(roll) -cos(yaw)sin(pitch)cos(roll)+sin(yaw)sin(roll) |
		// | sin(yaw)cos(pitch) -sin(yaw)sin(pitch)sin(roll)+cos(yaw)cos(roll) -sin(yaw)sin(pitch)cos(roll)-cos(yaw)sin(roll) |
		// | sin(pitch)          cos(pitch)sin(roll)                            cos(pitch)sin(roll)                           |
		val yaw = atan2(x.y, x.x) / PI * 180
		val pitch = atan2(-x.z, sqrt(y.z * y.z + z.z * z.z)) / PI * 180
//		val roll = atan2(y.z, z.z)
//		val yaw = -atan2(z.x, z.z) / PI * 180
//		while (yaw < 0)
//			yaw += 360
//		val pitch = -asin(z.y / 1) / PI * 180
		// maybe
		val roll = atan2(y.z, z.z) / PI * 180
		RolledView(yaw, pitch, roll)
	}

	override fun toString() =
		"""
			[ ${x.x}  ${y.x}  ${z.x} ]
			[ ${x.y}  ${y.y}  ${z.y} ]
			[ ${x.z}  ${y.z}  ${z.z} ]
		""".trimIndent()

	companion object {
		val IDENTITY = Matrix3D(Vector3D(1.0, 0.0, 0.0), Vector3D(0.0, 1.0, 0.0), Vector3D(0.0, 0.0, 1.0))
	}
}