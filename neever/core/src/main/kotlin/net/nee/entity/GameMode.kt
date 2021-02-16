package net.nee.entity

import io.ktor.utils.io.core.BytePacketBuilder

enum class GameMode(val id: Byte) {
	NONE(-1),
	SURVIVAL(0),
	CREATIVE(1),
	ADVENTURE(2),
	SPECTATOR(3);
}