package uk.flood.xml

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal class XmlBuilder<T : Any>(
    private val map: Map<String, XmlNodeDescription>,
    private val converters: Map<KClass<*>, XmlTypeConverter<*>>
) {

    private data class BuilderXmlEntity(
        val tag: String,
        var hasInnerData: Boolean = false
    )

    private val sb = StringBuilder(2048).append(header)

    private val stack = Stack<BuilderXmlEntity>()

    private fun fillAttrs(node: Any, nodeName: String) {
        map[nodeName]?.attributes?.entries?.forEach { (s, p) ->
            attr(p.getter.call(node), s)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun fillNodes(value: Any, valueNodeName: String) {
        if (value is List<*>) {
            (value as List<Any>).forEach { v ->
                addNode(v, v::class.findAnnotation<Node>()?.value ?: valueNodeName)
            }
        } else {
            map[valueNodeName]?.nodeList?.forEach { (s, p) ->
                p.getter.call(value)?.let { propertyValue ->
                    addNode(propertyValue, s)
                }
            }

            map[valueNodeName]?.nodes?.forEach { (s, p) ->
                p.getter.call(value)?.let { propertyValue ->
                    if (propertyValue is List<*>) {
                        fillNodes(propertyValue, s)
                    } else {
                        addNode(propertyValue, s)
                    }
                }
            }

        }
    }

    private fun attr(value: Any?, name: String) {
        value?.let {
            converters[it::class]?.from(it)?.let { converted ->
                sb.append(" $name=\"$converted\"")
            }
        }
    }

    private fun closeTag() {
        if (sb.isNotEmpty() && sb.last() != '>') {
            sb.append(">")
        }
    }

//    private fun value(value: String?) {
//        value?.let {
//            stack.peek().hasInnerData = true
//            closeTag()
//            sb.append(value)
//        }
//    }

    private fun tag(name: String) {
        if (!stack.empty()) {
            stack.peek().hasInnerData = true
        }
        closeTag()
        sb.append("<${stack.push(BuilderXmlEntity(name)).tag}")
    }

    private fun end() {
        if (stack.peek().hasInnerData) {
            sb.append("</${stack.pop().tag}>")
        } else {
            sb.append("/>")
            stack.pop()
        }
    }

    private fun clear() {
        sb.setLength(0)
        sb.append(header)
    }

    override fun toString(): String {
        return sb.toString()
    }

    private fun addNode(element: Any, elementName: String) {
        tag(elementName)
        fillAttrs(element, elementName)
        fillNodes(element, elementName)
        end()
    }

    fun buildString(element: T): String {
        element::class.findAnnotation<Node>()?.let {
            clear()
            addNode(element, it.value)
        } ?: throw IllegalArgumentException("add @Node annotation to $this")
        return toString()
    }

    companion object {
        private const val header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    }

}