package net.nee.api.mojang.session

import net.nee.api.Api
import net.nee.api.mojang.Mojang

abstract class Session<R : Session.Result> : Mojang<R>() {
	protected val base = base("sessionserver") + "session/minecraft/"

	interface Result : Api.Result
}