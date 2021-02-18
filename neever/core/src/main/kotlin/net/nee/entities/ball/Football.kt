package net.nee.entities.ball

import info.kunalsheth.units.generated.Mass
import net.nee.entity.EntityId
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import java.util.*

class Football(
	eid: EntityId,
	uuid: UUID,
	mass: Mass,
	position3D: Position3D,
	view: View,
	velocity: Vector3D,
	itemId: Int
) : Ball(eid, uuid, mass, position3D, view, velocity, itemId) {
}