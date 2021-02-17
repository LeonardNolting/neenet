package net.nee.event

import net.nee.Server.logger
import org.reflections8.Reflections
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
private val <D : Data<*>> KClass<D>.hierarchyReversed: List<KClass<out D>>
	get() = (mutableListOf(this) + (superclasses
		.filter { it.isSubclassOf(Data::class) } as List<KClass<out D>>).asReversed()
		.flatMap { it.hierarchyReversed })

internal val <D : Data<*>>KClass<out D>.hierarchy get() = hierarchyReversed.asReversed().distinct()

/**
 * Pseudo code of the structure:
 * ```
 * val handler = Handler(Event::class, handlers = [
 * 	Handler(Chat::class, handlers = [
 * 		Handler(Chat.In::class),
 * 		Handler(Chat.Out::class),
 * 	]),
 * 	...
 * ])
 * ```
 *
 * Use [print] to visualize the hierarchy.
 */
class Handler<D : Data<RN>, RN> private constructor(
	kClass: KClass<D>,
	private val parent: Handler<in D, *>? = null
) : Listeners<D, RN, Unit>(kClass) {
	val listenersBefore = mutableMapOf<List<KTypeProjection>, SortedSet<Listener.Before<D, *>>>()
	val listenersAfter = mutableMapOf<List<KTypeProjection>, SortedSet<Listener.After<D, *, RN>>>()
	val handlers = mutableListOf<Handler<out D, *>>()

	private fun createHierarchy(hierarchy: List<KClass<out Data<*>>>) {
		if (hierarchy.size <= 1) return

		val nextInHierarchy = hierarchy[1]

		@Suppress("UNCHECKED_CAST")
		val handler = handlers.getOrPut(nextInHierarchy as KClass<out D>, this as Handler<out D, *>?)
		handler.createHierarchy(hierarchy.drop(1))
	}

	internal fun createHierarchy(kClass: KClass<out D>) {
		val hierarchy = kClass.hierarchy
		require(hierarchy.first() == this.kClass) { "Cannot create hierarchy from a different root." }
		createHierarchy(hierarchy)
	}

	private operator fun List<Handler<out D, *>>.get(kClass: KClass<out D>) = find { it.kClass == kClass }

	@Suppress("UNCHECKED_CAST")
	private fun List<Handler<out D, *>>.getOrPut(kClass: KClass<out D>, parent: Handler<out D, *>?) = get(kClass)
		?: Handler(kClass, parent as Handler<D, *>?).also { handlers.add(it as Handler<out D, *>) }

	@Suppress("UNCHECKED_CAST")
	fun <D2 : Data<*>> find(kClass: KClass<D2>, hierarchy: List<KClass<out D2>> = kClass.hierarchy): Handler<D2, *> = (
		if (kClass == this.kClass) this
		else (handlers.find { it.kClass == hierarchy[1] } as Handler<D2, *>?)?.find(kClass, hierarchy.drop(1))
			?: error("Event class $kClass has not been registered. Please use `events` to register event classes in a package. (Note: You can only register listeners after the server was initialized.")
		) as Handler<D2, *>

	@Suppress("UNCHECKED_CAST")
	private fun <D : Data<*>, L : Listener<out D, *, *>, S : SortedSet<out Listener<out D, *, *>>> MutableMap<List<KTypeProjection>, S>.add(
		arguments: List<KTypeProjection>,
		listener: L
	) = (getOrPut(arguments) { sortedSetOf(Listener.reversedComparator) as S } as SortedSet<L>).add(listener)

	@Suppress("UNCHECKED_CAST")
	fun <D2 : Data<RO>, RNN, RO> before(
		d2: KType,
		kClass: KClass<D2>,
		listener: Listener.Before<D2, RNN>,
	) = run {
		find(kClass).listenersBefore.add(d2.arguments, listener as Listener<out D, *, *>)
		listener
	}

	@Suppress("UNCHECKED_CAST")
	fun <D2 : Data<RO>, RNN, RO> after(
		d2: KType,
		kClass: KClass<D2>,
		listener: Listener.After<D2, RNN, *>,
	) = run {
		find(kClass as KClass<out D>).listenersAfter.add(d2.arguments, listener as Listener<out D, *, *>)
		listener
	}

	inline fun <reified D2 : D, RNN> before(
		priority: Int,
		call: Listener.Before.Call<D2, RNN>
	) = before(typeOf<D2>(), D2::class, Listener.Before(priority, call::call))

	inline fun <reified D2 : Data<RO>, RNN, RO> after(
		priority: Int,
		call: Listener.After.Call<D2, RNN, RO>
	) = after(typeOf<D2>(), D2::class, Listener.After(priority, call::call))

	inline fun <reified D2 : D, RNN> before(
		priority: Priority = Priority.NORMAL,
		call: Listener.Before.Call<D2, RNN>
	) = before(priority.ordinal, call)

	inline fun <reified D2 : Data<RO>, RNN, RO> after(
		priority: Priority = Priority.NORMAL,
		call: Listener.After.Call<D2, RNN, RO>
	) = after(priority.ordinal, call)

	private fun <L : Listener<*, *, *>> MutableMap<List<KTypeProjection>, SortedSet<L>>.listeners(d: KType): List<L> =
		filter { (kTypeProjections, _) ->
			var called = true

			kTypeProjections.withIndex().forEach { (index, kTypeProjection) ->
				val argument = d.arguments.getOrNull(index) ?: return@filter false
				val argumentType = argument.type ?: run {
					logger.error { "Cannot emit events with star projections." }
					return emptyList()
				}

				if (!when {
						kTypeProjection.type == null                         -> true
						kTypeProjection.variance == KVariance.OUT &&
							argumentType.isSubtypeOf(kTypeProjection.type!!) -> true
						kTypeProjection.type!! == argumentType               -> true
						else                                                 -> false
					}
				) called = false
			}

			called
		}.flatMap { (_, listeners) -> listeners }

	override fun runBefore(d: KType, event: Event<D>) {
		(parent as Handler<D, *>?)?.runHead(d, event)
		listenersBefore.listeners(d).forEach { it.run(d, event, Unit) }
	}

	override fun runAfter(d: KType, event: Event<D>, result: RN) {
		listenersAfter.listeners(d).forEach { it.run(d, event, result) }
		(parent as Handler<D, *>?)?.runTail(d, event, Unit)
	}

	override fun runThis(event: Event<D>, result: Unit): RN = event.data.run()

	internal fun run(e: KType, event: Event<D>) = run(e, event, Unit)

	private fun <D2 : D> emit(
		f: KType,
		kClass: KClass<out D2>,
		event: Event<D2>,
		hierarchy: List<KClass<out D2>> = kClass.hierarchy
	): Event<D2> {
		@Suppress("UNCHECKED_CAST")
		if (this@Handler.kClass == kClass)
			run(f, event as Event<D>)
		else (handlers.find { it.kClass == hierarchy[1] } as Handler<D2, *>?)
			?.emit(f, kClass, event, hierarchy.drop(1))
			?: error("Event class $kClass has not been registered. Please use `events` to register event classes in a package.")
		return event
	}

	@Suppress("UNCHECKED_CAST")
	fun <D2 : D> emit(d: KType, event: Event<D2>) =
		emit(d, d.classifier as KClass<out D2>, event)

	inline fun <reified D2 : D> emit(event: Event<D2>): Event<D2> =
		emit(typeOf<D2>(), event)

	fun <D2 : D> emit(d: KType, data: D2) =
		emit(d, Event(data))

	inline fun<reified D2 : D> emit(data: D2) =
		emit(Event(data))

	inline fun <reified D2 : D> emit(vararg events: Event<D2>) =
		events.map { event -> emit(event) }

	inline fun <reified D2 : D> emit(vararg data: D2): List<Event<D2>> =
		emit(*data.map { Event(it) }.toTypedArray())

	companion object {
		private val kClasses = mutableListOf<KClass<out Data<*>>>()
		private val handlers = mutableListOf<Handler<Data<*>, *>>()

		@JvmName("invokeDefault")
		operator fun invoke() = Handler(Data::class).apply {
			kClasses.forEach { createHierarchy(it) }
			Companion.handlers += this
		}

		fun register(kClass: KClass<out Data<*>>) {
			kClasses += kClass
			handlers.forEach { it.createHierarchy(kClass) }
		}

		inline fun <reified D : Data<*>> register() =
			register(D::class)

		fun register(path: String) = Reflections(path)
			.getSubTypesOf(Data::class.java)
			.forEach { register(it.kotlin) }
	}

	fun print(indentation: Int = 0) {
		println("\t".repeat(indentation) + "Handler for ${kClass.qualifiedName} (parent=${parent?.kClass?.qualifiedName})")
		for (handler in handlers) handler.print(indentation + 1)
	}
}