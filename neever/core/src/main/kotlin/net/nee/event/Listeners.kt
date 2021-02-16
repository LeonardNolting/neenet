package net.nee.event

import kotlin.reflect.KClass
import kotlin.reflect.KType

sealed class Listeners<D : Data<*>, RN, RO>(val kClass: KClass<D>) {
	protected abstract fun runBefore(d: KType, event: Event<D>)
	protected abstract fun runAfter(d: KType, event: Event<D>, result: RN)
	protected abstract fun runThis(event: Event<D>, result: RO): RN

	internal fun runHead(d: KType, event: Event<D>) {
		if (event.cancelled) return
		runBefore(d, event)
	}

	internal fun runTail(d: KType, event: Event<D>, result: RO) {
		if (event.cancelled) return
		val newResult = runThis(event, result)
		if (event.cancelled) return
		runAfter(d, event, newResult)
	}

	internal fun run(d: KType, event: Event<D>, result: RO) {
		runHead(d, event)
		runTail(d, event, result)
	}
}
