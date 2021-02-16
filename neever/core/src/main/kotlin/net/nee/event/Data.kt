package net.nee.event

interface Data<out RN> {
	fun run(): RN

	/*val cancelable get() = false
	val cancelled get() = false

	abstract class Cancellable<RN> : Event<RN> {
		override val cancelable get() = true
		override var cancelled = false
	}*/

	interface Passive : Data<Unit> {
		override fun run() {}
	}
}

