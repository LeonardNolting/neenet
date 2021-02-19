package net.nee.units.coordinates.location

import net.nee.units.coordinates.position.Position2D

data class Location2D(
	val x: Int,
	val z: Int
) : Location<Location2D>() {
	fun toLocation3D(y: Int) = Location3D(x, y, z)
	fun toPosition2D() = Position2D(x.toDouble(), z.toDouble())

	override val minPosition2D = toPosition2D()
	override val maxPosition2DExclusive = Position2D(x + 1.0, z + 1.0)
	override val minPosition3D by lazy {
		minPosition2D.toPosition3D(0.0)
	}
	override val maxPosition3DExclusive by lazy {
		maxPosition2DExclusive.toPosition3D(257.0)
	}
}