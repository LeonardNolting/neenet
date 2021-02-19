package net.nee.units

import kotlin.math.PI

@JvmInline
value class Angle(val degrees: Double) {
	constructor(degrees: Float) : this(degrees.toDouble())
	constructor(degrees: Int) : this(degrees.toDouble())
	constructor(degrees: Byte) : this(degrees.toDouble() / 256 * 360)

	fun toFloat() = degrees.toFloat()
	fun toInt() = degrees.toInt()
	fun toByte() = (degrees / 360 * 256).toInt().toByte()

	val radians get() = degrees / 180 * PI
}