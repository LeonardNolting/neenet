package net.nee

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogger
import mu.KotlinLogging
import net.nee.connection.Connection
import net.nee.connection.types.Play
import net.nee.event.Event
import net.nee.event.Handler
import net.nee.events.packet.Packet
import net.nee.packet.data.Client
import net.nee.packet.data.Server
import net.nee.physics.Matrix3D
import net.nee.physics.PhysicsEntity
import net.nee.physics.PhysicsWorld
import net.nee.units.Angle
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import org.reflections8.Reflections
import java.net.InetSocketAddress
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.concurrent.schedule
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@OptIn(KtorExperimentalAPI::class)
object Server {
	const val name = "Neever"
	internal lateinit var logger: KLogger
	val timer = Timer()
	val scope = GlobalScope

	lateinit var configuration: Configuration

	object Version {
		const val name = "1.16.5"
		const val protocol = 754
	}

	lateinit var client: HttpClient

	var running = false
	var init = false

	lateinit var keypair: KeyPair

	val connections = mutableListOf<Connection>()
	var keepAlive: TimerTask? = null
	var physics: TimerTask? = null

	val events = Handler()

	fun init(configuration: Configuration) {
		if (init) return

		logger = KotlinLogging.logger(name)
		net.nee.Server.configuration = configuration

		Handler.register("net.nee.event")
		Handler.register("net.nee.events")

		packets("net.nee.packets")

		keypair = KeyPairGenerator.getInstance("RSA").run {
			initialize(1024)
			genKeyPair()
		}

		client = HttpClient(CIO) {
			install(JsonFeature) {
				serializer = GsonSerializer()
			}
		}

		events.after { event: Event<Packet<*>>, _ ->
			event.data.connection.logger.info {
				val isServer = event.data.packet.d.isSubtypeOf(typeOf<Server<*>>())
				"${if (isServer) "OUT" else "IN "} ${
					event.data.packet.d.toString()
						.removePrefix("net.nee.packets.")
						.removePrefix("server.")
						.removePrefix("client.")
				}: ${event.data.packet}".cutOverflow(100u)
			}
		}

		init = true
		logger.info { "Initialized" }
	}

	/**
	 * Open socket, handle packets, send keep alive packets
	 */
	fun start() = scope.launch {
		check(init)

		running = true
		val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
			.bind(InetSocketAddress(configuration.host, configuration.port))

		logger.info { "Listening on /${configuration.host}:${configuration.port}" }

		keepAlive = timer.schedule(delay = 0L, period = configuration.keepAlivePeriod) {
			runBlocking(this@launch.coroutineContext) {
				connections.forEach {
					if (it.type is Play && it.type.state == Play.States.PLAYING) it.keepAlive()
				}
			}
		}

//		val random = ThreadLocalRandom.current()
//		repeat(10) { eid ->
//			val pos = Position3D(
//				0.0 + random.nextDouble(-10.0, 10.0),
////				0.0,
//				100.0 + random.nextDouble(-10.0, 10.0),
//				0.0 + random.nextDouble(-10.0, 10.0)
////				0.0
//			)
//			val vel = Vector3D(
//				random.nextDouble(-0.1, 0.1),
//				random.nextDouble(-0.1, 0.1),
//				random.nextDouble(-0.1, 0.1),
////				0.0, 0.0, 0.0
//			)
//			PhysicsWorld.objects.add(PhysicsEntity(eid + 1, pos, vel, Matrix3D.IDENTITY, Vector3D.ZERO, 1.0))
//		}
//		PhysicsWorld.objects.add(PhysicsEntity(1000, Position3D(0.0, 100.0, 0.0), Vector3D.ZERO,Matrix3D.IDENTITY, Vector3D.ZERO,  1000.0))
//		var lastTime = getTimeMillis()
//		physics = timer.schedule(delay = 0L, period = 50L) {
//			val now = getTimeMillis()
//			val dt = (now - lastTime) / 1000.0
//			lastTime = now
//			runBlocking(this@launch.coroutineContext) {
//				PhysicsWorld.tick(dt * 0.001)
//			}
//		}

		while (running) {
			val socket = server.accept()
			launch {
				Connection(socket).let {
					connections += it
					it.listen()
				}
			}
		}
	}

	/**
	 * Stops server from accepting new sockets,
	 * stops sending keep alive packets
	 */
	fun close() {
		running = false

		keepAlive?.cancel()
		keepAlive = null
		timer.purge()
	}

	/**
	 * Register all packets in a package
	 */
	private fun packets(path: String) = Reflections(path)
		.getSubTypesOf(Client::class.java)
		.forEach { packet(it.kotlin) }

	/**
	 * Register a packet
	 */
	private fun packet(packet: KClass<out Client<*>>) {
		val handler = if (packet.companionObjectInstance is Client.Handler<*>)
			packet.companionObjectInstance as Client.Handler<*>
		else Client.Handler(packet)
		packets[packet] = handler
	}

	internal val packets = mutableMapOf<KClass<out Client<*>>, Client.Handler<*>>()
}