package net.nee.physics

import net.nee.Server
import net.nee.particles.ParticleType
import net.nee.particles.Particles
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

class ParticleCube(
	val sideLength: Double,
	particles: Particles<*>,
	pos: Position3D,
	vel: Vector3D,
	rotation: Quaternion,
	angularVelocity: Vector3D,
	mass: Double
) : ParticleBody(particles, pos, vel, rotation, angularVelocity, mass) {
	override suspend fun tick(dt: Double) {
		super.tick(dt)
		val matrix = rotation.normalized.asMatrix
		val env =
			Particles.DrawingEnvironment(Server.connections.toList().filter { it.hasJoined }, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength) * -0.5), matrix * sideLength)
		val edge = 20
		val face = 10
		val faceParticles = Particles(
			ParticleType.Dust(0.0F, 1F, 1.0F, 0.4F),
			false,
			Vector3D.ZERO,
			0F,
			0
		)
		val cornerParticles = Particles(
			ParticleType.Dust(0.2F, 0.8F, 0.2F, 1F),
			false,
			Vector3D.ZERO,
			0F,
			4
		)

		with(env) {
			draw(cornerParticles, Vector3D(0.0, 0.0, 0.0))
			draw(cornerParticles, Vector3D(0.0, 0.0, 1.0))
			draw(cornerParticles, Vector3D(0.0, 1.0, 0.0))
			draw(cornerParticles, Vector3D(0.0, 1.0, 1.0))
			draw(cornerParticles, Vector3D(1.0, 0.0, 0.0))
			draw(cornerParticles, Vector3D(1.0, 0.0, 1.0))
			draw(cornerParticles, Vector3D(1.0, 1.0, 0.0))
			draw(cornerParticles, Vector3D(1.0, 1.0, 1.0))

			drawLineFromTo(particles, Vector3D(0.0, 0.0, 0.0), Vector3D(0.0, 0.0, 1.0), edge)
			drawLineFromTo(particles, Vector3D(0.0, 0.0, 0.0), Vector3D(1.0, 0.0, 0.0), edge)
			drawLineFromTo(particles, Vector3D(0.0, 0.0, 1.0), Vector3D(1.0, 0.0, 1.0), edge)
			drawLineFromTo(particles, Vector3D(1.0, 0.0, 0.0), Vector3D(1.0, 0.0, 1.0), edge)

			drawLineFromTo(particles, Vector3D(0.0, 1.0, 0.0), Vector3D(0.0, 1.0, 1.0), edge)
			drawLineFromTo(particles, Vector3D(0.0, 1.0, 0.0), Vector3D(1.0, 1.0, 0.0), edge)
			drawLineFromTo(particles, Vector3D(0.0, 1.0, 1.0), Vector3D(1.0, 1.0, 1.0), edge)
			drawLineFromTo(particles, Vector3D(1.0, 1.0, 0.0), Vector3D(1.0, 1.0, 1.0), edge)

			drawLineFromTo(particles, Vector3D(0.0, 0.0, 0.0), Vector3D(0.0, 1.0, 0.0), edge)
			drawLineFromTo(particles, Vector3D(0.0, 0.0, 1.0), Vector3D(0.0, 1.0, 1.0), edge)
			drawLineFromTo(particles, Vector3D(1.0, 0.0, 0.0), Vector3D(1.0, 1.0, 0.0), edge)
			drawLineFromTo(particles, Vector3D(1.0, 0.0, 1.0), Vector3D(1.0, 1.0, 1.0), edge)

//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D.ZERO,
//				Vector3D(1.0, 0.0, 0.0),
//				Vector3D(0.0, 0.0, 1.0),
//				face
//			)
//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D.ZERO,
//				Vector3D(0.0, 1.0, 0.0),
//				Vector3D(0.0, 0.0, 1.0),
//				face
//			)
//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D.ZERO,
//				Vector3D(0.0, 1.0, 0.0),
//				Vector3D(1.0, 0.0, 0.0),
//				face
//			)
//
//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D(1.0, 1.0, 1.0),
//				Vector3D(-1.0, 0.0, 0.0),
//				Vector3D(0.0, 0.0, -1.0),
//				face
//			)
//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D(1.0, 1.0, 1.0),
//				Vector3D(0.0, -1.0, 0.0),
//				Vector3D(0.0, 0.0, -1.0),
//				face
//			)
//			drawRandomizedParallelogram(
//				faceParticles,
//				Vector3D(1.0, 1.0, 1.0),
//				Vector3D(0.0, -1.0, 0.0),
//				Vector3D(-1.0, 0.0, 0.0),
//				face
//			)
		}

		/*
			particles.drawLine(it, pos, matrix.transform(Vector3D(0.0, sideLength, sideLength)), 10)
			particles.drawLine(it, pos, matrix.transform(Vector3D(sideLength, 0.0, sideLength)), 10)
			particles.drawLine(it, pos, matrix.transform(Vector3D(sideLength, sideLength, 0.0)), 10)

			particles.drawLine(it, pos, matrix.transform(Vector3D(sideLength, 0.0, 0.0)), 10)
			particles.drawLine(it, pos, matrix.transform(Vector3D(0.0, sideLength, 0.0)), 10)
			particles.drawLine(it, pos, matrix.transform(Vector3D(0.0, 0.0, sideLength)), 10)

			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(0.0, -sideLength, -sideLength)), 10)
			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(-sideLength, 0.0, -sideLength)), 10)
			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(-sideLength, -sideLength, 0.0)), 10)

			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(-sideLength, 0.0, 0.0)), 10)
			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(0.0, -sideLength, 0.0)), 10)
			particles.drawLine(it, pos + matrix.transform(Vector3D(sideLength, sideLength, sideLength)), matrix.transform(Vector3D(0.0, 0.0, -sideLength)), 10)*/

