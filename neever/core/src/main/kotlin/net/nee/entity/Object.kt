package net.nee.entity

import info.kunalsheth.units.generated.Mass
import net.nee.connection.Connection
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import net.nee.units.toVarInt
import java.util.*

abstract class Object(
	eid: EntityId,
	val uuid: UUID,
	type: Int,
	mass: Mass,
	position3D: Position3D,
	view: View,
	val velocity: Vector3D
) : Entity(eid, type, mass, position3D, view) {
	override suspend fun spawn(connection: Connection) = connection.send(
		net.nee.packets.server.playing.spawn.Object(eid, uuid, type.toVarInt(), position3D, view, data = 1, velocity)
	)
}