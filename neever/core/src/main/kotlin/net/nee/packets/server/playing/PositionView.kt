package net.nee.packets.server.playing

import net.nee.packet.data.Server
import net.nee.packet.data.Unit
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.toVarInt

data class PositionView(
	val position: Position3D,
	@Unit(Float::class) val view: View,
	val flags: Byte = 0.toByte(), // TODO
	val teleportId: VarInt = 0.toVarInt(), // TODO
) : Server<PositionView>(0x34)