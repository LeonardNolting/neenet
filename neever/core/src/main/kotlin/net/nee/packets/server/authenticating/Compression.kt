package net.nee.packets.server.authenticating

import net.nee.units.VarInt
import net.nee.connection.Connection
import net.nee.packet.data.Server
import net.nee.packet.Packet

data class Compression(
	val threshold: VarInt,
) : Server<Compression>(0x03) {
	override suspend fun before(connection: Connection, packet: Packet<Compression>) {
		// TODO set compression
	}
}