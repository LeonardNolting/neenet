package net.nee.packets.client.playing

import net.nee.packet.data.Client
import net.nee.units.ViewDistance

data class Settings(
	val locale: String,
	val viewDistance: ViewDistance,
	val chatMode: ChatMode,
	val chatColors: Boolean,
	val displayedSkinParts: UByte,
	val mainHand: MainHand,
) : Client<Settings>() {
	enum class ChatMode(val int: Int) {
		ENABLED(0),
		COMMANDS_ONLY(1),
		HIDDEN(2)
	}

	enum class MainHand(val int: Int) {
		LEFT(0),
		RIGHT(1)
	}
}