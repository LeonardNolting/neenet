package net.nee.entities.ball

import info.kunalsheth.units.generated.Mass
import net.nee.connection.Connection
import net.nee.entity.EntityId
import net.nee.entity.Object
import net.nee.packets.server.playing.EntityEquipment
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import net.nee.units.toVarInt
import java.util.*

open class Ball(
	eid: EntityId,
	uuid: UUID,
	mass: Mass,
	position3D: Position3D,
	view: View,
	velocity: Vector3D,

	val itemId: Int,
) : Object(eid, uuid, 1, mass, position3D, view, velocity) {
	override suspend fun spawn(connection: Connection) {
		super.spawn(connection)
		connection.send(
			EntityEquipment(
				eid, listOf(
					EntityEquipment.Entry(
						EntityEquipment.Slot.HELMET,
						EntityEquipment.Entry.Item.Filled(itemId.toVarInt(), 1)
					)
				)
			)
		)
	}
}