package net.nee

data class Configuration(
	val host: String,
	val port: Int,
	val offline: Boolean,
	val keepAlivePeriod: Long,
)