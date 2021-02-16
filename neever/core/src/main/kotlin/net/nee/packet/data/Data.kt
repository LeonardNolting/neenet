package net.nee.packet.data

import net.nee.packet.Packet
import net.nee.connection.Connection
import net.nee.Server

sealed class Data<D : Data<D, E>, E : net.nee.events.packet.Packet<D>> {
	abstract fun packetEvent(connection: Connection, packet: Packet<D>): E
	fun emit(connection: Connection, packet: Packet<D>) {
		val packetEvent = packetEvent(connection, packet)
		@Suppress("UNCHECKED_CAST")
		Server.events.emit(packet.e, packetEvent)

		/*if (!packetEvent.cancelled)
			net.nee.Server.events.emit(
				*events,
				*events(packet).toTypedArray()
			)*/
	}

	protected suspend fun run(connection: Connection, packet: Packet<D>) {
		before(connection, packet)
		emit(connection, packet)
		after(connection, packet)
	}

	protected open suspend fun before(connection: Connection, packet: Packet<D>) {}
	protected open suspend fun after(connection: Connection, packet: Packet<D>) {}

	annotation class Method
}