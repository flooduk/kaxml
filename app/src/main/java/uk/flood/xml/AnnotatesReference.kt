package uk.flood.xml

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

class AnnotatesReference private constructor(
    val klass: KClass<*>
) {

    private val mapex = mutableMapOf<String, XmlNodeDescription>()

    fun get(name: String) = mapex[name]

    init {
        val startTime = System.currentTimeMillis()
        klass.findAnnotation<Node>()?.let {
            addElement(it.value, klass)
        } ?: throw IllegalStateException("class $klass is not annotated with @Node")
        val stopTime = System.currentTimeMillis()
        println("Annotates reference to ${klass.simpleName} init in ${stopTime - startTime} ms")
    }

    private fun addElement(name: String, klass: KClass<*>) {
        XmlNodeDescription(klass).also {
            prepareInternal(it)
            mapex[name] = it
        }
    }

    private fun prepareInternal(value: XmlNodeDescription) {
        if (value.klass.isSealed) {
            value.klass.nestedClasses.forEach { klazz ->
                klazz.findAnnotation<Node>()?.let {
                    addElement(it.value, klazz)
                }
            }
        }
        value.klass.declaredMemberProperties.forEach { property ->
            if (property is KMutableProperty1) {
                property.annotations.forEach {
                    when (it) {
                        is Attr -> {
                            value.attr[it.value] = property
                        }
                        is Node -> {
                            value.node[it.value] = property
                            addElement(it.value, property.type(false))
                        }
                        is NodeList -> {
                            value.list[it.value] = property
                            val xklass = property.type(true)
                            val elementName = xklass.findAnnotation<Node>()?.value ?: it.value
                            addElement(elementName, xklass)
                            addElement(it.value, xklass)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val cache = mutableMapOf<KClass<*>, AnnotatesReference>()
        fun provide(klass: KClass<*>): AnnotatesReference =
            cache[klass] ?: AnnotatesReference(klass).also { cache[klass] = it }
    }
}