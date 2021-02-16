package net.nee.packets.server.playing.spawn

import net.nee.packet.data.Server
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import java.util.*

class Player(
	val eid: VarInt,
	val uuid: UUID,
	val position: Position3D,
	val view: View,
) : Server<Player>(0x04)
