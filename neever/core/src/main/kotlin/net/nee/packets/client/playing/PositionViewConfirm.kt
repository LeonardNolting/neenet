package net.nee.packets.client.playing

import net.nee.packet.data.Client
import net.nee.units.VarInt

data class PositionViewConfirm(
	val teleportId: VarInt
) : Client<PositionViewConfirm>()