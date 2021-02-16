package net.nee.entity

typealias EntityId = Int

val EntityId.entity: Entity by lazy {
	TODO()
}

interface Entity {
	val entityId: EntityId
}