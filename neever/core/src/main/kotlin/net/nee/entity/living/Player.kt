package net.nee.entity.living

import net.nee.entity.EntityId
import java.util.*

typealias PlayerId = UUID

class Player(override val entityId: EntityId, val playerId: PlayerId) : Living