package org.example.graphql.type.adapter

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.mockserver.MockResponse
import com.apollographql.apollo3.mockserver.MockServer
import kotlinx.coroutines.runBlocking
import org.example.graphql.PaymentMethodsQuery
import org.example.graphql.SubmitPaymentDetailsMutation
import org.example.graphql.type.AdyenChannelEnum
import org.example.graphql.type.AdyenPaymentResultCodeEnum
import org.example.graphql.type.AdyenRedirectMethodEnum
import org.example.graphql.type.GraphQLJson
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.skyscreamer.jsonassert.JSONAssert
import kotlin.test.*

/** Tests for [JsonAdapter]. */
@DisplayName("JsonAdapter")
class JsonAdapterTest {

    @Test
    fun `use adapter for whole JSON response`() {
        val responseJsonBody = """
{
    "data": {
        "adyen": {
            "paymentMethods": {
                "groups": [
                    {
                        "name": "Credit Card",
                        "types": [
                            "amex",
                            "bcmc",
                            "diners",
                            "discover",
                            "maestro",
                            "mc",
                            "visa"
                        ]
                    }
                ],
                "paymentMethods": [
                    {
                        "brands": [
                            "amex",
                            "bcmc",
                            "diners",
                            "discover",
                            "maestro",
                            "mc",
                            "visa"
                        ],
                        "details": [
                            {
                                "key": "encryptedCardNumber",
                                "type": "cardToken"
                            },
                            {
                                "key": "encryptedSecurityCode",
                                "type": "cardToken"
                            },
                            {
                                "key": "encryptedExpiryMonth",
                                "type": "cardToken"
                            },
                            {
                                "key": "encryptedExpiryYear",
                                "type": "cardToken"
                            },
                            {
                                "key": "holderName",
                                "optional": true,
                                "type": "text"
                            }
                        ],
                        "name": "Credit Card",
                        "supportsRecurring": true,
                        "type": "scheme"
                    },
                    {
                        "brands": [
                            "bcmc",
                            "maestro",
                            "visa"
                        ],
                        "details": [
                            {
                                "key": "encryptedCardNumber",
                                "type": "cardToken"
                            },
                            {
                                "key": "encryptedExpiryMonth",
                                "type": "cardToken"
                            },
                            {
                                "key": "encryptedExpiryYear",
                                "type": "cardToken"
                            },
                            {
                                "key": "holderName",
                                "optional": true,
                                "type": "text"
                            }
                        ],
                        "name": "Bancontact card",
                        "supportsRecurring": true,
                        "type": "bcmc"
                    },
                    {
                        "details": [
                            {
                                "key": "paywithgoogle.token",
                                "type": "payWithGoogleToken"
                            }
                        ],
                        "name": "Google Pay",
                        "supportsRecurring": true,
                        "type": "paywithgoogle"
                    },
                    {
                        "details": [
                            {
                                "key": "sepa.ownerName",
                                "type": "text"
                            },
                            {
                                "key": "sepa.ibanNumber",
                                "type": "text"
                            }
                        ],
                        "name": "SEPA Direct Debit",
                        "supportsRecurring": true,
                        "type": "sepadirectdebit"
                    }
                ]
            }
        }
    }
}"""

        server.enqueue(responseJsonBody)

        val response = runBlocking {
            apolloClient.query(
                PaymentMethodsQuery(
                    channel = AdyenChannelEnum.Android,
                    countryCode = "EN",
                    amount = 100
                )
            ).execute()
        }

        val data = assertNotNull(response.data)
        val actualJson = data.adyen.paymentMethods

        val expectedJson = """
        {
            "groups": [
                {
                    "name": "Credit Card",
                    "types": [
                        "amex",
                        "bcmc",
                        "diners",
                        "discover",
                        "maestro",
                        "mc",
                        "visa"
                    ]
                }
            ],
            "paymentMethods": [
                {
                    "brands": [
                        "amex",
                        "bcmc",
                        "diners",
                        "discover",
                        "maestro",
                        "mc",
                        "visa"
                    ],
                    "details": [
                        {
                            "key": "encryptedCardNumber",
                            "type": "cardToken"
                        },
                        {
                            "key": "encryptedSecurityCode",
                            "type": "cardToken"
                        },
                        {
                            "key": "encryptedExpiryMonth",
                            "type": "cardToken"
                        },
                        {
                            "key": "encryptedExpiryYear",
                            "type": "cardToken"
                        },
                        {
                            "key": "holderName",
                            "optional": true,
                            "type": "text"
                        }
                    ],
                    "name": "Credit Card",
                    "supportsRecurring": true,
                    "type": "scheme"
                },
                {
                    "brands": [
                        "bcmc",
                        "maestro",
                        "visa"
                    ],
                    "details": [
                        {
                            "key": "encryptedCardNumber",
                            "type": "cardToken"
                        },
                        {
                            "key": "encryptedExpiryMonth",
                            "type": "cardToken"
                        },
                        {
                            "key": "encryptedExpiryYear",
                            "type": "cardToken"
                        },
                        {
                            "key": "holderName",
                            "optional": true,
                            "type": "text"
                        }
                    ],
                    "name": "Bancontact card",
                    "supportsRecurring": true,
                    "type": "bcmc"
                },
                {
                    "details": [
                        {
                            "key": "paywithgoogle.token",
                            "type": "payWithGoogleToken"
                        }
                    ],
                    "name": "Google Pay",
                    "supportsRecurring": true,
                    "type": "paywithgoogle"
                },
                {
                    "details": [
                        {
                            "key": "sepa.ownerName",
                            "type": "text"
                        },
                        {
                            "key": "sepa.ibanNumber",
                            "type": "text"
                        }
                    ],
                    "name": "SEPA Direct Debit",
                    "supportsRecurring": true,
                    "type": "sepadirectdebit"
                }
            ]
        }
        """.trimIndent()

        assertTrue(actualJson.isNotEmpty())
        assertJsonEquals(expectedJson, actualJson)
    }

