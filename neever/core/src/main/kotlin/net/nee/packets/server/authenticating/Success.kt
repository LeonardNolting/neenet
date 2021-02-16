package net.nee.packets.server.authenticating

import net.nee.connection.Connection
import net.nee.connection.types.Play
import net.nee.packet.data.Server
import net.nee.packet.Packet
import net.nee.packets.server.playing.Join
import java.util.*

data class Success(
	val uuid: UUID,
	val username: String,
) : Server<Success>(0x02) {
	override suspend fun afterSend(connection: Connection, packet: Packet<Success>) {
		connection.type.state = Play.States.PLAYING
		// TODO wait
		connection.send(Join())
	}
}