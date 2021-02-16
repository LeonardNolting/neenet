package net.nee.connection

import net.nee.Server
import net.nee.packet.PacketId
import net.nee.packet.data.Client
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class State internal constructor(vararg allowedClientPackets: Pair<PacketId, KClass<out Client<*>>>) {
	val allowedClientPackets =
		allowedClientPackets.map { (id, packet) ->
			check(packet.isSubclassOf(Client::class)) { "Allowed Client Packet (in State $this) $packet must inherit from ${Client::class}." }
			id to (Server.packets[packet] ?: error("Please register packets using `register`."))
		}.toMap()
}