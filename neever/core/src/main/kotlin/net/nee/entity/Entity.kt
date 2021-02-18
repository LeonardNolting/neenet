package net.nee.entity

import info.kunalsheth.units.generated.Mass
import net.nee.connection.Connection
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D

typealias EntityId = Int

abstract class Entity(
	val eid: EntityId,
	val type: Int,
	val mass: Mass,
	var position3D: Position3D,
	var view: View,
) {
	fun move(connection: Connection) { TODO() }
	abstract suspend fun spawn(connection: Connection)
}