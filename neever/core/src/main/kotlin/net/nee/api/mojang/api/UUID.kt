package net.nee.api.mojang.api

import io.ktor.client.request.get
import net.nee.Server

class UUID(
	uuid: String,
) : Api<UUID.Result>() {
	class Result : java.util.ArrayList<Result.Entry>(), Api.Result {
		data class Entry(
			val name: String,
			val changedToAt: String
		)
	}

	override val url = "${base}user/profiles/${encode(uuid)}/names"
	override suspend fun get(url: String) = Server.client.get<Result>(url)
}