    @Test
    fun `use adapter for nested JSON response`() {
        val responseJsonBody = """
        {
            "data": {
                "adyen": {
                    "submitDetails": {
                        "__typename": "adyenPaymentResponseFragment",
                        "paymentId": "1",
                        "rawResponse": {
                            "additionalData": {
                                "cardSummary": "1239",
                                "shopperCountry": "SK",
                                "expiryDate": "3/2030",
                                "cardBin": "677183"
                            },
                            "pspReference": "MSJKVVK84TGLNK82",
                            "resultCode": "RedirectShopper",
                            "merchantReference": "351090_1681235908"
                        },
                        "resultCode": "RedirectShopper",
                        "captureResult": null,
                        "redirect": {
                            "data": "",
                            "httpMethod": "GET",
                            "url": "https://foo.bar/"
                        }
                    }
                }
            }
        }
        """.trimIndent()
        val requestDetailsJson = """
        {
            "var1": "foo",
            "var2": "bar"
        }
        """.trimIndent()
        val expectedRawResponseJson = """
        {
            "additionalData": {
                "cardSummary": "1239",
                "shopperCountry": "SK",
                "expiryDate": "3/2030",
                "cardBin": "677183"
            },
            "pspReference": "MSJKVVK84TGLNK82",
            "resultCode": "RedirectShopper",
            "merchantReference": "351090_1681235908"
        }
        """.trimIndent()

        server.enqueue(responseJsonBody)

        val response = runBlocking {
            apolloClient.mutation(
                SubmitPaymentDetailsMutation(
                    paymentId = "1",
                    request = requestDetailsJson,
                )
            ).execute()
        }

        val data = assertNotNull(response.data)
        val details = data.adyen.submitDetails.adyenPaymentResponseFragment

        assertEquals("1", details.paymentId)
        assertJsonEquals(expectedRawResponseJson, details.rawResponse)
        assertEquals(AdyenPaymentResultCodeEnum.RedirectShopper, details.resultCode)
        assertNull(details.captureResult)
        assertNotNull(details.redirect).also { redirect ->
            assertNotNull(redirect.data).also { redirectData ->
                assertJsonEquals("\"\"", redirectData)
            }
            assertEquals(AdyenRedirectMethodEnum.GET, redirect.httpMethod)
            assertEquals("https://foo.bar/", redirect.url)
        }
    }

    private fun MockServer.enqueue(body: String) =
        enqueue(MockResponse.Builder().body(body = body).build())

    private fun assertJsonEquals(expected: String, actual: GraphQLJson) =
        JSONAssert.assertEquals(expected, actual, true)

    companion object {

        @JvmStatic
        private lateinit var server: MockServer

        @JvmStatic
        private lateinit var apolloClient: ApolloClient

        @BeforeAll
        @JvmStatic
        fun before() {
            server = MockServer()

            val serverUrl = runBlocking {
                server.url()
            }
            apolloClient = ApolloClient.Builder().serverUrl(serverUrl).build()
        }

        @AfterAll
        @JvmStatic
        fun after() = runBlocking {
            server.stop()
        }
    }
}
