package net.nee.units

import kotlin.experimental.or

@JvmInline
value class VarInt internal constructor(private val value: Int) {
	fun toInt() = value
	fun toByteArray(): ByteArray {
		val bytes = mutableListOf<Byte>()
		var x = value
		do {
			var byte = (x and 0b01111111).toByte()
			x = x ushr 7
			if (x != 0) byte = byte or 0b10000000.toByte()
			bytes += byte
		} while (x != 0)

		return bytes.toByteArray()
	}
}

fun Int.toVarInt() = VarInt(this)