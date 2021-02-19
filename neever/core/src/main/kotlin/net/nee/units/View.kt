package net.nee.units

import net.nee.units.coordinates.vector.Vector3D
import kotlin.math.cos
import kotlin.math.sin

data class View(val yaw: Angle, val pitch: Angle) {
	val asVector: Vector3D by lazy {
		val x = -cos(pitch.radians) * sin(yaw.radians)
		val y = -sin(pitch.radians)
		val z =  cos(pitch.radians) * cos(yaw.radians)
		Vector3D(x, y, z)
	}

	companion object {
		val ZERO = View(0.0, 0.0)

		operator fun invoke(yaw: Double, pitch: Double) =
			View(Angle(yaw), Angle(pitch))
	}
}