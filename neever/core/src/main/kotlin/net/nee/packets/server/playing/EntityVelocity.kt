package net.nee.packets.server.playing

import net.nee.entity.EntityId
import net.nee.packet.data.Server
import net.nee.units.toVarInt

class EntityVelocity(eid: EntityId, val velocityX: Short, val velocityY: Short, val velocityZ: Short) : Server<EntityVelocity>(0x46){
	val eid = eid.toVarInt()
}