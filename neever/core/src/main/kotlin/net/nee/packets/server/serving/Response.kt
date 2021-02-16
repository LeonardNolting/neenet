package net.nee.packets.server.serving

import net.nee.packet.data.Server

data class Response(
	val string: String
) : Server<Response>(0x00)