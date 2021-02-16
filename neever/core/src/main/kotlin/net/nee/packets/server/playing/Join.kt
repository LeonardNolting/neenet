package net.nee.packets.server.playing

import net.nee.connection.Connection
import net.nee.units.VarInt
import net.nee.entity.GameMode
import net.nee.packet.data.Server
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTList
import org.jglrxavpok.hephaistos.nbt.NBTTypes
import net.nee.packet.Packet
import net.nee.packet.data.Unit
import net.nee.units.Angle
import net.nee.units.View
import net.nee.units.ViewDistance
import net.nee.units.coordinates.location.Location3D

data class Join(
	val eid: Int = 0,
	val isHardcore: Boolean = false,
	val gameMode: GameMode = GameMode.CREATIVE,
	val previousGameMode: GameMode = GameMode.NONE,
	val worldNames: List<String> = listOf(defaultWorldName),
	val dimensionCodec: NBTCompound = defaultDimensionCodec,
	val dimension: NBTCompound = defaultDimension,
	val worldName: String = defaultWorldName,
	val hashedSeed: Long = 0L,
	val maxPlayers: VarInt = VarInt(0),
	@Unit(VarInt::class) val viewDistance: ViewDistance = ViewDistance(16),
	val reducedDebugInfo: Boolean = false,
	val enableRespawnScreen: Boolean = false,
	val isDebug: Boolean = false,
	val isFlat: Boolean = false,
) : Server<Join>(0x24) {
	override suspend fun afterSend(connection: Connection, packet: Packet<Join>) {
		connection.send(PositionView(Location3D(0, 100, 0).toPosition3D(), View(Angle(0), Angle(0))))
	}

	companion object {
		val defaultWorldName = "minecraft:overworld"
		val defaultDimensionCodec = NBTCompound().apply {
			set("minecraft:dimension_type", NBTCompound().apply {
				setString("type", "minecraft:dimension_type")
				set("value", NBTList<NBTCompound>(NBTTypes.TAG_Compound).apply {
					add(NBTCompound().apply {
						setString("name", "minecraft:overworld")
						setInt("id", 0)
						set("element", NBTCompound().apply {
							setByte("piglin_safe", 0)
							setByte("natural", 1)
							setFloat("ambient_light", 0f)
							setString("infiniburn", "minecraft:infiniburn_overworld")
							setByte("respawn_anchor_works", 0)
							setByte("has_skylight", 1)
							setByte("bed_works", 1)
							setString("effects", "minecraft:overworld")
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
			setString("name", "minecraft:overworld")
			setByte("piglin_safe", 0)
			setByte("natural", 1)
			setFloat("ambient_light", 0f)
			setString("infiniburn", "minecraft:infiniburn_overworld")
			setByte("respawn_anchor_works", 0)
			setByte("has_skylight", 1)
			setByte("bed_works", 1)
			setString("effects", "minecraft:overworld")
			setByte("has_raids", 1)
			setInt("logical_height", 256)
			setDouble("coordinate_scale", 1.0)
			setByte("ultrawarm", 0)
			setByte("has_ceiling", 0)
		}
	}

	/*companion object {
//		val defaultWorldName = net.nee.Server.name.toLowerCase() + ":world"
		val defaultWorldName = "minecraft:overworld"
		val defaultDimensionCodec = """
			{
			    "minecraft:dimension_type": {
			        "type": "minecraft:dimension_type",
			        "value": [
			            {
			                "name": "minecraft:overworld",
			                "id": 0,
			                "element": {
			                    "piglin_safe": 0b,
			                    "natural": 1b,
			                    "ambient_light": 0.0f,
			                    "infiniburn": "minecraft:infiniburn_overworld",
			                    "respawn_anchor_works": 0b,
			                    "has_skylight": 1b,
			                    "bed_works": 1b,
			                    "effects": "minecraft:overworld",
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
			                name: "minecraft:overworld",
			                id: 0,
			                element: {
			                    piglin_safe: 0b,
			                    natural: 1b,
			                    ambient_light: 0.0f,
			                    infiniburn: "minecraft:infiniburn_overworld",
			                    respawn_anchor_works: 0b,
			                    has_skylight: 1b,
			                    bed_works: 1b,
			                    effects: "minecraft:overworld",
			                    has_raids: 1b,
			                    logical_height: 256,
			                    coordinate_scale: 1.0d,
			                    ultrawarm: 0b,
			                    has_ceiling: 0b
			                }
			            },
			            {
			                name: "minecraft:overworld_caves",
			                id: 1,
			                element: {
			                    piglin_safe: 0b,
			                    natural: 1b,
			                    ambient_light: 0.0f,
			                    infiniburn: "minecraft:infiniburn_overworld",
			                    respawn_anchor_works: 0b,
			                    has_skylight: 1b,
			                    bed_works: 1b,
			                    effects: "minecraft:overworld",
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
			    "name": "minecraft:overworld",
			    "piglin_safe": 0b,
			    "natural": 1b,
			    "ambient_light": 0.0f,
			    "infiniburn": "minecraft:infiniburn_overworld",
			    "respawn_anchor_works": 0b,
			    "has_skylight": 1b,
			    "bed_works": 1b,
			    "effects": "minecraft:overworld",
			    "has_raids": 1b,
			    "logical_height": 256,
			    "coordinate_scale": 1.0d,
			    "ultrawarm": 0b,
			    "has_ceiling": 0b
			}
		""".trimIndent().replace(Regex("\\s"), "")
	}*/
}