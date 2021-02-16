package net.nee.events.packet

import net.nee.connection.Connection
import net.nee.packet.data.Client

class Receive<D : Client<D>>(
	connection: Connection,
	packet: net.nee.packet.Packet<D>
) : Packet<D>(connection, packet)