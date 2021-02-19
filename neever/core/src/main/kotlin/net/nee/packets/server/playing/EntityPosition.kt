package net.nee.packets.server.playing

import net.nee.entity.EntityId
import net.nee.packet.data.Server
import net.nee.units.toVarInt

class EntityPosition(eid: EntityId, val deltaX: Short, val deltaY: Short, val deltaZ: Short, val onGround: Boolean) : Server<EntityPosition>(0x27){
	val eid = eid.toVarInt()
}