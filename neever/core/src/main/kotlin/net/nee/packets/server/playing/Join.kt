package net.nee.packets.server.playing

import info.kunalsheth.units.generated.Kilogram
import kotlinx.coroutines.runBlocking
import net.nee.connection.Connection
import net.nee.entities.ball.Football
import net.nee.entity.EntityId
import net.nee.entity.GameMode
import net.nee.packet.Packet
import net.nee.packet.data.Server
import net.nee.packet.data.Unit
import net.nee.physics.PhysicsEntity
import net.nee.physics.PhysicsWorld
import net.nee.units.*
import net.nee.units.coordinates.location.chunk.ChunkLocation2D
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTTypes
import java.util.*

data class Join(
	val eid: EntityId = 0,
	val isHardcore: Boolean = false,
	val gameMode: GameMode = GameMode.CREATIVE,
	val previousGameMode: GameMode = GameMode.NONE,
	val worldNames: List<String> = listOf(defaultWorldName),
	val dimensionCodec: NBTCompound = defaultDimensionCodec,
	val dimension: NBTCompound = defaultDimension,
	val worldName: String = defaultWorldName,
	val hashedSeed: Long = 0L,
	val maxPlayers: VarInt = 0.toVarInt(),
	@Unit(VarInt::class) val viewDistance: ViewDistance = ViewDistance(16),
	val reducedDebugInfo: Boolean = false,
	val enableRespawnScreen: Boolean = false,
	val isDebug: Boolean = false,
	val isFlat: Boolean = false,
) : Server<Join>(0x24) {
	override suspend fun afterSend(connection: Connection, packet: Packet<Join>) = connection.run {
		send(
			PlayerAbilities(
				PlayerAbilities.Flags(
					invulnerable = true,
					flying = true,
					allowFlying = true,
					creativeMode = true
				), 0.05f, 0.1f
			)
		)

		for (x in -9..9)
			for (z in -9..9)
				send(
					Chunk.New(
						ChunkLocation2D(x, z),
						0.toVarInt(),
						NBTCompound().apply {
							setLongArray("MOTION_BLOCKING", LongArray(37))
						},
						List(1024) { 1.toVarInt() },
						ByteArray(0),
						listOf()
					)
				)

		send(PositionView(Position3D(0, 100, 0), View(Angle(0), Angle(0))))

		PhysicsWorld.objects.filterIsInstance<PhysicsEntity>().forEach {
			connection.send(
				net.nee.packets.server.playing.spawn.Object(it.eid, UUID.randomUUID(), VarInt(71), it.pos, View(Angle(0.0), Angle(0.0)), data = 1, it.vel)
			)
		}

		connection.hasJoined = true

//		repeat(1) {
//			Football(
//				eid = 1 + it,
//				UUID.randomUUID(),
//				0.5.Kilogram,
//				Position3D(0, 100, 2),
//				View(Angle(0), Angle(0)),
//				Vector3D(0.0, 0.0, 0.0),
//				itemId = 428
//			).spawn(connection)
//		}
//		repeat(1) {
//			var pos = Position3D(0.0, 100.0, 0.0)
//			var vel = Vector3D(0.0, 1.0, 0.0)
//			val mass = 1.0
//
////			var kin = 1.0
////			var dir = -1.0
////			var vel: Double
//			val position3D = Position3D(5.0, 100.0, 0.0)
//			Football(
//				eid = 2 + it,
//				UUID.randomUUID(),
//				0.5.Kilogram,
//				position3D,
//				View(Angle(0), Angle(0)),
//				Vector3D(0.0, 0.0, 0.0),
//				itemId = 428
//			).spawn(connection)
//			Timer("physics", true)
//				.schedule(
//					object : TimerTask() {
//						override fun run() {
////							if (y < 90) {
////								vel *= -1
////								vel = vel.sign * (abs(vel) - 0.2)
////								if (abs(vel) < 0.2) vel = 0.0
////							} else {
////								vel -= 0.1
////							}
////							pos += vel
////							kin = max(0.0, kin - 0.01)
////							if (y < 90)
////								dir *= -1
////							vel = kin * dir
////							y += vel
////							println(kin)
////							println(vel)
////							println(y)
//							val forces =
//								mutableListOf<Vector3D>(
//									// gravity
////									Vector3D(0.0, -9.81 / 20, 0.0),
//									// friction
////									if (vel.length > 0) -(vel / vel.length) * 0.01 else Vector3D(0.0, 0.0, 0.0),
//								)
//							forces.add((position3D - pos).let { it.withLength(10.0 / it.lengthSquared) })
//							vel += forces.reduce { acc, cur -> acc + cur } / mass
//							println(vel)
//							if (vel.lengthSquared > 64) {
//								println("TOO FAST")
//								vel = (vel / vel.length) * 8
//							}
//							pos += vel
//							runBlocking {
//								send(
//									EntityPosition(
//										1 + it,
//										(vel.x * 4096).toInt().toShort(),
//										(vel.y * 4096).toInt().toShort(),
//										(vel.z * 4096).toInt().toShort(),
//										false
//									)
//								)
//							}
//						}
//					},
//					0L,
//					50L
//				)
//			}
//			/*send(
//				net.nee.packets.server.playing.spawn.Object(
//					eid = 1 + it,
//					UUID.randomUUID(),
//					1.toVarInt(),
//					Position3D(0, 100, 2),
//					View(Angle(0), Angle(0)),
//					data = 1,
//					Vector3D(0.0, 0.0, 0.0)
//				)
//			)
//
//			send(
//				EntityEquipment(
//					eid = 1 + it, listOf(
//						EntityEquipment.Entry(
//							EntityEquipment.Slot.HELMET,
//							EntityEquipment.Entry.Item.Filled(428.toVarInt(), 1)
//						)
//					)
//				)
//			)*/
////		}
	}

	companion object {
		val defaultWorldName = "neever:football"
		val defaultDimensionCodec = NBTCompound().apply {
			set("minecraft:dimension_type", NBTCompound().apply {
				setString("type", "minecraft:dimension_type")
				set("value", NBTList<NBTCompound>(NBTTypes.TAG_Compound).apply {
					add(NBTCompound().apply {
						setString("name", "neever:football")
						setInt("id", 0)
						set("element", NBTCompound().apply {
							setByte("piglin_safe", 0)
							setByte("natural", 1)
							setFloat("ambient_light", 0f)
							setString("infiniburn", "minecraft:infiniburn_overworld")
							setByte("respawn_anchor_works", 0)
							setByte("has_skylight", 1)
							setByte("bed_works", 1)
							setString("effects", "neever:football")
							setByte("has_raids", 1)
							setInt("logical_height", 256)
							setDouble("coordinate_scale", 1.0)
							setByte("ultrawarm", 0)
							setByte("has_ceiling", 0)
						})
					})
				})
			})
			set("minecraft:worldgen/biome", NBTCompound().apply {
				setString("type", "minecraft:worldgen/biome")
				set("value", NBTList<NBTCompound>(NBTTypes.TAG_Compound).apply {
					add(NBTCompound().apply {
						setString("name", "minecraft:ocean")
						setInt("id", 0)
						set("element", NBTCompound().apply {
							setString("precipitation", "rain")
							set("effects", NBTCompound().apply {
								setInt("sky_color", 8103167)
								setInt("water_fog_color", 329011)
								setInt("fog_color", 12638463)
								setInt("water_color", 4159204)
								set("mood_sound", NBTCompound().apply {
									setInt("tick_delay", 6000)
									setDouble("offset", 2.0)
									setString("sound", "minecraft:ambient.cave")
									setInt("block_search_extent", 8)
								})
							})
							setFloat("depth", -1.0f)
							setFloat("temperature", 0.5f)
							setFloat("scale", 0.1f)
							setFloat("downfall", 0.5f)
							setString("category", "ocean")
						})
					})
					add(NBTCompound().apply {
						setString("name", "minecraft:plains")
						setInt("id", 1)
						set("element", NBTCompound().apply {
							setString("precipitation", "rain")
							set("effects", NBTCompound().apply {
								setInt("sky_color", 7907327)
								setInt("water_fog_color", 329011)
								setInt("fog_color", 12638463)
								setInt("water_color", 4159204)
								set("mood_sound", NBTCompound().apply {
									setInt("tick_delay", 6000)
									setDouble("offset", 2.0)
									setString("sound", "minecraft:ambient.cave")
									setInt("block_search_extent", 8)
								})
							})
							setFloat("depth", 0.125f)
							setFloat("temperature", 0.8f)
							setFloat("scale", 0.05f)
							setFloat("downfall", 0.4f)
							setString("category", "plains")
						})
					})
				})
			})
		}
		val defaultDimension = NBTCompound().apply {
			setString("name", "neever:football")
			setByte("piglin_safe", 0)
			setByte("natural", 1)
			setFloat("ambient_light", 0f)
			setString("infiniburn", "minecraft:infiniburn_overworld")
			setByte("respawn_anchor_works", 0)
			setByte("has_skylight", 1)
			setByte("bed_works", 1)
			setString("effects", "neever:football")
			setByte("has_raids", 1)
			setInt("logical_height", 256)
			setDouble("coordinate_scale", 1.0)
			setByte("ultrawarm", 0)
			setByte("has_ceiling", 0)
		}
	}

	/*companion object {
//		val defaultWorldName = net.nee.Server.name.toLowerCase() + ":world"
		val defaultWorldName = "neever:football"
		val defaultDimensionCodec = """
			{
			    "minecraft:dimension_type": {
			        "type": "minecraft:dimension_type",
			        "value": [
			            {
			                "name": "neever:football",
			                "id": 0,
			                "element": {
			                    "piglin_safe": 0b,
			                    "natural": 1b,
			                    "ambient_light": 0.0f,
			                    "infiniburn": "minecraft:infiniburn_overworld",
			                    "respawn_anchor_works": 0b,
			                    "has_skylight": 1b,
			                    "bed_works": 1b,
			                    "effects": "neever:football",
			                    "has_raids": 1b,
			                    "logical_height": 256,
			                    "coordinate_scale": 1.0d,
			                    "ultrawarm": 0b,
			                    "has_ceiling": 0b
			                }
			            }
			        ]
			    },
			    "minecraft:worldgen/biome": {
			        "type": "minecraft:worldgen/biome",
			        "value": [
			            {
			                "name": "minecraft:ocean",
			                "id": 0,
			                "element": {
			                    "precipitation": "rain",
			                    "effects": {
			                        "sky_color": 8103167,
			                        "water_fog_color": 329011,
			                        "fog_color": 12638463,
			                        "water_color": 4159204,
			                        "mood_sound": {
			                            "tick_delay": 6000,
			                            "offset": 2.0d,
			                            "sound": "minecraft:ambient.cave",
			                            "block_search_extent": 8
			                        }
			                    },
			                    "depth": -1.0f,
			                    "temperature": 0.5f,
			                    "scale": 0.1f,
			                    "downfall": 0.5f,
			                    "category": "ocean"
			                }
			            }
			        ]
			    }
			}
		""".trimIndent().replace(Regex("\\s"), "")
		*//*val defaultDimensionCodec = """
			{
			    "minecraft:dimension_type": {
			        type: "minecraft:dimension_type",
			        value: [
			            {
			                name: "neever:football",
			                id: 0,
			                element: {
			                    piglin_safe: 0b,
			                    natural: 1b,
			                    ambient_light: 0.0f,
			                    infiniburn: "minecraft:infiniburn_overworld",
			                    respawn_anchor_works: 0b,
			                    has_skylight: 1b,
			                    bed_works: 1b,
			                    effects: "neever:football",
			                    has_raids: 1b,
			                    logical_height: 256,
			                    coordinate_scale: 1.0d,
			                    ultrawarm: 0b,
			                    has_ceiling: 0b
			                }
			            },
			            {
			                name: "neever:football_caves",
			                id: 1,
			                element: {
			                    piglin_safe: 0b,
			                    natural: 1b,
			                    ambient_light: 0.0f,
			                    infiniburn: "minecraft:infiniburn_overworld",
			                    respawn_anchor_works: 0b,
			                    has_skylight: 1b,
			                    bed_works: 1b,
			                    effects: "neever:football",
			                    has_raids: 1b,
			                    logical_height: 256,
			                    coordinate_scale: 1.0d,
			                    ultrawarm: 0b,
			                    has_ceiling: 1b
			                }
			            },
			            {
			                name: "minecraft:the_nether",
			                id: 2,
			                element: {
			                    piglin_safe: 1b,
			                    natural: 0b,
			                    ambient_light: 0.1f,
			                    infiniburn: "minecraft:infiniburn_nether",
			                    respawn_anchor_works: 1b,
			                    has_skylight: 0b,
			                    bed_works: 0b,
			                    effects: "minecraft:the_nether",
			                    fixed_time: 18000L,
			                    has_raids: 0b,
			                    logical_height: 128,
			                    coordinate_scale: 8.0d,
			                    ultrawarm: 1b,
			                    has_ceiling: 1b
			                }
			            },
			            {
			                name: "minecraft:the_end",
			                id: 3,
			                element: {
			                    piglin_safe: 0b,
			                    natural: 0b,
			                    ambient_light: 0.0f,
			                    infiniburn: "minecraft:infiniburn_end",
			                    respawn_anchor_works: 0b,
			                    has_skylight: 0b,
			                    bed_works: 0b,
			                    effects: "minecraft:the_end",
			                    fixed_time: 6000L,
			                    has_raids: 1b,
			                    logical_height: 256,
			                    coordinate_scale: 1.0d,
			                    ultrawarm: 0b,
			                    has_ceiling: 0b
			                }
			            }
			        ]
			    },
			    "minecraft:worldgen/biome": {
			        type: "minecraft:worldgen/biome",
			        value: [
			            {
			                name: "minecraft:ocean",
			                id: 0,
			                element: {
			                    precipitation: "rain",
			                    effects: {
			                        sky_color: 8103167,
			                        water_fog_color: 329011,
			                        fog_color: 12638463,
			                        water_color: 4159204,
			                        mood_sound: {
			                            tick_delay: 6000,
			                            offset: 2.0d,
			                            sound: "minecraft:ambient.cave",
			                            block_search_extent: 8
			                        }
			                    },
			                    depth: -1.0f,
			                    temperature: 0.5f,
			                    scale: 0.1f,
			                    downfall: 0.5f,
			                    category: "ocean"
			                }
			            },
			            {
			                name: "minecraft:basalt_deltas",
			                id: 173,
			                element: {
			                    precipitation: "none",
			                    effects: {
			                        music: {
			                            replace_current_music: 0b,
			                            max_delay: 24000,
			                            sound: "minecraft:music.nether.basalt_deltas",
			                            min_delay: 12000
			                        },
			                        sky_color: 7254527,
			                        ambient_sound: "minecraft:ambient.basalt_deltas.loop",
			                        additions_sound: {
			                            sound: "minecraft:ambient.basalt_deltas.additions",
			                            tick_chance: 0.0111d
			                        },
			                        particle: {
			                            probability: 0.118093334f,
			                            options: {
			                                type: "minecraft:white_ash"
			                            }
			                        },
			                        water_fog_color: 4341314,
			                        fog_color: 6840176,
			                        water_color: 4159204,
			                        mood_sound: {
			                            tick_delay: 6000,
			                            offset: 2.0d,
			                            sound: "minecraft:ambient.basalt_deltas.mood",
			                            block_search_extent: 8
			                        }
			                    },
			                    depth: 0.1f,
			                    temperature: 2.0f,
			                    scale: 0.2f,
			                    downfall: 0.0f,
			                    category: "nether"
			                }
			            }
			        ]
			    }
			}
		""".trimIndent().replace(Regex("\\s"), "")*//*
		val defaultDimension = """
			{
			    "name": "neever:football",
			    "piglin_safe": 0b,
			    "natural": 1b,
			    "ambient_light": 0.0f,
			    "infiniburn": "minecraft:infiniburn_overworld",
			    "respawn_anchor_works": 0b,
			    "has_skylight": 1b,
			    "bed_works": 1b,
			    "effects": "neever:football",
			    "has_raids": 1b,
			    "logical_height": 256,
			    "coordinate_scale": 1.0d,
			    "ultrawarm": 0b,
			    "has_ceiling": 0b
			}
		""".trimIndent().replace(Regex("\\s"), "")
	}*/
}