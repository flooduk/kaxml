package uk.flood.xmlparser

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class AnnotatesReference<T : Any>(val klass: KClass<T>) {

    private val mapex = mutableMapOf<String, XmlNodeDescription>()

    fun get(name: String) = mapex[name]

    init {
        val startTime = System.currentTimeMillis()
        klass.findAnnotation<Node>()?.let {
            addElement(it.value, klass)
        } ?: throw IllegalStateException("class $klass is not annotated with @Node")
        val stopTime = System.currentTimeMillis()
        println("XmlParser<${klass.simpleName}> init in ${stopTime - startTime} ms")
    }

    private fun addElement(name: String, klass: KClass<*>) {
        XmlNodeDescription(klass).also {
            prepareInternal(it)
            mapex[name] = it
        }
    }

    private fun KProperty1<*, *>.type(isNodeList: Boolean): KClass<*> {
        return when {
            isList() -> (returnType.arguments[0].type!!.classifier as KClass<*>)
            !isNodeList -> (returnType.classifier as KClass<*>)
            else -> throw IllegalStateException("@NodeList for non-list property")
        }
    }

    private fun prepareInternal(value: XmlNodeDescription) {
        if (value.klass.isSealed) {
            value.klass.nestedClasses.forEach { klass ->
                klass.findAnnotation<Node>()?.let {
                    addElement(it.value, klass)
                }
            }
        }
        value.klass.declaredMemberProperties.forEach { property ->
            if (property is KMutableProperty1) {
                property.annotations.forEach {
                    when (it) {
                        is Attr -> {
                            value.attributes[it.value] = property
                        }
                        is Node -> {
                            value.nodes[it.value] = property
                            addElement(it.value, property.type(false))
                        }
                        is NodeList -> {
                            value.nodeList[it.value] = property
                            addElement(it.value, property.type(true))
                        }
                    }
                }
            }
        }
    }

}