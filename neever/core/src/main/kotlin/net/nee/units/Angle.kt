package net.nee.units

@JvmInline
value class Angle(private val degrees: Double) {
	constructor(degrees: Float) : this(degrees.toDouble())
	constructor(degrees: Int) : this(degrees.toDouble())
	constructor(degrees: Byte) : this(degrees.toDouble() / 256 * 360)

	fun toFloat() = degrees.toFloat()
	fun toInt() = degrees.toInt()
	fun toByte() = (degrees / 360 * 256).toInt().toByte()
}