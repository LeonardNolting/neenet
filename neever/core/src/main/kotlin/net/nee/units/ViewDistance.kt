package net.nee.units

@JvmInline
value class ViewDistance(/*private*/ val value: Byte) {
	init {
		require(value <= 32) { "Illegal view distance: $value > 32" }
		require(value >= 2) { "Illegal view distance: $value < 2" }
	}

	constructor(value: Int) : this(value.toByte())

	fun toInt() = value.toInt()
	fun toByte() = value
}