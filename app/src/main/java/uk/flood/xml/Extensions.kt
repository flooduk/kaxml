package uk.flood.xml

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf


internal fun KProperty1<*, *>.isList(): Boolean {
    return returnType.isSubtypeOf(List::class.createType(listOf(KTypeProjection.STAR), true))
}

internal fun KProperty1<*, *>.type(isNodeList: Boolean): KClass<*> {
    return when {
        isList() -> (returnType.arguments[0].type!!.classifier as KClass<*>)
        !isNodeList -> (returnType.classifier as KClass<*>)
        else -> throw IllegalStateException("@NodeList for non-list property")
    }
}
