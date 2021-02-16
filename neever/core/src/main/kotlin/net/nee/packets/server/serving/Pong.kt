package net.nee.packets.server.serving

import net.nee.packet.data.Server

data class Pong(
	val payload: Long
) : Server<Pong>(0x01)