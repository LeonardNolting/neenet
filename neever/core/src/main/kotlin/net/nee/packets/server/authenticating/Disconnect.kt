package net.nee.packets.server.authenticating

import net.nee.packet.data.Server

data class Disconnect(
	val reason: String
) : Server<Disconnect>(0x00)