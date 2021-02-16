package net.nee

import net.nee.event.Event
import net.nee.events.packet.Packet

fun main() {
	neever {
		/*val beforeHandshake = events.before { event: Receive<Handshake> ->
			println("BEFORE handshake")
			"nice"
		}
		val afterHandshake = events.after { event: Receive<Handshake>, result ->
			println("AFTER handshake: $result")
			1
		}
		afterHandshake.before { event ->
			println("before AFTER handshake")
		}
		val afterAfterHandshake = afterHandshake.after { event, result ->
			println("after AFTER handshake: $result")
		}

		afterAfterHandshake.after { event, result ->
			println("after after AFTER handshake")
		}*/

		events.before { event: Event<Packet<*>> ->
			println("Packet transaction")
		}

	}
}