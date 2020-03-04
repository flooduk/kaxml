package uk.flood.xmlparser

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class Attr(
    val value: String
)

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

class XmlNodeDescription(
    val klass: KClass<*>
) {
    val attributes = mutableMapOf<String, KMutableProperty1<*, *>>()
    val nodes = mutableMapOf<String, KMutableProperty1<*, *>>()
    val nodeList = mutableMapOf<String, KMutableProperty1<*, *>>()

}

