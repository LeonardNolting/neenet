package net.nee.api

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

abstract class Api<R : Api.Result> {
	suspend fun call() = get(url)
	abstract val url: String
	abstract suspend fun get(url: String): R

	protected fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)

	interface Result
}