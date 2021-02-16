package net.nee.packets.client.serving

import net.nee.connection.Connection
import net.nee.Server
import net.nee.packet.Packet
import net.nee.packet.data.Client
import net.nee.packets.server.serving.Response
import kotlin.random.Random

class Request : Client<Request>() {
	override suspend fun after(connection: Connection, packet: Packet<Request>) {
		connection.send(
			Response(
				"""
	                {
	                    "version": {
	                        "name": "${Server.Version.name}",
	                        "protocol": ${Server.Version.protocol}
	                    },
	                    "players": {
	                        "max": 100,
	                        "online": ${Random.nextInt(0, 100)},
	                        "sample": []
	                    },
	                    "description": {
	                        "text": "Hello world"
	                    }
	                }
	            """.trimIndent()
			)
		)
	}
}