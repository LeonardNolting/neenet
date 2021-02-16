package net.nee.event

import kotlin.reflect.KClass
import kotlin.reflect.KType

sealed class Listener<D : Data<*>, RN, RO>(
	kClass: KClass<D>,
	private val priority: Int
) : Listeners<D, RN, RO>(kClass), Comparable<Listener<*, *, *>> {
	private val listenersBefore = sortedSetOf<Before<D, *>>(reversedComparator)
	private val listenersAfter = sortedSetOf<After<D, *, RN>>(reversedComparator)

	override fun compareTo(other: Listener<*, *, *>): Int = priority.compareTo(other.priority)

	override fun runBefore(d: KType, event: Event<D>) =
		listenersBefore.forEach { it.run(d, event, Unit) }

	override fun runAfter(d: KType, event: Event<D>, result: RN) =
		listenersAfter.forEach { it.run(d, event, result) }

	fun <RNN> before(priority: Int, call: (Event<D>) -> RNN) =
		Before(kClass, priority, call).also { listenersBefore.add(it) }

	fun <RNN> after(priority: Int, call: (Event<D>, RN) -> RNN) =
		After(kClass, priority, call).also { listenersAfter.add(it) }

	fun <RNN> before(priority: Priority = Priority.NORMAL, call: (Event<D>) -> RNN) =
		before(priority.ordinal, call)

	fun <RNN> after(priority: Priority = Priority.NORMAL, call: (Event<D>, RN) -> RNN) =
		after(priority.ordinal, call)

	class Before<D : Data<*>, RN>(
		kClass: KClass<D>,
		priority: Int,
		val call: (Event<D>) -> RN
	) : Listener<D, RN, Unit>(kClass, priority) { // TODO Unit -> Nothing
		override fun runThis(event: Event<D>, result: Unit): RN = call(event)

		companion object {
			inline operator fun <reified D : Data<*>, RN> invoke(
				priority: Int,
				noinline call: (Event<D>) -> RN
			) = Before(D::class, priority, call)
		}

		fun interface Call<D : Data<*>, RN> {
			fun call(event: Event<D>): RN
		}
	}

	class After<D : Data<*>, RN, RO>(
		kClass: KClass<D>,
		priority: Int,
		val call: (Event<D>, RO) -> RN
	) : Listener<D, RN, RO>(kClass, priority) {
		override fun runThis(event: Event<D>, result: RO): RN = call(event, result)

		companion object {
			inline operator fun <reified D : Data<*>, RN, RO> invoke(
				priority: Int,
				noinline call: (Event<D>, RO) -> RN
			) = After(D::class, priority, call)
		}

		fun interface Call<D : Data<RO>, RN, RO> {
			fun call(event: Event<D>, result: RO): RN
		}
	}

	companion object {
		val reversedComparator = Comparator<Listener<*, *, *>> { a, b -> -a.compareTo(b) }
	}
}