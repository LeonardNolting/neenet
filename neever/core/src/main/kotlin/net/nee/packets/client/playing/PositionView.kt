package net.nee.packets.client.playing

import net.nee.packet.data.Client
import net.nee.packet.data.Unit
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D

data class PositionView(
	val position: Position3D,
	@Unit(Float::class) val view: View,
) : Client<PositionView>()