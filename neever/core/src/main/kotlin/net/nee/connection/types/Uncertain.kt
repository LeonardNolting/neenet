package net.nee.connection.types

import net.nee.connection.Connection

object Uncertain : Type<Connection.States>() {
	override var state = States.HANDSHAKING

	object States : Connection.States()
}