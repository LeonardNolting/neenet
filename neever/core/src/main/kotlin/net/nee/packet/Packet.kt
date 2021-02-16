package net.nee.packet

import net.nee.connection.Connection
import net.nee.events.packet.Receive
import net.nee.events.packet.Send
import net.nee.packet.data.Client
import net.nee.packet.data.Server
import net.nee.packet.data.Data
import kotlin.reflect.KType
import kotlin.reflect.typeOf

typealias PacketId = Int
fun PacketId.format() = "0x" + toString(16).padStart(2, '0')

data class Packet<D : Data<D, out net.nee.events.packet.Packet<D>>>(val d: KType, val e: KType, val data: D) {
	fun emit(connection: Connection) = data.emit(connection, this)

	companion object {
		// net.nee.Server packets
		inline operator fun <reified D : Server<D>> invoke(data: D) =
			Packet(typeOf<D>(), typeOf<Send<D>>(), data)

		// Client packets
		inline operator fun <reified D : Client<D>> invoke(data: D) =
			Packet(typeOf<D>(), typeOf<Receive<D>>(), data)
	}

	override fun toString() = "Packet(data=$data)"
}