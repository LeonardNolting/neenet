package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.packet.data.Server
import net.nee.units.View
import net.nee.units.coordinates.location.Location3D
import net.nee.units.coordinates.position.Position3D
import net.nee.units.toVarInt

sealed class Spawn<E : Spawn<E>>(
	id: Int,
	eid: EntityId
) : Server<E>(id) {
	val eid = eid.toVarInt()

	sealed class Location<E : Location<E>>(id: Int, eid: EntityId, val location: Location3D) : Spawn<E>(id, eid)
	sealed class Position<E : Position<E>>(id: Int, eid: EntityId, val position: Position3D) : Spawn<E>(id, eid)
	sealed class PositionView<E : PositionView<E>>(
		id: Int,
		eid: EntityId,
		position: Position3D,
		val view: View
	) : Position<E>(id, eid, position)
}