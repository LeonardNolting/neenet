package net.nee.packets.client.playing

import net.nee.connection.Connection
import net.nee.packet.Packet
import net.nee.packet.data.Client
import java.time.Instant
import java.time.temporal.ChronoUnit

data class KeepAlive(
	val keepAliveId: Long
) : Client<KeepAlive>() {
	override suspend fun after(connection: Connection, packet: Packet<KeepAlive>) {
//		connection.lastKeepAliveRequest = System.currentTimeMillis()
		connection.waitingForKeepAliveResponse = false
		connection.logger.info { "Ping: ${connection.lastKeepAliveRequest.until(Instant.now(), ChronoUnit.MILLIS)}ms" }
	}
}