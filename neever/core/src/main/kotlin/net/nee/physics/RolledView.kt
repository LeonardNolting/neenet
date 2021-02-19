package net.nee.physics

import net.nee.units.Angle

data class RolledView(val yaw: Angle, val pitch: Angle, val roll: Angle) {
	companion object {
		val ZERO = RolledView(0.0, 0.0, 0.0)

		operator fun invoke(yaw: Double, pitch: Double, roll: Double) = RolledView(Angle(yaw), Angle(pitch), Angle(roll))
	}
}