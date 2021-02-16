package net.nee.event

/*
@Suppress("UNCHECKED_CAST")
val <E : Event> KClass<E>.handler
	get() = companionObjectInstance as Event.Handler<E>

inline fun <reified E : Event> on(priority: Comparable<*> = Event.net.nee.event.Listener.Priority.NORMAL, noinline call: (E, remove: () -> Unit) -> Unit) {
	val listeners = E::class.handler.listeners

	@Suppress("JoinDeclarationAndAssignment")
	lateinit var listener: Event.net.nee.event.Listener<E>
	listener = Event.net.nee.event.Listener(priority) { event -> call(event) { listeners -= listener } }

	listeners += listener
}

inline fun <reified E : Event> on(
	priority: Comparable<*> = Event.net.nee.event.Listener.Priority.NORMAL,
	noinline call: (E) -> Unit
) {
	E::class.handler.listeners += Event.net.nee.event.Listener(priority, call = call)
}

inline fun <reified E : Event> once(
	priority: Comparable<*> = Event.net.nee.event.Listener.Priority.NORMAL,
	noinline call: (E) -> Unit
) = on<E>(priority) { event, remove ->
	call(event)
	remove()
}*/
