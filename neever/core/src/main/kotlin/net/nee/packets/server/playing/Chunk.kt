package net.nee.packets.server.playing

import net.nee.packet.data.Server
import net.nee.units.coordinates.location.chunk.ChunkLocation2D

data class Chunk(
	val location: ChunkLocation2D
) : Server<Chunk>(0x20)