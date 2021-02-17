package net.nee.packet.data

import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.outputStream
import net.nee.connection.Connection
import net.nee.connection.writeString
import net.nee.connection.writeVarInt
import net.nee.entity.EntityId
import net.nee.entity.GameMode
import net.nee.events.packet.Send
import net.nee.packet.Packet
import net.nee.packets.server.playing.spawn.Painting
import net.nee.units.Direction
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.ViewDistance
import net.nee.units.coordinates.location.chunk.ChunkLocation2D
import net.nee.units.coordinates.position.Position3D
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTWriter
import java.util.*
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

abstract class Server<D : Server<D>>(
	val id: Int,
) : Data<D, Send<D>>() {
	final override fun packetEvent(connection: Connection, packet: Packet<D>): Send<D> =
		Send(connection, packet)

	protected open suspend fun afterSend(connection: Connection, packet: Packet<D>) {}

	private val kClass = this::class
	private val constructor = kClass.constructors.singleOrNull { it.hasAnnotation<Method>() }
		?: kClass.primaryConstructor
		?: error("No constructor to prepare packet $kClass. To use a secondary constructor, please annotate it with `@Method`.")

	companion object {
		private class Writer<T>(
			val t: KType,
			val write: BytePacketBuilder.(value: T, type: KType, unit: KType?) -> kotlin.Unit
		)

		private inline fun <reified T> Writer(noinline write: BytePacketBuilder.(value: T) -> kotlin.Unit) =
			Writer<T>(typeOf<T>()) { value, type, unit ->
				check(unit == null) { "Unhandled unit on parameter of type $type: $unit" }
				write(value)
			}

		@Suppress("FunctionName")
		private inline fun <reified T> WriterFull(noinline write: BytePacketBuilder.(value: T, type: KType, unit: KType?) -> kotlin.Unit) =
			Writer(typeOf<T>(), write)

		private val writers = listOf<Writer<*>>(
			Writer<Byte> { writeByte(it) },
			Writer<Int> { writeInt(it) },
			Writer<VarInt> { writeVarInt(it) },
			Writer<String> { writeString(it) },
			Writer<Short> { writeShort(it) },
			Writer<UShort> { writeUShort(it) },
			Writer<Long> { writeLong(it) },
			Writer<ByteArray> {
				writeVarInt(it.size)
				it.forEach { byte -> writeByte(byte) }
			},
			Writer<UUID> {
				writeLong(it.mostSignificantBits)
				writeLong(it.leastSignificantBits)
			},
			Writer<Boolean> {
				writeByte(if (it) 0x01 else 0x00)
			},
			WriterFull<List<*>> { list, type, _ ->
				writeVarInt(list.size)
				val argument = type.arguments.single().type!!
				list.forEach { write(it, type = argument) }
			},
			Writer<GameMode> {
				writeByte(it.id)
			},
			Writer<Position3D> {
				writeDouble(it.x)
				writeDouble(it.y)
				writeDouble(it.z)
			},
			/*Writer<Location2D> {

			},*/
			WriterFull<View> { it, type, unit ->
				if (unit == typeOf<Float>()) {
					writeFloat(it.yaw.toFloat())
					writeFloat(it.pitch.toFloat())
				} else {
					writeByte(it.yaw.toByte())
					writeByte(it.pitch.toByte())
				}
			},
			Writer<NBTCompound> {
				NBTWriter(outputStream(), compressed = false).writeNamed("", it)
			},
			WriterFull<ViewDistance> { it, _, unit ->
				if (unit == typeOf<VarInt>()) writeVarInt(it.toInt())
				else writeByte(it.toByte())
			},
			WriterFull<ChunkLocation2D> { it, _, unit ->
				if (unit == typeOf<VarInt>()) {
					writeVarInt(it.x)
					writeVarInt(it.z)
				} else {
					writeInt(it.x)
					writeInt(it.z)
				}
			},
			WriterFull<BitField> { it, type, unit ->
				val kClass = it::class
				val value = kClass.primaryConstructor!!.valueParameters.map { parameter ->
					// Order
					kClass.declaredMemberProperties.find { it.name == parameter.name }?.to(parameter)
						?: error("Parameter ${parameter.name} of server packet $kClass is not a field. Server packet constructor parameters must all be fields.") // TODO error message
				}.withIndex().fold(0) { acc, (index, pair) ->
					val (field) = pair
					@Suppress("UNCHECKED_CAST")
					val value = (field as KProperty1<BitField, *>).get(it)
					check(value is Boolean)
					if (value) acc or (1 shl index)
					else acc
				}

				when (unit) {
					typeOf<VarInt>() -> write(VarInt(value))
					typeOf<Int>()    -> write(value)
					else             -> write(value.toByte())
				}
			},
			Writer<Float> {
				writeFloat(it)
			},
			WriterFull<EntityId> { it, _, unit ->
				when (unit) {
					typeOf<Int>() -> write(it)
					else          -> write(VarInt(it))
				}
			},
			Writer<Painting.Motive> { write(VarInt(it.id)) },
			Writer<Direction> { writeByte(it.id.toByte()) }
		)

		@Suppress("UNCHECKED_CAST") // type = typeOf<T>()
		private fun <T> find(type: KType): Writer<T> =
			(writers.find { writer -> type.isSubtypeOf(writer.t) }
				?: error("Missing writer for type $type.")) as Writer<T>

		private inline fun <reified T> find() = find<T>(typeOf<T>())

		fun <T> BytePacketBuilder.write(value: T, type: KType, unit: KType? = null) =
			find<T>(type).write(this, value, type, unit)

		@JvmStatic
		inline fun <reified T : Any?> BytePacketBuilder.write(value: T, unit: KType? = null) =
			write(value, typeOf<T>(), unit)
	}

	private fun prepareReflectively(packet: BytePacketBuilder) {
		constructor.valueParameters.map { parameter ->
			// Order
			kClass.declaredMemberProperties.find { it.name == parameter.name }?.to(parameter)
				?: error("Parameter ${parameter.name} of server packet $kClass is not a field. Server packet constructor parameters must all be fields.")
		}.forEach { (field, parameter) ->
			@Suppress("UNCHECKED_CAST")
			packet.write(
				(field as KProperty1<Server<D>, *>).get(this@Server),
				field.returnType,
				parameter.findAnnotation<Unit>()?.toKType()
			)
		}
	}

	protected open suspend fun BytePacketBuilder.prepare() =
		prepareReflectively(this)

	private fun Connection.encrypt(packet: ByteReadPacket): ByteReadPacket {
		return if (encryption.enabled) encryption.encrypt(packet)
		else packet
	}

	internal suspend fun send(
		connection: Connection,
		packet: Packet<D>,
	) {
		val content = buildPacket { prepare() }
		val idVarInt = VarInt(id).toByteArray()
		val length = content.remaining.toInt() + idVarInt.size

		connection.run {
			run(this, packet)

			output.writePacket(encrypt(buildPacket {
				writeVarInt(length)
				writeFully(idVarInt)
				writePacket(content)
			}))

			output.flush()

			afterSend(this, packet)
		}
	}
}