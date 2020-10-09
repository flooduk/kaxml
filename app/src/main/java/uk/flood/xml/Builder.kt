package uk.flood.xml

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@Suppress("unused")
class Builder<T : Any>(
        klass: KClass<T>,
        private val converters: Map<KClass<*>, XmlTypeConverter<*>> = defaultTypesConverters()
) {

    private data class BuilderXmlEntity(
            val tag: String,
            var hasInnerData: Boolean = false
    )

    private val sb = StringBuilder(2048).append(header)

    private val stack = Stack<BuilderXmlEntity>()

    private val refs = AnnotatesReference.provide(klass)

    private fun fillAttrs(node: Any, nodeName: String) {
        if (node !is List<*>) {
            refs.get(nodeName)?.attr?.entries?.forEach { (s, p) ->
                attr(p.getter.call(node), s)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun fillNodes(value: Any, valueNodeName: String) {
        if (value is List<*>) {
            (value as List<Any>).forEach { v ->
                addNode(v, v::class.findAnnotation<Node>()?.value ?: valueNodeName)
            }
        } else {
            refs.get(valueNodeName)?.list?.forEach { (s, p) ->
                p.getter.call(value)?.let { propertyValue ->
                    addNode(propertyValue, s)
                }
            }

            refs.get(valueNodeName)?.node?.forEach { (s, p) ->
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
                val masked = mask(converted)
                sb.append(" $name=\"$masked\"")
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

    private fun mask(source: String): String {
        return source
                .replace("'", "&apos;")
                .replace("\"", "&quot;")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
    }

    override fun toString() = sb.toString()

    private fun addNode(element: Any, elementName: String) {
        tag(elementName)
        fillAttrs(element, elementName)
        fillNodes(element, elementName)
        end()
    }

    fun build(element: T): String {
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