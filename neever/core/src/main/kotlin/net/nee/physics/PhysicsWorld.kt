package net.nee.physics

import net.nee.units.coordinates.vector.Vector3D
import kotlin.math.pow

object PhysicsWorld : PhysicsTickable {
	const val G = 10.0

	val objects = mutableListOf<PhysicsObject>()

	override suspend fun tick(dt: Double) {
//		println("PHYSICS TICK")
		objects.forEach {
//			println()
			var netForce = Vector3D.ZERO
			objects.forEach inner@{ other ->
				if (it === other) return@inner
				netForce += (it.pos to other.pos).withLength((G * ((it.mass * other.mass) / it.pos.distanceTo(other.pos).pow(2))))//.also(::println)
			}
			it.vel += netForce / it.mass * dt
//			println(netForce)
//			println(netForce / it.mass)
//			println(netForce / it.mass * dt)
		}
		objects.forEach {
			it.lastPos = it.pos
			it.pos += it.vel
//			println(it.vel)
//			println(it.pos)
//			println()
			it.tick(dt)
		}
	}
}