package net.nee.packet.data

import net.nee.units.VarInt
import net.nee.units.ViewDistance
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

/**
 * Used when one type here can appear as multiple different types in packets.
 *
 * Example: [ViewDistance], can be [Byte] (default) or [VarInt]
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class Unit(val kClass: KClass<*>, val nullable: Boolean = false, vararg val arguments: Unit)

internal fun Unit.toKType(): KType =
	kClass.createType(arguments.map { KTypeProjection(KVariance.INVARIANT, it.toKType()) }, nullable)