package uk.flood.xml

// no need annotate sealed class
sealed class TestSealed {

    @Node("Sealed1")
    class Sealed1 : TestSealed() {

        // required = true
        @Attr("Sealed1Attribute")
        lateinit var name: String

    }

    @Node("Sealed2")
    class Sealed2 : TestSealed() {

        // required = false
        @Attr("Sealed2Attribute")
        var value: Int? = null

        // required = true
        @Node("InnerSealedNode")
        lateinit var innerNode: TestInnerNode

    }

}

// no need annotate class, that used on annotated fields
class TestInnerNode {

    // required = true
    @Attr("InnerAttribute1")
    lateinit var innerAttribute1: String

    // required = false
    @Attr("InnerAttribute2")
    var innerAttribute2: Int? = null

}

// no need annotate @Node, if it is used only in @Node annotated List
class ManyNodes {

    // required = false
    @Attr("EachNodeAttribute")
    var attribute: String? = null

}

// class must be annotated with @Node if it used as root tag in xml
@Node("RootNode")
class TestRootNode {

    // required = false
    @Attr("Attribute1")
    var attribute1: String? = null

    // required = false
    @Attr("Attribute2")
    var attribute2: Int? = null

    // required = true
    @Node("InnerNode")
    lateinit var innerNode: TestInnerNode

    // required = true
    @Node("ManyNodes")
    lateinit var flatList: List<ManyNodes>

    // required = true
    @NodeList("NodeList")
    lateinit var innerList: MutableList<TestSealed.Sealed1>

}

@Node("NodeUp")
open class NodeUp {
    @Attr("attr1")
    var attr1: String? = null
}

@Node("NodeDown")
class NodeDown: NodeUp() {

    @Attr("attr2")
    var attr2: String? = null

}