//			particles.drawLine(
//				it,
//				pos + matrix.transform(Vector3D(sideLength, sideLength, 0.0)),
//				matrix.transform(Vector3D(-sideLength, -sideLength, -0.0)),
//				10
//			)
//			particles.drawLine(
//				it,
//				pos + matrix.transform(Vector3D(sideLength, 0.0, sideLength)),
//				matrix.transform(Vector3D(-sideLength, -0.0, -sideLength)),
//				10
//			)
//			particles.drawLine(
//				it,
//				pos + matrix.transform(Vector3D(0.0, sideLength, sideLength)),
//				matrix.transform(Vector3D(-0.0, -sideLength, -sideLength)),
//				10
//			)
	}

//		println(matrix)
//		println(matrix.determinant)
//		println()
//		val density = 50
//		repeat(density) { i ->
//			val dx = i * (1.0 / density) * sideLength
//			repeat(density) { j ->
//				val dy = j * (1.0 / density) * sideLength
//				repeat(density) { k ->
//					val dz = k * (1.0 / density) * sideLength
//					val d = matrix.transform(Vector3D(dx, dy, dz))
//					val onSurface =
//						i == 0 || i == (density - 1) || j == 0 || j == (density - 1) || k == 0 || k == (density - 1)
//					if (!onSurface) return@repeat
//					val onEdge =
//						onSurface && ((i == 0 || i == (density - 1)) && (j == 0 || j == (density - 1)) || ((i == 0 || i == (density - 1)) && (k == 0 || k == (density - 1))) || ((k == 0 || k == (density - 1)) && (j == 0 || j == (density - 1))))
//					if (onSurface)
//						Server.connections.toList().filter { it.hasJoined }.forEach {
////							it.send(
////								EntityPosition(
////									0,
////									1,
////									2,
////									3,
////									false
////								)
////							)
//							it.send(
////								Particle.Normal(
//////									if (onEdge) particle else
//////										net.nee.particles.Particle.Dust(1.0F, 1.0F, 1.0F, 0.2F),
//////										net.nee.particles.Particle.Normal(13),
////									2,
////									true,
////									pos + d,
////									if (onEdge) Vector3D(0.05, 0.05, 0.05) else Vector3D.ZERO,
////									0f,
////									if (onEdge) 10 else 1
////								)
//								Particle(
//									if (onEdge) particles else
//										Particles(
//											ParticleType.Dust(1.0F, 1.0F, 1.0F, 0.2F),
//											false,
//											Vector3D.ZERO,
//											0F,
//											1
//										),
////									true,
//									pos + d,
////									if (onEdge) Vector3D(0.05, 0.05, 0.05) else Vector3D.ZERO,
////									0f,
////									if (onEdge) 10 else 1
//								)
//							)
//						}
////						Server.connections.forEach {
////							it.send(
////								Particle(
////									if (onEdge) particleId else if (onSurface) 6 else 4,
////									true,
////									pos + d,
////									if (onSurface) Vector3D(0.05, 0.05, 0.05) else Vector3D.ZERO,
////									0f,
////									if (onEdge) 10 else if (onSurface) 5 else 1
////								)
////							)
////						}
//				}
//			}
//		}
//}
}