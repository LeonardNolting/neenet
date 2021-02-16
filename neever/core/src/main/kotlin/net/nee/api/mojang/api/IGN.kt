package net.nee.api.mojang.api

import io.ktor.client.request.get
import net.nee.Server

class IGN(
	ign: String
) : Api<IGN.Result>() {
	override val url = "${base}users/profiles/minecraft/${encode(ign)}"
	override suspend fun get(url: String) = Server.client.get<Result>(url)

	data class Result(
		val id: String,
	) : Api.Result
}