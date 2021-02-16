package net.nee

import java.lang.System.Logger.Level

fun neever(
	host: String = "127.0.0.1",
	port: Int = 25566,
	offline: Boolean = false,
	keepAlivePeriod: Long = 2000L,
	logLevel: Level = Level.ALL,
	block: Server.() -> Unit = {}
)  = Server.run {
	// TODO set log level
	init(Configuration(host, port, offline, keepAlivePeriod))
	apply(block)
	start()
}