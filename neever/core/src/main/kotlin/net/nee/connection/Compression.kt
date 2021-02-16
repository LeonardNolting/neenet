package net.nee.connection

import kotlin.properties.Delegates

class Compression {
	var enabled = false
	var threshold by Delegates.notNull<Int>()
}