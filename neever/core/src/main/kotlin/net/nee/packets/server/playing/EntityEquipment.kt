package net.nee.packets.server.playing

import net.nee.entity.EntityId
import net.nee.units.VarInt
import net.nee.units.toVarInt
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import kotlin.experimental.or

class EntityEquipment(
	eid: EntityId,
	val entries: List<Entry>
) : net.nee.packet.data.Server<EntityEquipment>(0x47) {
	val eid = eid.toVarInt()

	data class Entry(val slot: Slot, val item: Item) {
		sealed class Item(val present: Boolean) {
			class Empty : Item(false)
			data class Filled(
				val id: VarInt,
				val amount: Byte,
				val nbt: NBTCompound? = null
			) : Item(true)
		}
	}

	enum class Slot(val id: Byte) {
		MAIN_HAND(0),
		OFF_HAND(1),
		BOOTS(2),
		LEGGINGS(3),
		CHESTPLATE(4),
		HELMET(5);

		fun toByte(following: Boolean) =
			if (following) id or 0b10000000.toByte()
			else id
	}
}