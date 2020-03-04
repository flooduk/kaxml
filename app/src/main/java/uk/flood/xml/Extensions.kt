package uk.flood.xmlparser

import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf


fun KProperty1<*, *>.isList(): Boolean {
    return returnType.isSubtypeOf(List::class.createType(listOf(KTypeProjection.STAR), true))
}