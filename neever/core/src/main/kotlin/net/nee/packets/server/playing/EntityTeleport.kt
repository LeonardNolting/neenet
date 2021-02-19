package net.nee.packets.server.playing

import net.nee.entity.EntityId
import net.nee.packet.data.Server
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.toVarInt

class EntityTeleport(eid: EntityId, val pos: Position3D, val view: View, val onGround: Boolean) : Server<EntityTeleport>(0x56){
	val eid = eid.toVarInt()
}