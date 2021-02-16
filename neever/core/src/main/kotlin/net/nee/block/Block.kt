package net.nee.block

import net.nee.entity.Entity

class Block {
	interface Type {
		object GRASS : Type
		object AIR : Type
		object STONE : Type
		class Spawner<E : Entity> : Type
	}
}

/*
enum class Block {
	GRASS,
	AIR,
	STONE
}*/
