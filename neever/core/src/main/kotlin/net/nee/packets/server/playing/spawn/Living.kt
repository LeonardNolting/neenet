package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.packet.data.Unit
import net.nee.units.Angle
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import java.util.*

class Living(
	eid: EntityId,
	val uuid: UUID,
	val type: VarInt,
	position: Position3D,
	view: View,
	val headPitch: Angle,
	@Unit(Short::class) val velocity: Vector3D,
) : Spawn.PositionView<Living>(0x02, eid, position, view)