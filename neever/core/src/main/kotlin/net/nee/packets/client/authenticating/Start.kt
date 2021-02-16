package net.nee.packets.client.authenticating

import net.nee.connection.Connection
import net.nee.Server
import net.nee.packet.Packet
import net.nee.packet.data.Client
import net.nee.packets.server.authenticating.Success
import net.nee.packets.server.authenticating.encryption.Request
import java.util.*
import kotlin.random.Random

data class Start(
	val username: String
) : Client<Start>() {
	override suspend fun before(connection: Connection, packet: Packet<Start>) = connection.run {
		username = this@Start.username
		encryption.verifyToken = ByteArray(4).apply { Random.Default.nextBytes(this) }

		if (enableEncryption)
			send(Request("", Server.keypair.public.encoded, encryption.verifyToken))
		else send(
			Success(
				UUID.fromString("baa68a58-5e36-4dd2-afd8-a6e99e38cfbd"), username
			)
		)
	}
}