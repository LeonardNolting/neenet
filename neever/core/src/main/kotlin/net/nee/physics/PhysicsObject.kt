package net.nee.physics

import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

abstract class PhysicsObject(var pos: Position3D, var vel: Vector3D, val mass: Double) : PhysicsTickable {
	var lastPos: Position3D = pos
	val deltaPos get() = pos - lastPos

	open suspend fun applyForce(direction: Vector3D, position: Vector3D) {
		vel += direction / mass // TODO dt
	}
}