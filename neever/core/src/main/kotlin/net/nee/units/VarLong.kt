package net.nee.units

import kotlin.experimental.or

@JvmInline
value class VarLong(private val value: Long) {
	fun toLong() = value
	fun toByteArray(): ByteArray {
		TODO()
	}
}