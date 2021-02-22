package net.nee.particles

import net.nee.connection.Connection
import net.nee.packets.server.playing.Particle
import net.nee.physics.Matrix3D
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import kotlin.random.Random

data class Particles<out T : ParticleType>(
	val type: T,
	val longDistance: Boolean,
	val offset: Vector3D,
	val speed: Float,
	val count: Int
) {
	data class DrawingEnvironment(val connections: List<Connection>, val position: Position3D, val matrix: Matrix3D) {
		suspend fun draw(particles: Particles<*>, position: Vector3D) =
			connections.forEach { it.send(Particle(particles, this.position + matrix.transform(position))) }

		suspend fun drawStepped(particles: Particles<*>, position: Vector3D, step: Vector3D, steps: Int) {
			repeat(steps) {
				draw(particles, position + step * it)
			}
		}

		suspend fun drawLine(particles: Particles<*>, position: Vector3D, length: Vector3D, steps: Int) {
			drawStepped(particles, position, length / steps, steps)
		}

		suspend fun drawRandomizedLine(particles: Particles<*>, position: Vector3D, length: Vector3D, count: Int) {
			repeat(count) {
				draw(particles, position + length * Random.nextDouble())
			}
		}

		suspend fun drawLineFromTo(particles: Particles<*>, from: Vector3D, to: Vector3D, steps: Int) {
			drawLine(particles, from, from to to, steps)
		}

		suspend fun drawRandomizedLineFromTo(particles: Particles<*>, from: Vector3D, to: Vector3D, count: Int) {
			drawRandomizedLine(particles, from, from to to, count)
		}

		suspend fun drawRandomizedParallelogram(particles: Particles<*>, position: Vector3D, a: Vector3D, b: Vector3D, count: Int) {
			repeat(count) {
				draw(particles, position + a * Random.nextDouble() + b * Random.nextDouble())
			}
		}
	}
}