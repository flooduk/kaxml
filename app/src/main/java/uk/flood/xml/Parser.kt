@file:Suppress("UNCHECKED_CAST")

package uk.flood.xml

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.*

class XmlParser<T : Any>(
    private val klass: KClass<T>,
    private val typeConverters: Map<KClass<*>, XmlTypeConverter<*>> = defaultTypesConverters
) {

    private val refs = AnnotatesReference(klass)

    private val builder by lazy {
        XmlBuilder<T>(mapex, typeConverters)
    }

    fun build(value: T): String {
        return builder.buildString(value)
    }

    fun parse(sourceXml: String): T {

        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(StringReader(sourceXml))

        val stack = Stack<Pair<XmlNodeDescription, Any>>()
        var firstTag: T? = null
        var currentTag: Any?

        var bool: Boolean

        while (true) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> {
                    refs.get(parser.name)?.let { nodeDescription ->
                        bool = true
                        if (!stack.isEmpty()) {
                            stack.peek().let { pair ->
                                pair.first.nodeList[parser.name]?.let { property ->
                                    mutableListOf<Any>().let { list ->
                                        stack.push(nodeDescription to list)
                                        property.setter.call(pair.second, list)
                                    }
                                    bool = false
                                }
                            }
                        }
                        if (bool) {
                            nodeDescription.klass.createInstance().also {
                                stack.push(nodeDescription to it)
                                currentTag = it
                            }
                            if (firstTag == null && klass.isInstance(currentTag)) {
                                firstTag = klass.cast(currentTag)
                            }
                            for (i in 0 until parser.attributeCount) {
                                nodeDescription.attributes[parser.getAttributeName(i)]?.let {
                                    it.setter.call(
                                        currentTag,
                                        cast(parser.getAttributeValue(i), it.returnType)
                                    )
                                }

                            }
                        }

                    }
                }
                XmlPullParser.END_TAG -> {
                    refs.get(parser.name)?.let { nodeDescription ->
                        if (nodeDescription == stack.peek().first) {
                            currentTag = stack.pop().second
                            if (!stack.isEmpty()) {
                                stack.peek().let {
                                    it.first.nodes[parser.name]?.let { property ->
                                        addNode(it.second, property, currentTag)
                                    }

                                    if (it.second is MutableList<*>) {
                                        currentTag?.let { value ->
                                            (it.second as MutableList<Any>).add(value)
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                XmlPullParser.END_DOCUMENT -> {
                    requireNotNull(firstTag) { "can't parse xml" }
                    return firstTag!!
                }
            }
        }

    }

    private fun addNode(objectReference: Any, property: KMutableProperty1<*, *>, value: Any?) {
        // найдена Node с именем
        if (property.isList()) {
            var currentValue: Any? = null
            if (!property.isLateinit) {
                currentValue = property.getter.call(objectReference)
            }
            if (currentValue == null) {
                currentValue = mutableListOf<Any>()
                property.setter.call(objectReference, currentValue)
            }
            (currentValue as MutableList<Any>).add(value!!)
        } else {
            property.setter.call(objectReference, value)
        }
    }


    private fun cast(value: String, type: KType): Any? {
        return typeConverters.entries.firstOrNull {
            type.isSupertypeOf(it.key.starProjectedType)
        }?.value?.to(value)
    }

    companion object {
        val defaultTypesConverters = mapOf(
            String::class to StringTypeConverter,
            Date::class to DateTypeConverter,
            Boolean::class to BooleanTypeConverter,
            Int::class to IntTypeConverter,
            BigDecimal::class to BigDecimalTypeConverter
        )
    }
}
