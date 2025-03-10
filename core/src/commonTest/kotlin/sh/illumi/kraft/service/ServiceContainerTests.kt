package sh.illumi.kraft.service

import sh.illumi.kraft.layer.TestLayer
import kotlin.test.Test
import kotlin.test.asserter

class ServiceContainerTests {
    @Test
    fun `ServiceContainer can register and resolve singleton services`() {
        val layer = TestLayer
        
        asserter.assertEquals("layer cannot resolve TestService", layer.testService.someValue, 42)
    }
    
    @Test
    fun `ServiceContainer can register and resolve owned services`() {
        val layer = TestLayer
        
        asserter.assertEquals("layer cannot resolve OwnedTestService", layer.testOwnedService.someOtherValue, 500)
        
        asserter.assertEquals("service cannot resolve OwnedTestService", layer.testService.getTestValue(), 500)
    }
}