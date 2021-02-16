package net.nee.packets.server.playing

import io.ktor.utils.io.core.BytePacketBuilder
import net.nee.packet.data.Server
import net.nee.units.VarInt
import net.nee.units.coordinates.location.chunk.ChunkLocation2D
import org.jglrxavpok.hephaistos.nbt.NBTCompound

sealed class Chunk(
	val location: ChunkLocation2D,
	val sections: VarInt,
	val heightMaps: NBTCompound,
	val data: ByteArray,
	val blockEntities: List<NBTCompound>,
) : Server<Chunk>(0x20) {
	class Change(
		location: ChunkLocation2D,
		sections: VarInt,
		heightMaps: NBTCompound,
		data: ByteArray,
		blockEntities: List<NBTCompound>
	) : Chunk(location, sections, heightMaps, data, blockEntities) {
		override suspend fun BytePacketBuilder.prepare() {
			write(location)
			// fullChunk
			write(false)
			write(sections)
			write(heightMaps)
			write(data)
			write(blockEntities)
		}
	}

	class New(
		location: ChunkLocation2D,
		sections: VarInt,
		heightMaps: NBTCompound,
		val biomes: List<VarInt>,
		data: ByteArray,
		blockEntities: List<NBTCompound>
	) : Chunk(location, sections, heightMaps, data, blockEntities) {
		override suspend fun BytePacketBuilder.prepare() {
			write(location)
			// fullChunk
			write(true)
			write(sections)
			write(heightMaps)
			write(biomes)
			write(data)
			write(blockEntities)
		}
	}
}