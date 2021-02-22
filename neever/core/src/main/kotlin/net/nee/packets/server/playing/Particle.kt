package net.nee.packets.server.playing

import net.nee.packet.data.Server
import net.nee.packet.data.Unit
import net.nee.particles.ParticleType
import net.nee.particles.Particles
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D

//class Particle(
//	val particleId: Int,
//	val longDistance: Boolean,
//	val position: Position3D,
//	@Unit(Float::class) val offset: Vector3D,
//	val speed: Float,
//	val count: Int
//) : Server<Particle>(0x22)

sealed class Particle(
	val particleId: Int,
	val longDistance: Boolean,
	val position: Position3D,
	@Unit(Float::class) val offset: Vector3D,
	val speed: Float,
	val count: Int
) : Server<Particle>(0x22) {
	class Normal(
		particleId: Int,
		longDistance: Boolean,
		position: Position3D,
		@Unit(Float::class) offset: Vector3D,
		speed: Float,
		count: Int
	) : Particle(particleId, longDistance, position, offset, speed, count) {
		constructor(
			particleType: ParticleType.Normal,
			longDistance: Boolean,
			position: Position3D,
			offset: Vector3D,
			speed: Float,
			count: Int,
		) : this(particleType.id, longDistance, position, offset, speed, count)

		constructor(particles: Particles<ParticleType.Normal>, position: Position3D) : this(
			particles.type.id,
			particles.longDistance,
			position,
			particles.offset,
			particles.speed,
			particles.count
		)
	}

	class Dust(
		particleId: Int,
		longDistance: Boolean,
		position: Position3D,
		@Unit(Float::class) offset: Vector3D,
		speed: Float,
		count: Int,
//		val colorScale: Int,
		val red: Float,
		val green: Float,
		val blue: Float,
		val scale: Float
	) : Particle(particleId, longDistance, position, offset, speed, count) {
		constructor(
			particleType: ParticleType.Dust,
			longDistance: Boolean,
			position: Position3D,
			offset: Vector3D,
			speed: Float,
			count: Int
		) : this(
			particleType.id,
			longDistance,
			position,
			offset,
			speed,
			count,
			particleType.red,
			particleType.green,
			particleType.blue,
			particleType.scale
		)
//		) : this(particle.id, longDistance, position, offset, speed, count,
//			((particle.red * 255).toInt() shl 24) or ((particle.green * 255).toInt() shl 16) or((particle.blue * 255).toInt() shl 8) or((particle.scale * 255).toInt() shl 0)
//		)

		constructor(particles: Particles<ParticleType.Dust>, position: Position3D) : this(
			particles.type,
			particles.longDistance,
			position,
			particles.offset,
			particles.speed,
			particles.count
		)
	}

	companion object {
		operator fun invoke(
			particleType: ParticleType,
			longDistance: Boolean,
			position: Position3D,
			offset: Vector3D,
			speed: Float,
			count: Int,
		) = when (particleType) {
			is ParticleType.Normal -> Normal(
				particleType,
				longDistance,
				position,
				offset,
				speed,
				count
			)
			is ParticleType.Dust -> Dust(particleType, longDistance, position, offset, speed, count)
		}

		@Suppress("UNCHECKED_CAST")
		operator fun <T : ParticleType> invoke(
			particles: Particles<T>,
			position: Position3D
		) = when (particles.type) {
			is ParticleType.Normal -> Normal(particles as Particles<ParticleType.Normal>, position)
			is ParticleType.Dust -> Dust(particles as Particles<ParticleType.Dust>, position)
			else -> TODO("Implement this function for ${particles.type::class}")
		}
	}
}