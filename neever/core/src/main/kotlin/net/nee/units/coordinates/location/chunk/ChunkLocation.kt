package net.nee.units.coordinates.location.chunk

import net.nee.units.coordinates.position.Position2D
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.location.Location
import net.nee.units.coordinates.location.Location2D
import net.nee.units.coordinates.location.Location3D

sealed class ChunkLocation<CL : ChunkLocation<CL>> : Location<CL>() {
	abstract override fun contains(position2D: Position2D): Boolean
	abstract override fun contains(position3D: Position3D): Boolean
	abstract fun contains(location2D: Location2D): Boolean
	abstract fun contains(location3D: Location3D): Boolean

	abstract val minLocation2D: Location2D
	abstract val maxLocation2DExclusive: Location2D
	abstract val minLocation3D: Location3D
	abstract val maxLocation3DExclusive: Location3D
}