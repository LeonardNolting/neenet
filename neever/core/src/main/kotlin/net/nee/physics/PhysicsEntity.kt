package net.nee.physics

import net.nee.Server
import net.nee.entity.EntityId
import net.nee.packets.server.playing.EntityPosition
import net.nee.packets.server.playing.EntityPositionAndView
import net.nee.packets.server.playing.EntityTeleport
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

class PhysicsEntity(val eid: EntityId, pos: Position3D, vel: Vector3D, rotation: Quaternion, angularVelocity: Vector3D, mass: Double) : PhysicsBody(pos, vel, rotation,
	angularVelocity, mass) {
	override suspend fun tick(dt: Double) {
		val packet =
			if (deltaPos.lengthSquared >= 64.0)
				EntityTeleport(eid, pos, View.ZERO, false)
			else
				EntityPosition(
					eid,
					(deltaPos.x * 4096).toInt().toShort(),
					(deltaPos.y * 4096).toInt().toShort(),
					(deltaPos.z * 4096).toInt().toShort(),
					false
				)
		Server.connections.filter { it.hasJoined }.forEach {
			when (packet) {
				is EntityTeleport -> it.send(packet)
				is EntityPosition -> it.send(packet)
			}
		}
	}
}