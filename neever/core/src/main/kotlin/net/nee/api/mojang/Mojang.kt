package net.nee.api.mojang

import net.nee.api.Api

abstract class Mojang<R : Api.Result> : Api<R>() {
	protected fun base(subdomain: String) = "https://$subdomain.mojang.com/"

	interface Result : Api.Result
}