package uk.flood.xml

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


interface XmlTypeConverter<T : Any> {
    fun from(value: Any): String?
    fun to(value: String): T?
}

object StringTypeConverter : XmlTypeConverter<String> {

    override fun from(value: Any): String? = value.toString()

    override fun to(value: String) = value
}

object BigDecimalTypeConverter : XmlTypeConverter<BigDecimal> {

    override fun from(value: Any): String? = (value as? BigDecimal)?.toPlainString()

    override fun to(value: String) = try {
        BigDecimal(value)
    } catch (t: Throwable) {
        null
    }
}

object DateTypeConverter : XmlTypeConverter<Date> {

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    override fun from(value: Any): String? = (value as? Date)?.let { sdf.format(it) }

    override fun to(value: String): Date? = sdf.parse(value)
}

object BooleanTypeConverter : XmlTypeConverter<Boolean> {

    override fun from(value: Any): String? = (value as? Boolean)?.let {
        if (it) "true" else "false"
    }

    override fun to(value: String): Boolean = "true" == value

}

object IntTypeConverter : XmlTypeConverter<Int> {

    override fun from(value: Any): String? = (value as? Int)?.toString()

    override fun to(value: String) = try {
        value.toInt()
    } catch (e: NumberFormatException) {
        null
    }

}

fun defaultTypesConverters() = mapOf(
    String::class to StringTypeConverter,
    Date::class to DateTypeConverter,
    Boolean::class to BooleanTypeConverter,
    Int::class to IntTypeConverter,
    BigDecimal::class to BigDecimalTypeConverter
)
