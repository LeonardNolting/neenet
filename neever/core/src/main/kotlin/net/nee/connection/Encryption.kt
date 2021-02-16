package net.nee.connection

import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readBytes
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class Encryption {
	var enabled = false
		private set

	lateinit var verifyToken: ByteArray
	lateinit var sharedSecret: SecretKey

	private fun cipher(mode: Int) =
		Cipher.getInstance("AES/CFB8/NoPadding").apply {
			init(mode, sharedSecret, IvParameterSpec(sharedSecret.encoded))
		}!!

	lateinit var encryptCipher: Cipher
	lateinit var decryptCipher: Cipher

	fun enable() {
		encryptCipher = cipher(Cipher.ENCRYPT_MODE)
		decryptCipher = cipher(Cipher.DECRYPT_MODE)
		enabled = true
		println("Enabled encryption")
	}

	fun decrypt(packet: ByteReadPacket): ByteReadPacket {
		if (!enabled) return packet

		val input = packet.readBytes()
		return ByteReadPacket(ByteArray(decryptCipher.getOutputSize(input.size)).apply {
			decryptCipher.update(input, 0, input.size, this)
		})
	}

	fun encrypt(packet: ByteReadPacket): ByteReadPacket {
		if (!enabled) return packet

		val result = ByteArray(encryptCipher.getOutputSize(packet.remaining.toInt()))
		val input = packet.readBytes()
		packet.release()
		encryptCipher.update(input, 0, input.size, result)
		return ByteReadPacket(result)
	}
}