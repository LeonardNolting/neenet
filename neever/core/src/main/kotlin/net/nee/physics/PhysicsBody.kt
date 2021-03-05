package net.nee.physics

import net.nee.Server
import net.nee.particles.ParticleType
import net.nee.particles.Particles
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

abstract class PhysicsBody(
	pos: Position3D,
	vel: Vector3D,
	var rotation: Quaternion,
	angularVelocity: Vector3D,
	mass: Double
) :
	PhysicsObject(pos, vel, mass) {
	var angVel = rotation.asMatrix.inverse.transform(angularVelocity)
	override suspend fun tick(dt: Double) {
		pos += vel
//		rotation = Matrix3D(
//			Vector3D(0.0, -angularVelocity.z, angularVelocity.y),
//			Vector3D(angularVelocity.z, 0.0, -angularVelocity.x),
//			Vector3D(-angularVelocity.y, angularVelocity.x, 0.0)
//		).transform(rotation)
		rotation = (rotation + Quaternion(
			0.0, /*rotation.asMatrix.inverseTransform(angularVelocity)*/
			angVel
		) * rotation * 0.5).normalized//.also { println(it.length) }
	}

	override suspend fun applyForce(direction: Vector3D, position: Vector3D) {
		super.applyForce(direction, position)
//		angularVelocity += rotation.asMatrix.also{println(it)}/*.inverse*/.transform((position cross direction).also{println(it)}).also{println(it)}
//		println("Rotation: $rotation")
//		println("Rotation length: ${rotation.length}")
//		println("Rotation determinant: ${rotation.asMatrix.determinant}")
		angVel -= rotation.inverse.transform((position cross direction))//.also { println("Angular velocity change: ${-it}") }
//		angularVelocity += (position cross direction).also { println(it) }

		val env =
			Particles.DrawingEnvironment(Server.connections.toList().filter { it.hasJoined }, pos, Matrix3D.IDENTITY)

		env.draw(
			Particles(
				ParticleType.Dust(1F, 0F, 0F, 1F),
				false,
				Vector3D.ZERO,
				0F,
				4
			),
			position
		)
		env.drawStepped(
			Particles(
				ParticleType.Dust(1F, 0.5F, 0F, 0.4F),
				false,
				Vector3D.ZERO,
				0F,
				2
			),
			position,
			direction * 100,
			10
		)
	}
}