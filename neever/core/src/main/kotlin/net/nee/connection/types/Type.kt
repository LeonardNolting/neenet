package net.nee.connection.types

import net.nee.connection.Connection
import net.nee.connection.State

sealed class Type<S : Connection.States> {
	abstract var state: State
}