package net.nee.packets.client.serving

import net.nee.connection.Connection
import net.nee.packet.Packet
import net.nee.packet.data.Client
import net.nee.packets.server.serving.Pong

data class Ping(
	val payload: Long
) : Client<Ping>() {
	override suspend fun after(connection: Connection, packet: Packet<Ping>) {
		connection.send(Pong(payload))
	}
}