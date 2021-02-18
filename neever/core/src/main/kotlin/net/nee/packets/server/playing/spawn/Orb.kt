package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.units.coordinates.position.Position3D

class Orb(
	eid: EntityId,
	position: Position3D,
	val value: Short,
) : Spawn.Position<Orb>(0x01, eid, position)