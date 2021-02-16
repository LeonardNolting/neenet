package net.nee.events.packet

import net.nee.connection.Connection
import net.nee.event.Data

sealed class Packet<D : net.nee.packet.data.Data<D, out Packet<D>>>(
	val connection: Connection,
	val packet: net.nee.packet.Packet<D>
) : Data.Passive