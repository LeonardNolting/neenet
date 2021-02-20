package net.nee.connection

import net.nee.Server
import net.nee.connection.types.Type
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import mu.KotlinLogging
import net.nee.connection.types.Uncertain
import net.nee.packet.Packet
import net.nee.packet.PacketId
import net.nee.packet.format
import net.nee.packets.client.handshaking.Handshake
import net.nee.packets.server.playing.KeepAlive
import java.security.MessageDigest
import java.security.PublicKey
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.SecretKey
import kotlin.random.Random

@Suppress("PropertyName")
class Connection(private val socket: Socket) : Socket by socket {
	val logger = KotlinLogging.logger(Server.name + socket.remoteAddress.toString())

	var type: Type<out States> = Uncertain

	val input = openReadChannel()
	val output = openWriteChannel(autoFlush = false)

	var enableEncryption = false

	internal val encryption = Encryption()
	internal val compression = Compression()

	lateinit var username: String

	init {
		logger.info { "Socket accepted" }
	}

	suspend fun listen() = try {
		while (true) {
			val length = input.readVarInt()
			val packet = input.readPacket(length.toInt(), 0)
			val id: PacketId = packet.readVarInt().toInt()

			handle(id, packet)
		}
	} catch (e: Throwable) {
		if (e is ClosedReceiveChannelException) logger.info { "Closed by client: ${e.message}" }
		else {
			e.printStackTrace()
			logger.error { "Closed." }
		}

		close()
	}

	override fun close() {
		Server.connections -= this
		if (!socket.isClosed)
			socket.close()
	}

	private suspend fun handle(id: PacketId, packet: ByteReadPacket) {
		val handler = type.state.allowedClientPackets[id]
		if (handler == null) logger.warn { "Unexpected packet with id ${id.format()} arrived." }
		else handler.handle(connection = this, encryption.decrypt(packet))
	}

	suspend fun <D : net.nee.packet.data.Server<D>> send(
		packet: Packet<D>,
	) = packet.data.send(this, packet)

	suspend inline fun <reified D : net.nee.packet.data.Server<D>> send(
		data: D
	) = send(Packet(data))

	// TODO catch exceptions
	fun digest(data: String, publicKey: PublicKey, secretKey: SecretKey): ByteArray =
		MessageDigest.getInstance("SHA-1").apply {
			update(data.toByteArray(Charsets.ISO_8859_1))
			update(secretKey.encoded)
			update(publicKey.encoded)
		}.digest()

	lateinit var lastKeepAliveRequest: Instant
	var waitingForKeepAliveResponse = false
	suspend fun keepAlive() {
		if (!waitingForKeepAliveResponse) {
			lastKeepAliveRequest = Instant.now()
			send(KeepAlive(Random.nextLong()))
			waitingForKeepAliveResponse = true
		}
	}

	abstract class States {
		val HANDSHAKING = State(
			0x00 to Handshake::class
		)

		val CLOSING = State(

		)
	}
}

class CipherBase(private val cipher: Cipher) {
	/*private var inTempArray = ByteArray(0)
	private var outTempArray = ByteArray(0)
	private fun bufToByte(buffer: ByteBuf): ByteArray {
		val remainingBytes = buffer.readableBytes()

		// Need to resize temp array
		if (inTempArray.size < remainingBytes)
			inTempArray = ByteArray(remainingBytes)
		buffer.readBytes(inTempArray, 0, remainingBytes)
		return inTempArray
	}*/

	fun decrypt(packet: ByteReadPacket): ByteReadPacket {
		val input = packet.readBytes()
		return ByteReadPacket(ByteArray(cipher.getOutputSize(input.size)).apply {
			cipher.update(input, 0, input.size, this)
		})
	}

	/*fun decrypt(channelHandlerContext: ChannelHandlerContext, byteBufIn: ByteBuf): ByteBuf {
		val remainingBytes = byteBufIn.readableBytes()
		return channelHandlerContext.alloc().heapBuffer(cipher.getOutputSize(remainingBytes)).apply {
			writerIndex(
				cipher.update(
					bufToByte(byteBufIn),
					0,
					remainingBytes,
					array(),
					arrayOffset()
				)
			)
		}
	}*/

	fun encrypt(packet: ByteReadPacket): ByteReadPacket {
		val result = ByteArray(cipher.getOutputSize(packet.remaining.toInt()))
		val input = packet.readBytes()
		packet.release()
		cipher.update(input, 0, input.size, result)
		return ByteReadPacket(result)
	}

	/*fun encrypt(byteBufIn: ByteBuf, byteBufOut: ByteBuf) {
		val remainingBytes = byteBufIn.readableBytes()
		val bytes = bufToByte(byteBufIn)
		val newSize = cipher.getOutputSize(remainingBytes)

		// Need to resize temp array
		if (outTempArray.size < newSize) {
			outTempArray = ByteArray(newSize)
		}
		byteBufOut.writeBytes(outTempArray, 0, cipher.update(bytes, 0, remainingBytes, outTempArray))
	}*/
}