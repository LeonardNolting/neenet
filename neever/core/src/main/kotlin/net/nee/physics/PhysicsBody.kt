package net.nee.physics

import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

abstract class PhysicsBody(pos: Position3D, vel: Vector3D, val rotation: Matrix3D, val angularVelocity: Vector3D, mass: Double) :
	PhysicsObject(pos, vel, mass) {

}