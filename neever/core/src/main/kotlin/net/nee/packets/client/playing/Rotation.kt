package net.nee.packets.client.playing

import net.nee.connection.Connection
import net.nee.packet.Packet
import net.nee.packet.data.Client
import net.nee.packet.data.Unit
import net.nee.packets.server.playing.Particle
import net.nee.physics.Matrix3D
import net.nee.physics.RolledView
import net.nee.units.Angle
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

class Rotation(@Unit(Float::class) val view: View, val onGround: Boolean) : Client<Rotation>() {
	override suspend fun after(connection: Connection, packet: Packet<Rotation>) {
		println(view)
		val rolledView = RolledView(view.yaw, view.pitch, Angle(0.0))
		println(rolledView)
		val matrix3D = Matrix3D(rolledView)
		println(matrix3D)
		println(matrix3D.x)
		println(matrix3D.y)
		println(matrix3D.z)
		println(matrix3D.rotationAsRolledView)
		suspend fun particleVector(vector: Vector3D, id: Int) {
			repeat(10) {
				connection.send(
					Particle(
						id,
						true,
						Position3D(0.0, 101.62, 0.0) + vector * (0.5 + it / 10.0),
						Vector3D.ZERO,
						0f,
						5
					)
				)
			}
		}
//		particleVector(matrix3D.x, 26)
//		particleVector(matrix3D.y, 6)
//		particleVector(matrix3D.z.normalized, 4)
		particleVector(view.asVector, 26)
	}
}