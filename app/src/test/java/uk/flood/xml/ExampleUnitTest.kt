package uk.flood.xml

import android.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import kotlin.properties.Delegates

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {


    class PrinterSettings {

        @Attr("onlyElectronReceipt")
        var onlyElectronReceipt: Boolean = false

        @Attr("calculateDiscounts")
        var calculateDiscounts: Boolean = false

        @Attr("printDividers")
        var printDividers: Boolean = false

        @Attr("printFullPrice")
        var printFullPrice: Boolean = false

        @Attr("printTaxes")
        var printTaxes: Boolean = false
    }

    @Node("requisite")
    class Requisite {
        @Attr("tag")
        var tag: Int by Delegates.notNull()

        @Attr("value")
        var value: String? = null

        @NodeList("fiscalRequisites")
        var requisites: List<Requisite> = mutableListOf()

        @Attr("type") // FiscalRequisiteType
        var type: Int by Delegates.notNull()

        @Attr("mustPrint")
        var mustPrint: Boolean = true

    }

    @Node("position")
    class Position {

        @Attr("name")
        lateinit var name: String

        @Attr("quantity")
        var quantity: BigDecimal = BigDecimal.ONE

        @Attr("price")
        lateinit var price: BigDecimal

        @Attr("priceWithDiscount")
        lateinit var priceWithDiscount: BigDecimal

        @Attr("sum")
        lateinit var sum: BigDecimal

        @Attr("tax")
        var tax: Int by Delegates.notNull()

        @Attr("settlementMethod")
        var settlementMethod: Int by Delegates.notNull()

        @Attr("partialSettlementSum")
        var partialSum: BigDecimal? = null

        @Attr("additionalInfoForPosition")
        var additionalInfo: String? = null

        @Attr("productType")
        var productType: Int by Delegates.notNull()

        @NodeList("fiscalRequisites")
        var fiscalRequisites: List<Requisite> = mutableListOf()
    }

    @Node("payment")
    class Payment {

        @Attr("type")
        var type: Int = 1

        @Attr("sum")
        lateinit var sum: BigDecimal

    }

    @Node("request")
    class FiscalReceiptRequest {

        @Attr("settlementType")
        var settlementType: Int by Delegates.notNull()

        @Attr("sessionNumber")
        var session: Int? = null

        @Attr("receiptNumber")
        var receipt: Int? = null

        @Attr("taxationSystem")
        var taxationSystem: Int by Delegates.notNull()

        @Attr("clientPhone")
        var clientPhone: String? = null

        @Attr("clientEmail")
        var clientEmail: String? = null

        @Node("printerSettings")
        var printerSettings: PrinterSettings? = null

        @NodeList("fiscalRequisites")
        var fiscalRequisites: List<Requisite> = mutableListOf()

        @NodeList("positions")
        lateinit var positions: List<Position>

        @NodeList("payments")
        lateinit var payments: List<Payment>

        override fun toString(): String {
            return "FiscalReceiptRequest(session=$session, receipt=$receipt, clientPhone=$clientPhone, clientEmail=$clientEmail, printerSettings=$printerSettings, fiscalRequisites=$fiscalRequisites, positions=$positions, payments=$payments)"
        }


    }

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
    fun testPrintReceipt() {
        val receipt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<request settlementType=\"1\" taxationSystem=\"1\">" +
                "<cashier inn=\"inn\" name=\"name\"/>" +
                "<positions>" +
                "<position name=\"test\" price=\"10.00\" tax=\"1\" settlementMethod=\"4\" productType=\"1\" priceWithDiscount=\"10.00\" sum=\"10.00\"/>" +
                "</positions>" +
                "<payments>" +
                "<payment type=\"1\" sum=\"10.00\"/>" +
                "</payments>" +
                "</request>"

        val obj = Parser(FiscalReceiptRequest::class).parse(receipt)
        println(obj)

        val xml = Builder(FiscalReceiptRequest::class).build(obj)
        println(xml)
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
