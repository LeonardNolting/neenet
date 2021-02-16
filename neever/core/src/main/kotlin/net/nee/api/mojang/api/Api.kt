package net.nee.api.mojang.api

import net.nee.api.mojang.Mojang

abstract class Api<R : Api.Result> : Mojang<R>() {
	protected val base = base("api")

	interface Result : Mojang.Result
}