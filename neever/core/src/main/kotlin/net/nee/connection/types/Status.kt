package net.nee.connection.types

import net.nee.connection.Connection
import net.nee.connection.State
import net.nee.packets.client.serving.Ping
import net.nee.packets.client.serving.Request

/**
 * States:
 * Handshaking,
 * Serving
 */
object Status : Type<Status.States>() {
	override var state = States.SERVING

	object States : Connection.States() {
		val SERVING = State(
			0x00 to Request::class,
			0x01 to Ping::class,
		)
	}
}