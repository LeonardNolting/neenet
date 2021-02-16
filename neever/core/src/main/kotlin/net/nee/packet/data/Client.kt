package net.nee.packet.data

import net.nee.connection.Connection
import net.nee.connection.readString
import net.nee.connection.readVarInt
import net.nee.entity.GameMode
import net.nee.events.packet.Receive
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readDouble
import io.ktor.utils.io.core.readInt
import io.ktor.utils.io.core.readLong
import io.ktor.utils.io.core.readShort
import io.ktor.utils.io.core.readUByte
import io.ktor.utils.io.core.readUShort
import net.nee.packet.Packet
import net.nee.packets.client.playing.Settings
import net.nee.units.VarInt
import net.nee.units.ViewDistance
import net.nee.units.coordinates.position.Position3D
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.typeOf

abstract class Client<D : Client<D>> : Data<D, Receive<D>>() {
	open class Handler<D : Client<D>>(private val kClass: KClass<D>) {
		private val d = kClass.createType()
		private val e = Receive::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, d)))
		private val constructor = kClass.constructors.singleOrNull { it.hasAnnotation<Method>() }
			?: kClass.primaryConstructor
			?: error("No constructor to parse packet $kClass. To use a secondary constructor, please annotate it with `@Method`.")

		protected suspend fun Connection.parseReflectively(packet: ByteReadPacket): D {
			val arguments = constructor.valueParameters.associateWith { parameter ->
				packet.read(parameter)
			}.toMutableMap()

			constructor.extensionReceiverParameter?.let {
				require(it.type == typeOf<Connection>())
				arguments[it] = this
			}

			return constructor.callBy(arguments)
		}

		protected open suspend fun Connection.parse(packet: ByteReadPacket) =
			parseReflectively(packet)

		private fun pack(data: D) = Packet(d, e, data)

		suspend fun handle(connection: Connection, packet: ByteReadPacket) =
			pack(connection.parse(packet)).apply {
				data.run(connection, packet = this)
			}

		companion object {
			private class Reader<T>(
				val t: KType,
				val read: suspend ByteReadPacket.(type: KType, unit: KType?) -> T
			)

			private inline fun <reified T> Reader(noinline read: suspend ByteReadPacket.() -> T) =
				Reader(typeOf<T>()) { type, unit ->
					check(unit == null) { "Unhandled unit on parameter of type $type: $unit" }
					read()
				}

			@Suppress("FunctionName")
			private inline fun <reified T> ReaderFull(noinline read: suspend ByteReadPacket.(type: KType, unit: KType?) -> T) =
				Reader(typeOf<T>(), read)

			@Suppress("RemoveExplicitTypeArguments")
			private val readers = listOf(
				Reader<Byte> { readByte() },
				Reader<UByte> { readUByte() },
				Reader<Boolean> { readByte().toInt() == 1 },
				Reader<Int> { readInt() },
				Reader<VarInt> { readVarInt() },
				Reader<String> { readString() },
				Reader<Short> { readShort() },
				Reader<UShort> { readUShort() },
				Reader<Long> { readLong() },
				Reader<ByteArray> { (0 until readVarInt().toInt()).map { readByte() }.toByteArray() },
				Reader<List<Any?>> {
					(0 until readVarInt().toInt()).map { read() }
				},
				Reader<GameMode> {
					val id = readByte()
					GameMode.values().find { it.id == id }!!
				},
				Reader<Position3D> { Position3D(readDouble(), readDouble(), readDouble()) },
				ReaderFull<ViewDistance> { _, unit ->
					ViewDistance(
						if (unit == typeOf<VarInt>()) readVarInt().toInt()
						else readByte().toInt()
					)
				},
				Reader<Settings.ChatMode> {
					val int = readVarInt().toInt()
					Settings.ChatMode.values().find { it.int == int }!!
				},
				Reader<Settings.MainHand> {
					val int = readVarInt().toInt()
					Settings.MainHand.values().find { it.int == int }!!
				}
			)

			@Suppress("UNCHECKED_CAST") // type = typeOf<T>()
			private fun <T> find(type: KType): Reader<T> =
				(readers.find { reader -> reader.t == type }
					?: error("Missing parse method for type $type.")) as Reader<T>

			protected suspend fun <T> ByteReadPacket.read(type: KType, unit: KType?) =
				find<T>(type).read(this, type, unit)

			@JvmStatic
			protected suspend inline fun <reified T : Any?> ByteReadPacket.read(unit: KType? = null) =
				read<T>(typeOf<T>(), unit)

			protected suspend fun ByteReadPacket.read(parameter: KParameter) =
				read<Any?>(parameter.type, parameter.findAnnotation<Unit>()?.toKType())
		}
	}

	final override fun packetEvent(connection: Connection, packet: Packet<D>): Receive<D> =
		Receive(connection, packet)
}