package net.nee.packets.server.authenticating.encryption

import net.nee.packet.data.Server

data class Request(
	val serverId: String,
	val publicKey: ByteArray,
	val verifyToken: ByteArray,
) : Server<Request>(0x01)