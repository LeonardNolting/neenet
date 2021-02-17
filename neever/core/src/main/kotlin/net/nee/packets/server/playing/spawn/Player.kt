package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import java.util.*

class Player(
	val uuid: UUID,
	eid: EntityId,
	position: Position3D,
	view: View,
) : Spawn.PositionView<Player>(0x04, eid, position, view)
