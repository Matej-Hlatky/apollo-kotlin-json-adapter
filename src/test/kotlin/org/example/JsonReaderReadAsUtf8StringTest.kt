package org.example

import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.MapJsonReader
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/** Tests for [readAsUtf8String]. */
@DisplayName("JsonReader.readAsUtf8String")
class JsonReaderReadAsUtf8StringTest {

    @Test
    fun `read null from JSON`() {
        val reader = jsonReader("null")
        val actual = reader.readAsUtf8String()

        assertEquals("null", actual)
    }

    @Test
    fun `read string from JSON`() {
        val reader = jsonReader("\"Hello JSON!\"")
        val actual = reader.readAsUtf8String()

        assertEquals("\"Hello JSON!\"", actual)
    }

    @Test
    fun `read number from JSON`() {
        val reader = jsonReader("123")
        val actual = reader.readAsUtf8String()

        assertEquals("123", actual)
    }

    @Test
    fun `read boolean from JSON`() {
        val reader = jsonReader("false")
        val actual = reader.readAsUtf8String()

        assertEquals("false", actual)
    }

    @Test
    fun `read empty array from JSON`() {
        val reader = jsonReader("[]")
        val actual = reader.readAsUtf8String()

        assertEquals("[]", actual)
    }

    @Test
    fun `read empty object from JSON`() {
        val reader = jsonReader(emptyMap())
        val actual = reader.readAsUtf8String()

        assertEquals("{}", actual)
    }

    @Test
    fun `read object with one attribute from JSON`() {
        val reader = jsonReader(mapOf("attr" to "value"))
        val actual = reader.readAsUtf8String()

        assertEquals("{\"attr\":\"value\"}", actual)
    }

    @Test
    fun `read object with multiple attributes of all primitive types from JSON`() {
        val data = mapOf(
            "number" to -123,
            "Boolean" to true,
            "String" to "Hello!",
            "Long" to Long.MAX_VALUE,
            "Double" to Math.PI,
            "Null" to null
        )
        val reader = jsonReader(data)
        val actual = reader.readAsUtf8String()

        assertEquals(
            "{\"number\":-123,\"Boolean\":true,\"String\":\"Hello!\",\"Long\":${Long.MAX_VALUE},\"Double\":${Math.PI},\"Null\":null}",
            actual
        )
    }

    @Test
    fun `read object with complex data from JSON`() {
        val data = mapOf(
            "data" to mapOf<String, Any>(
                "groups" to listOf(
                    mapOf(
                        "name" to "Credit Card",
                        "types" to listOf(
                            "amex",
                            "bcmc",
                            "diners",
                            "discover",
                            "maestro",
                            "mc",
                            "visa"
                        )
                    )
                )
            )
        )
        val reader = jsonReader(data)
        val actual = reader.readAsUtf8String()
        assertEquals(
            "{\"data\":{\"groups\":[{\"name\":\"Credit Card\",\"types\":[\"amex\",\"bcmc\",\"diners\",\"discover\",\"maestro\",\"mc\",\"visa\"]}]}}",
            actual
        )
    }

    @Test
    fun `read object with more complex data from JSON`() {
        val data = mapOf(
            "resultCode" to "RedirectShopper",
            "action" to mapOf(
                "paymentData" to "PD",
                "paymentMethodType" to "scheme",
                "url" to "https://checkoutshopper-test.adyen.com",
                "method" to "GET",
                "type" to "redirect",
            ),
            "details" to listOf(
                mapOf(
                    "key" to "MD",
                    "type" to "text"
                ),
                mapOf(
                    "key" to "PaRes",
                    "type" to "text"
                )
            ),
            "paymentData" to "PD",
            "redirect" to mapOf(
                "method" to "GET",
                "url" to "https://checkoutshopper-test.adyen.com",
            )
        )
        val reader = jsonReader(data)
        val actual = reader.readAsUtf8String()
        assertEquals(
            "{\"resultCode\":\"RedirectShopper\",\"action\":{\"paymentData\":\"PD\",\"paymentMethodType\":\"scheme\",\"url\":\"https://checkoutshopper-test.adyen.com\",\"method\":\"GET\",\"type\":\"redirect\"},\"details\":[{\"key\":\"MD\",\"type\":\"text\"},{\"key\":\"PaRes\",\"type\":\"text\"}],\"paymentData\":\"PD\",\"redirect\":{\"method\":\"GET\",\"url\":\"https://checkoutshopper-test.adyen.com\"}}",
            actual
        )
    }

    @Test
    fun `read next attribute from JSON`() {
        val data = mapOf(
            "first" to "w/e",
            "nested" to listOf(1, 2, 3),
            "second" to "w/e 2",
        )
        val reader = jsonReader(data)

        reader.beginObject() // {
        reader.nextName() // first=
        reader.nextString() // w/e
        reader.nextName() // nested=
        val actual = reader.readAsUtf8String()
        reader.nextName() // second=
        reader.nextString() // w/e 2
        reader.endObject() // }

        assertEquals("[1,2,3]", actual)
    }

    @Test
    fun `read nested object from JSON`() {
        val data = mapOf(
            "first" to "w/e",
            "attribute" to "value"
        )
        val reader = jsonReader(data)

        reader.beginObject() // {
        reader.nextName() //first=
        reader.nextString() // "w/e"
        reader.nextName() // attribute=
        val actual = reader.readAsUtf8String()
        reader.endObject() // }

        assertEquals("\"value\"", actual)
    }

    private fun jsonReader(root: Map<String, Any?>): JsonReader =
        MapJsonReader(root)
}
