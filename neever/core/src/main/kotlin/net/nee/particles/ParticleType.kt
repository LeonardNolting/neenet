package net.nee.particles

sealed class ParticleType(val id: Int) {
	class Normal(id: Int) : ParticleType(id)

//	class Block(val blockState: Int) : Particle(Int(3))

	class Dust(val red: Float, val green: Float, val blue: Float, val scale: Float) : ParticleType(14)

	// TODO https://wiki.vg/Protocol#Particle
}
