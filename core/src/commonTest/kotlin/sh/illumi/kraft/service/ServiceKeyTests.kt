package sh.illumi.kraft.service

import sh.illumi.kraft.layer.TestLayer
import kotlin.test.Test
import kotlin.test.asserter


class ServiceKeyTests {
    @Test
    fun testServiceKeyEquality() {
        val key1 = ServiceKey(ServiceKey.Tag.Accessor(TestLayer), ServiceKey.Tag.ServiceClass(TestService::class))
        val key2 = ServiceKey(ServiceKey.Tag.Accessor(TestLayer), ServiceKey.Tag.ServiceClass(TestService::class))

        asserter.assertEquals("Keys are not equal", key1, key2)
    }
    
    @Test
    fun testServiceKeyInequality() {
        val key1 = ServiceKey(ServiceKey.Tag.Accessor(TestLayer), ServiceKey.Tag.ServiceClass(TestService::class))
        val key2 = ServiceKey(ServiceKey.Tag.Accessor(TestLayer))

        asserter.assertNotEquals("Keys are equal", key1, key2)
    }
}