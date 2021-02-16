package net.nee.packets.client.handshaking

import net.nee.units.VarInt
import net.nee.connection.Connection
import net.nee.connection.readVarInt
import io.ktor.utils.io.core.ByteReadPacket
import net.nee.connection.types.Play
import net.nee.connection.types.Status
import net.nee.connection.types.Type
import net.nee.packet.Packet
import net.nee.packet.data.Client

data class Handshake(
	val protocolVersion: VarInt,
	val serverAddress: String,
	val serverPort: UShort,
	val intention: Intention,
) : Client<Handshake>() {
	override suspend fun before(connection: Connection, packet: Packet<Handshake>) {
		connection.type = packet.data.intention.connectionType()
	}

	enum class Intention(val int: Int, val connectionType: () -> Type<*>) {
		STATUS(1, { Status }),

		// Login
		PLAY(2, { Play() })
	}

	companion object : Client.Handler<Handshake>(Handshake::class) {
		override suspend fun Connection.parse(packet: ByteReadPacket) = packet.run {
			Handshake(
				read(),
				read(),
				read(),
				readVarInt().let { intention ->
					Intention.values().find { it.int == intention.toInt() }
						?: error("Unexpected next state: $intention")
				}
			)
		}
	}
}