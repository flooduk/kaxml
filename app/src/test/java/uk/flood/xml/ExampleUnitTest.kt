package uk.flood.xmlparser

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

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
                "    <Sealed2 Sealed1Attribute=\"v3\"/>\n" +
                "    <Sealed2 Sealed1Attribute=\"v4\"/>\n" +
                "  </NodeList>\n" +
                "</RootNode>\n"

        XmlParser(TestRootNode::class).apply {
            println(xml)
            val data1 = parse(xml)
            println(data1)
            val string1 = build(data1)
            println(string1)
            val data2 = parse(string1)
            val string2 = build(data2)
            println(string2)

            assertEquals(string1, string2)

        }
    }

}