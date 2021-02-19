package net.nee.units.coordinates.location

import net.nee.units.coordinates.position.Position3D

data class Location3D(
	val x: Int,
	val y: Int,
	val z: Int
) : Location<Location3D>() {
	fun toLocation2D() = Location2D(x, z)
	fun toPosition3D() = Position3D(x.toDouble(), y.toDouble(), z.toDouble())

	override val minPosition3D = toPosition3D()
	override val maxPosition3DExclusive = Position3D(x + 1.0, y + 1.0, z + 1.0)
	override val minPosition2D by lazy {
		minPosition3D.toPosition2D()
	}
	override val maxPosition2DExclusive by lazy {
		maxPosition3DExclusive.toPosition2D()
	}
}