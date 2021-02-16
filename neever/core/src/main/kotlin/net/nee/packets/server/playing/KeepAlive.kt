package net.nee.packets.server.playing

import net.nee.packet.data.Server

data class KeepAlive(
	val keepAliveId: Long
) : Server<KeepAlive>(0x1F)