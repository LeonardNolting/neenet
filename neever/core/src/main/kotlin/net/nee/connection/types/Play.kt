package net.nee.connection.types

import net.nee.connection.Connection
import net.nee.connection.State
import net.nee.packets.client.authenticating.Start
import net.nee.packets.client.authenticating.encryption.Response
import net.nee.packets.client.playing.KeepAlive
import net.nee.packets.client.playing.PositionViewConfirm
import net.nee.packets.client.playing.Rotation
import net.nee.packets.client.playing.Settings

/**
 * States:
 * Handshaking,
 * Authenticating,
 * Playing
 */
class Play : Type<Play.States>() {
	override var state = States.AUTHENTICATING

	var encrypted = false
	var compressed = false

	object States : Connection.States() {
		val AUTHENTICATING = State(
			0x00 to Start::class,
			0x01 to Response::class
		)

		val PLAYING = State(
			0x05 to Settings::class,
			0x10 to KeepAlive::class,
			0x00 to PositionViewConfirm::class,
			0x14 to Rotation::class
		)
	}
}