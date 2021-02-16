package net.nee.events.packet

import net.nee.connection.Connection
import net.nee.packet.data.Server

class Send<D : Server<D>>(
	connection: Connection,
	packet: net.nee.packet.Packet<D>
) : Packet<D>(connection, packet)