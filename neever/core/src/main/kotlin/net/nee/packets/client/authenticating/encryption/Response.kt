package net.nee.packets.client.authenticating.encryption

import net.nee.connection.Connection
import net.nee.connection.decrypt
import net.nee.Server
import net.nee.api.mojang.session.HasJoined
import net.nee.packet.Packet
import net.nee.packet.data.Client
import net.nee.packets.server.authenticating.Disconnect
import net.nee.packets.server.authenticating.Success
import java.math.BigInteger
import java.util.*
import javax.crypto.spec.SecretKeySpec

class Response(
	val sharedSecret: ByteArray,
	val verifyToken: ByteArray,
) : Client<Response>() {
	override suspend fun before(connection: Connection, packet: Packet<Response>) {
		if (!decrypt(verifyToken).contentEquals(connection.encryption.verifyToken)) {
			connection.logger.warn { "Client failed to send verify token back." }
			connection.send(Packet(Disconnect("Wrong verify token.")))
			// TODO close connection
			return
		}

		connection.encryption.sharedSecret =
			SecretKeySpec(
				decrypt(sharedSecret),
				"AES"
			)

		// TODO http get (https://ptb.discord.com/channels/@me/735515355400634411/806873107066257488)
		val hasJoined = HasJoined(
			connection.username,
			BigInteger(connection.digest("", Server.keypair.public, connection.encryption.sharedSecret)).toString(16)
		).call()
		// TODO compression
		// TODO plugin channels
		// TODO set encryption
		connection.encryption.enable()
		connection.send(
			Success(
				UUID.fromString(
					// https://wiki.vg/Protocol_Encryption#Server
					StringBuilder(hasJoined.id).apply {
						listOf(8, 13, 18, 23).forEach { insert(it, '-') }
					}.toString()
				), hasJoined.name
			)
		)
		// TODO wait (probably)
	}
}