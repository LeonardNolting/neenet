package net.nee.physics

import net.nee.units.coordinates.vector.Vector3D
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Quaternion(val s: Double, val v: Vector3D) {
	operator fun times(scalar: Double) = Quaternion(s * scalar, v * scalar)

	operator fun times(other: Quaternion) =
		Quaternion(s * other.s - (v dot other.v), other.v * s + v * other.s + (v cross other.v))

	operator fun plus(other: Quaternion) =
		Quaternion(s + other.s, v + other.v)

	operator fun div(scalar: Double) = this * (1 / scalar)

	val conjugate by lazy { Quaternion(s, -v) }
	val inverse by lazy { conjugate / lengthSquared }

	val length by lazy { sqrt(lengthSquared) }
	val lengthSquared by lazy { s * s + v.lengthSquared }

	//	val normalized by lazy {
//		if (length == 0.0) Quaternion(0.0, Vector3D(0.0, 1.0, 0.0)) else Quaternion(s / length, v / length)
//	}

	private val o_max = 2.0.pow(592)
	private val o_min = 2.0.pow(510)

	val normalized by lazy {
		val x1 = s
		val x2 = v.x
		val x3 = v.y
		val x4 = v.z
		val abs1 = abs(x1)
		val abs2 = abs(x2)
		val abs3 = abs(x3)
		val abs4 = abs(x4)
		var y1: Double
		var y2: Double
		var y3: Double
		var y4: Double
		var o = 0
		var m = abs1
		if (m < abs2) {
			m = abs2
			if (m < abs3) {
				m = abs3
			}
			if (m < abs4) {
				m = abs4
			}
		} else {
			if (m < abs3) {
				m = abs3
				if (m < abs4) {
					m = abs4
				}
			} else {
				if (m >= abs4) {
					if (m == 0.0) {
						return@lazy Quaternion(0.0, Vector3D(0.0, 1.0, 0.0))
					}
					m = abs4
				}
			}
		}

		if (m >= /*t_min*/Double.MIN_VALUE) {
			if (m <= /*t_max*/Double.MAX_VALUE) {
				o = 1
				y1 = x1
				y2 = x2
				y3 = x3
				y4 = x4
			} else {
//				o = `σ−1max`
				y1 = o_max * x1
				y2 = o_max * x2
				y3 = o_max * x3
				y4 = o_max * x4
			}
		} else {
//			o = `σ−1min`
			y1 = o_min * x1
			y2 = o_min * x2
			y3 = o_min * x3
			y4 = o_min * x4
		}

		val r = sqrt(y1 * y1 + y2 * y2 + y3 * y3 + y4 * y4)
		val h = 1 / r
		return@lazy Quaternion(h * y1, Vector3D(h * y2, h * y3, h * y4))
		// https://arxiv.org/pdf/1606.06508.pdf
	}

	fun transform(vector: Vector3D): Vector3D =
		v * (2.0 * (v dot vector)) + vector * (s*s - (v dot v)) + (v cross vector) * (2.0 * s)

	val asMatrix by lazy {
		Matrix3D(
			Vector3D(
				1.0 - 2.0 * v.y * v.y - 2.0 * v.z * v.z,
				2.0 * v.x * v.y - 2.0 * v.z * s,
				2.0 * v.x * v.z + 2.0 * v.y * s
			),
			Vector3D(
				2.0 * v.x * v.y + 2.0 * v.z * s,
				1.0 - 2.0 * v.x * v.x - 2.0 * v.z * v.z,
				2.0 * v.y * v.z - 2.0 * v.x * s
			),
			Vector3D(
				2.0 * v.x * v.z - 2.0 * v.y * s,
				2.0 * v.y * v.z + 2.0 * v.x * s,
				1.0 - 2.0 * v.x * v.x - 2.0 * v.y * v.y
			),
		)
	}
}
