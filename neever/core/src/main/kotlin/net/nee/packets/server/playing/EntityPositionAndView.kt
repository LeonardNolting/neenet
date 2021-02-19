package net.nee.packets.server.playing

import net.nee.entity.EntityId
import net.nee.packet.data.Server
import net.nee.units.View
import net.nee.units.toVarInt

class EntityPositionAndView(eid: EntityId, val deltaX: Short, val deltaY: Short, val deltaZ: Short, val view: View, val onGround: Boolean) : Server<EntityPositionAndView>(0x27){
	val eid = eid.toVarInt()
}