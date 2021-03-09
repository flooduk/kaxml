package uk.flood.xml

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testParse2() {

        val parser = Parser(NodeDown::class)
        val builder = Builder(NodeDown::class)

        val node = NodeDown().also {
            it.attr1 = "attr1"
            it.attr2 = "attr2"
        }

        val str = builder.build(node)
        val nd = parser.parse(str)

        println(node)
        println(str)
        println(nd)

    }

    @Test
    fun testParse() {
        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<RootNode Attribute1=\"string value\" Attribute2=\"42\">\n" +
                "  <InnerNode InnerAttribute1=\"inner value\" InnerAttribute2=\"17\"/>\n" +
                "  <ManyNodes EachNodeAttribute=\"a1\"/>\n" +
                "  <ManyNodes EachNodeAttribute=\"a2\"/>\n" +
                "  <ManyNodes EachNodeAttribute=\"a3\"/>\n" +
                "  <NodeList>\n" +
                "    <Sealed1 Sealed1Attribute=\"v1\"/>\n" +
                "    <Sealed1 Sealed1Attribute=\"v2\"/>\n" +
                "    <Sealed2 Sealed1Attribute=\"v3\">\n" +
                "      <InnerSealedNode InnerAttribute1=\"inner value\" InnerAttribute2=\"17\"/>\n" +
                "    </Sealed2>\n" +
                "    <Sealed2 Sealed1Attribute=\"v4\">\n" +
                "      <InnerSealedNode InnerAttribute1=\"inner value\" InnerAttribute2=\"13\"/>\n" +
                "    </Sealed2>\n" +
                "  </NodeList>\n" +
                "</RootNode>\n"

        val parser = Parser(TestRootNode::class)
        val builder = Builder(TestRootNode::class)

        val testString = builder.build(TestRootNode().also {
            it.attribute1 = "123"
            it.attribute2 = 456
            it.innerList = mutableListOf<TestSealed.Sealed1>().also {
                it.add(TestSealed.Sealed1().also {
                    it.name = "789"
                })
            }
            it.flatList = listOf()
            it.innerNode = TestInnerNode().also {
                it.innerAttribute1 = "234"
            }
        })
        println(testString)

        println(xml)
        val data1 = parser.parse(xml)
        println(data1)
        val string1 = builder.build(data1)
        println(string1)
        val data2 = parser.parse(string1)
        val string2 = builder.build(data2)
        println(string2)

        assertEquals(string1, string2)


    }

}
