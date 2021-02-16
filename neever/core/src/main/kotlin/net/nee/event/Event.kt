package net.nee.event

class Event<D : Data<*>>(val data: D) {
	var cancelled = false

	fun cancel() {
		cancelled = true
	}
}