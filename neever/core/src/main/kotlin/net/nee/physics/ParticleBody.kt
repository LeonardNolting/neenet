package net.nee.physics

import net.nee.particles.Particles
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

abstract class ParticleBody(val particles: Particles<*>, pos: Position3D, vel: Vector3D, rotation: Quaternion, angularVelocity: Vector3D, mass: Double) :
	PhysicsBody(pos, vel, rotation, angularVelocity, mass)