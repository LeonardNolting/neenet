package net.nee.units.coordinates.location.chunk

import net.nee.units.coordinates.location.Location2D
import net.nee.units.coordinates.position.Position2D
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.location.Location3D

class ChunkLocation3D(
	val x: Int,
	val y: Int,
	val z: Int
) : ChunkLocation<ChunkLocation3D>() {
	fun toChunkLocation2D() = ChunkLocation2D(x, z)

	override fun contains(position2D: Position2D): Boolean {
		TODO("Not yet implemented")
	}

	override fun contains(position3D: Position3D): Boolean {
		TODO("Not yet implemented")
	}

	override fun contains(location2D: Location2D): Boolean {
		TODO("Not yet implemented")
	}

	override fun contains(location3D: Location3D): Boolean {
		TODO("Not yet implemented")
	}

	override val minPosition3D: Position3D
		get() = TODO("Not yet implemented")
	override val minPosition2D: Position2D
		get() = TODO("Not yet implemented")
	override val maxPosition2DExclusive: Position2D
		get() = TODO("Not yet implemented")
	override val maxPosition3DExclusive: Position3D
		get() = TODO("Not yet implemented")
	override val minLocation2D: Location2D
		get() = TODO("Not yet implemented")
	override val maxLocation2DExclusive: Location2D
		get() = TODO("Not yet implemented")
	override val minLocation3D: Location3D
		get() = TODO("Not yet implemented")
	override val maxLocation3DExclusive: Location3D
		get() = TODO("Not yet implemented")
}