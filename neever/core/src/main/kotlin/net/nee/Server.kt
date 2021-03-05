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
import net.nee.packets.server.playing.EntityPosition
import net.nee.packets.server.playing.EntityTeleport
import net.nee.packets.server.playing.EntityVelocity
import net.nee.packets.server.playing.PositionView
import net.nee.packets.server.playing.spawn.Object
import net.nee.particles.ParticleType
import net.nee.particles.Particles
import net.nee.physics.ParticleCube
import net.nee.physics.PhysicsWorld
import net.nee.physics.Quaternion
import net.nee.units.VarInt
import net.nee.units.View
import net.nee.units.coordinates.position.Position3D
import net.nee.units.coordinates.vector.Vector3D
import org.reflections8.Reflections
import java.net.InetSocketAddress
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*
import kotlin.concurrent.schedule
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

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

	val connections = Collections.synchronizedList<Connection>(mutableListOf())
	var keepAlive: TimerTask? = null
	var physics: TimerTask? = null

	val events = Handler()

	fun init(configuration: Configuration) {
		if (init) return

		logger = KotlinLogging.logger(name)
		Server.configuration = configuration

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
//			event.data.connection.logger.info {
//				val isServer = event.data.packet.d.isSubtypeOf(typeOf<Server<*>>())
//				"${if (isServer) "OUT" else "IN "} ${
//					event.data.packet.d.toString()
//						.removePrefix("net.nee.packets.")
//						.removePrefix("server.")
//						.removePrefix("client.")
//				}: ${event.data.packet}".cutOverflow(100u)
//			}
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
		val cube =
			ParticleCube(
				5.0,
				Particles(
					ParticleType.Dust(1F, 1F, 0F, 0.5F),
					true,
					Vector3D.ZERO,
					0F,
					1
				),
//				Particles(
//					ParticleType.Normal(26),
//					true,
//					Vector3D(0.05, 0.05, 0.05),
//					0F,
//					5
//				),
				Position3D(0.0, 100.0, 0.0),
				Vector3D(0.0, 0.0, 0.0),
//				Matrix3D(
//					Vector3D(0.5000000, -0.5000000, 0.7071068),
//					Vector3D(0.8535534, 0.1464466, -0.5000000),
//					Vector3D(0.1464466, 0.8535534, 0.5000000),
//				),
//				Quaternion(0.7325378, Vector3D(0.4619398, 0.1913417, 0.4619398)),
				Quaternion(0.0, Vector3D(0.0, 1.0, 0.0)),
//				Quaternion(0.7071068, Vector3D(0.0, 0.0, 0.7071068)),
//				Quaternion(0.7071068, Vector3D(0.0, 0.0, 0.7071068)).asMatrix.inverse.transform(Vector3D(0.0, 0.1, 0.0)),
				Vector3D(0.0, 0.0, 0.0),
				10.0
			)
		PhysicsWorld.objects.add(cube)
//		timer.schedule(5500L, 1000L) {
//			runBlocking(this@launch.coroutineContext) {
//				cube.applyForce(/*cube.rotation.transform(*/Vector3D(0.0, -0.01, 0.0), Vector3D(0.0, 0.0, 2.5))
//				println("BACK: ${cube.angularVelocity}")
//			}
//		}
		var lastTime = getTimeMillis()
		var pos = Position3D(0.0, 100.0, 0.0)
		val c = ParticleCube(
			1.0,
			Particles(ParticleType.Dust(1.0f, 0.8f, 0.2f, 0.4f), true, Vector3D.ZERO, 0f, 1),
			Position3D(0.0, 100.0, 10.0),
			Vector3D.ZERO,
			Quaternion(0.0, Vector3D(0.0, 1.0, 0.0)),
			Vector3D.ZERO,
			1.0
		)
		connections.forEach {
			it.send(EntityTeleport(0, pos, View.ZERO, false))
//			it.send(Object(1000, UUID.randomUUID(), VarInt(1), Position3D(0.0, 100.0, 10.0), View.ZERO, 0, Vector3D(0.0, 0.0, 0.0)))
		}
		physics = timer.schedule(delay = 0L, period = 50L) {
			runBlocking(this@launch.coroutineContext) {
				c.tick(0.0)
				connections.forEach {
					val dy = 0.0625
					it.send(EntityPosition(0, 0, (dy * 4096).toShort(), 0, false))
					pos += Vector3D(0.0, dy, 0.0)
					it.send(EntityTeleport(0, pos, View.ZERO, false))
//					it.send(EntityTeleport(0, pos, ))
//					it.send(PositionView(pos, View.ZERO))
//					it.send(EntityVelocity(0, 0, (dy / 80000).toShort(), 0))
				}
			}
		}
//		physics = timer.schedule(delay = 0L, period = 10L) {
//			val now = getTimeMillis()
//			val dt = (now - lastTime) / 1000.0
//			lastTime = now
//			runBlocking(this@launch.coroutineContext) {
//				val position = cube.rotation.asMatrix.transform(Vector3D(2.5, 2.5, 2.5))
//				cube.applyForce(/*Vector3D(0.0, 0.004, 0.0)*/(position to (Position3D(0.0, 110.0, 0.0) - cube.pos)).let { it.withLength((it.length - 5.0) / 1000) }, position)
//				val position2 = cube.rotation.asMatrix.transform(Vector3D(-2.5, -2.5, -2.5))
//				cube.applyForce(/*Vector3D(0.0, 0.004, 0.0)*/(position2 to (Position3D(0.0, 118.0, 0.0) - cube.pos)).let { it.withLength((it.length - 5.0) / 1000) }, position2)
////				val position1 = cube.rotation.asMatrix.transform(Vector3D(-2.5, 2.5, 2.5))
////				cube.applyForce(/*Vector3D(0.0, 0.004, 0.0)*/(position1 to Vector3D(0.0, 10.0, 0.0)).withLength(0.001), position1)
////				cube.applyForce(/*Vector3D(0.0, 0.004, 0.0)*/(position2 to Vector3D(0.0, -10.0, 0.0)).withLength(0.001), position2)
////				val position2 = cube.rotation.asMatrix.transform(Vector3D(-2.5, -2.5, -2.5))
////				cube.applyForce(Vector3D(0.0, -0.004, 0.0), Vector3D.ZERO)
//
//				cube.angVel = cube.angVel.withLength(cube.angVel.length - 0.01)
//				cube.vel = cube.vel.withLength(cube.vel.length - 0.001)
//
////				println(cube.pos)
////				println(cube.rotation)
//
////				println("Angular velocity: ${cube.angularVelocity}")
////				println("Rotation: ${cube.rotation}")
////				println("Position: ${cube.pos}")
//
//				PhysicsWorld.tick(dt * 1)
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
