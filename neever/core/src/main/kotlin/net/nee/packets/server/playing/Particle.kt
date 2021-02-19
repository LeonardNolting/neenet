package net.nee.packets.server.playing

import net.nee.packet.data.Server
import net.nee.packet.data.Unit
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

open class Particle(
	val particleId: Int,
	val longDistance: Boolean,
	val position: Position3D,
	@Unit(Float::class) val offset: Vector3D,
	val speed: Float,
	val count: Int
) : Server<Particle>(0x22)