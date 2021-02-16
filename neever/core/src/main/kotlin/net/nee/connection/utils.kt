package net.nee.connection

import net.nee.Server
import net.nee.units.VarInt
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.core.Output
import io.ktor.utils.io.core.readFully
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.core.writeText
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeStringUtf8
import java.security.Key
import javax.crypto.Cipher
import kotlin.experimental.and

suspend fun readVarInt(readByte: suspend () -> Byte): VarInt {
	var numRead = 0
	var result = 0
	var read: Byte

	do {
		read = readByte()
		val value = read and 0b01111111
		result = result or (value.toInt() shl (7 * numRead))

		require(numRead++ <= 5) { "net.nee.units.VarInt can't be longer than 5 bytes." }
	} while ((read and 0b10000000.toByte()) != 0.toByte())

	return VarInt(result)
}

suspend fun ByteReadChannel.readVarInt() = readVarInt(::readByte)
suspend fun Input.readVarInt() = readVarInt(::readByte)

suspend fun ByteWriteChannel.writeString(string: String) {
	writeVarInt(string.length)
	writeStringUtf8(string)
}

fun Output.writeString(string: String) {
	writeVarInt(string.length)
	writeText(string)
}

suspend fun ByteWriteChannel.writeVarInt(value: VarInt) = writeFully(value.toByteArray())
fun Output.writeVarInt(value: VarInt) = writeFully(value.toByteArray())
suspend fun ByteWriteChannel.writeVarInt(value: Int) = writeVarInt(VarInt(value))
fun Output.writeVarInt(value: Int) = writeVarInt(VarInt(value))

suspend fun readString(readVarInt: suspend () -> VarInt, readFully: suspend (ByteArray) -> Unit): String {
	val length = readVarInt()
	val byteArray = ByteArray(length.toInt())
	readFully(byteArray)
	return byteArray.toString(Charsets.UTF_8)
}

suspend fun Input.readString() = readString(::readVarInt, ::readFully)

fun setUpCipher(mode: Int, transformation: String, key: Key): Cipher =
	Cipher.getInstance(transformation).apply { init(mode, key) }

fun setUpCipher(mode: Int, key: Key) =
	setUpCipher(mode, key.algorithm, key)

fun decrypt(bytes: ByteArray): ByteArray =
	setUpCipher(2, Server.keypair.private).doFinal(bytes)