@file:Suppress("unused")

package net.nee.packets.server.playing.spawn

import net.nee.entity.EntityId
import net.nee.units.Direction
import net.nee.units.coordinates.location.Location3D
import java.util.*

class Painting(
	eid: EntityId,
	val uuid: UUID,
	val motive: Motive,
	location: Location3D,
	val direction: Direction,
	) : Spawn.Location<Painting>(0x03, eid, location) {
	enum class Motive(
		val id: Int,
		val identifier: String,
		val x: Int,
		val y: Int,
		val width: Int,
		val height: Int
	) {
		KEBAB(0, "kebab", 0, 0, 16, 16),
		AZTEC(1, "aztec", 16, 0, 16, 16),
		ALBAN(2, "alban", 32, 0, 16, 16),
		AZTEC_2(3, "aztec2", 48, 0, 16, 16),
		BOMB(4, "bomb", 64, 0, 16, 16),
		PLANT(5, "plant", 80, 0, 16, 16),
		WASTELAND(6, "wasteland", 96, 0, 16, 16),
		POOL(7, "pool", 0, 32, 32, 16),
		COURBET(8, "courbet", 32, 32, 32, 16),
		SEA(9, "sea", 64, 32, 32, 16),
		SUNSET(10, "sunset", 96, 32, 32, 16),
		CREEBET(11, "creebet", 128, 32, 32, 16),
		WANDERER(12, "wanderer", 0, 64, 16, 32),
		GRAHAM(13, "graham", 16, 64, 16, 32),
		MATCH(14, "match", 0, 128, 32, 32),
		BUST(15, "bust", 32, 128, 32, 32),
		STAGE(16, "stage", 64, 128, 32, 32),
		VOID(17, "void", 96, 128, 32, 32),
		SKULL_AND_ROSES(18, "skull_and_roses", 128, 128, 32, 32),
		WITHER(19, "wither", 160, 128, 32, 32),
		FIGHTERS(20, "fighters", 0, 96, 64, 32),
		POINTER(21, "pointer", 0, 192, 64, 64),
		PIGSCENE(22, "pigscene", 64, 192, 64, 64),
		BURNING_SKULL(23, "burning_skull", 128, 192, 64, 64),
		SKELETON(24, "skeleton", 192, 64, 64, 48),
		DONKEY_KONG(25, "donkey_kong", 192, 112, 64, 48),
	}
}