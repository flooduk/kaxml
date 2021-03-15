package uk.flood.xml

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    sealed class Data {

        @Node("TextBlock")
        class TextBlock {
            @Attr("FontNum")
            lateinit var fontNum: String

            @Attr("Bold")
            lateinit var bold: String

            @Value
            lateinit var value: String

            override fun toString(): String {
                return "TextBlock(fontNum='$fontNum', bold='$bold', value='$value')"
            }


        }

        @Node("TextLine")
        class TextLine {
            @Attr("FontNum")
            lateinit var fontNum: String

            @Attr("Bold")
            lateinit var bold: String

            @Attr("Text")
            lateinit var text: String

            override fun toString(): String {
                return "TextLine(fontNum='$fontNum', bold='$bold', text='$text')"
            }


        }
    }

    @Node("Unfiscal")
    class Unfiscal {
        @Attr("Slip")
        lateinit var slip: String

        @Attr("CutAfter")
        lateinit var cutAfter: String

        @FlatList
        lateinit var list: List<Data>

        override fun toString(): String {
            return "Unfiscal(slip='$slip', cutAfter='$cutAfter' list=$list)"
        }


    }

    @Test
    fun tryToMakeUCS() {
        val unfiscal = Parser(Unfiscal::class).parse(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<Unfiscal Slip=\"1\" CutAfter=\"2\">" +
                    "<TextBlock FontNum=\"3\" Bold=\"4\">" +
                    "Строка 1" +
                    "Строка 2" +
                    "Строка 3" +
                    "</TextBlock>" +
                    "<TextLine FontNum=\"5\" Bold=\"6\" Text=\"data\"/>" +
                    "<TextLine FontNum=\"7\" Bold=\"8\" Text=\"data 2\"/>" +
                    "</Unfiscal>"
        )
        println(unfiscal)

        val xml = Builder(Unfiscal::class).build(unfiscal)
        println(xml)
    }

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
