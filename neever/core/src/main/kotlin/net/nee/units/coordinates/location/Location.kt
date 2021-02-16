package net.nee.units.coordinates.location

import net.nee.units.coordinates.Coordinates
import net.nee.units.coordinates.position.Position2D
import net.nee.units.coordinates.position.Position3D

abstract class Location<L : Location<L>> : Coordinates<Int> {
	abstract val minPosition2D: Position2D
	abstract val maxPosition2DExclusive: Position2D
	abstract val minPosition3D: Position3D
	abstract val maxPosition3DExclusive: Position3D

	open fun contains(position2D: Position2D) = position2D >= minPosition2D && position2D < maxPosition2DExclusive
	open fun contains(position3D: Position3D) = position3D >= minPosition3D && position3D < maxPosition3DExclusive
}