package net.nee.units.coordinates.vector

import net.nee.units.coordinates.Coordinates
import kotlin.math.sqrt

sealed class Vector<V : Vector<V>> : Coordinates<Double> {
	abstract operator fun plus(other: V): V
	abstract operator fun minus(other: V): V
	abstract operator fun times(other: Double): V
	abstract operator fun div(other: Double): V
	abstract operator fun times(other: Int): V
	abstract operator fun div(other: Int): V
	operator fun rangeTo(other: V): V = other - this as V
	operator fun unaryPlus() = this
	operator fun unaryMinus() = this * -1

	abstract infix fun dot(other: V): Double
	abstract infix fun cross(other: V): V
	abstract infix fun distanceTo(other: V): V

	abstract val lengthSquared: Double
	val length by lazy {
		sqrt(lengthSquared)
	}
}