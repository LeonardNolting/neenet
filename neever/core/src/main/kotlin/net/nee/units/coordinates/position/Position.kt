package net.nee.units.coordinates.position

import net.nee.units.coordinates.Coordinates
import net.nee.units.coordinates.vector.Vector

sealed class Position<P : Position<P>> : Coordinates<Double>, Comparable<P> {
	abstract infix fun to(other: P): Vector<*>
}