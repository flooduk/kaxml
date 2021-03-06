package uk.flood.xml

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Attr(
    val value: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Value()

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class Node(
    val value: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class NodeList(
    val value: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class FlatList()

class XmlNodeDescription(
    val klass: KClass<*>
) {
    val attr = mutableMapOf<String, KMutableProperty1<*, *>>()
    val node = mutableMapOf<String, KMutableProperty1<*, *>>()
    val list = mutableMapOf<String, KMutableProperty1<*, *>>()
    var flatList: KMutableProperty1<*, *>? = null
    var value: KMutableProperty1<*, *>? = null
}

