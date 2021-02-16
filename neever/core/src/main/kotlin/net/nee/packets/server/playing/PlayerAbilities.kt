package net.nee.packets.server.playing

import net.nee.packet.data.BitField

data class PlayerAbilities(
	val flags: Flags,
	val flyingSpeed: Float,
	val fov: Float
) : net.nee.packet.data.Server<PlayerAbilities>(0x30) {
	data class Flags(
		val invulnerable: Boolean,
		val flying: Boolean,
		val allowFlying: Boolean,
		val creativeMode: Boolean,
	) : BitField()
}