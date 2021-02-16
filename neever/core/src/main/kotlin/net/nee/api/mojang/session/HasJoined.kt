package net.nee.api.mojang.session

import io.ktor.client.request.get
import net.nee.Server

class HasJoined(
	username: String,
	serverId: String
) : Session<HasJoined.Result>() {
	data class Result(
		val id: String,
		val name: String,
		val properties: List<Properties>
	) : Session.Result {
		data class Properties(
			val name: String,
			val value: String,
			val signature: String
		)
	}

	override val url = "${base}hasJoined?username=${encode(username)}&serverId=${encode(serverId)}"
	override suspend fun get(url: String) = Server.client.get<Result>(url)
}