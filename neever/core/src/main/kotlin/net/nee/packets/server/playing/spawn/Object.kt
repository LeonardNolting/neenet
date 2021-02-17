package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import java.util.*

class Object<O : Object<O>>(
	eid: EntityId,
	val uuid: UUID,
	val type: VarInt,
	position: Position3D,
	view: View,
	id: Int,
) : Spawn.PositionView<O>(id, eid, position, view) {
}