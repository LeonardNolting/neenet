package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.packet.data.Unit
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import java.util.*

fun foo() = false

class Object(
	eid: EntityId,
	val uuid: UUID,
	val type: VarInt,
	position: Position3D,
	view: View,
	val data: Int,
	@Unit(Short::class) val velocity: Vector3D,
) : Spawn.PositionView<Object>(0x00, eid, position, view)