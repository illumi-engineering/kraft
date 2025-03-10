package sh.illumi.kraft.layer

import kotlinx.coroutines.test.TestScope
import sh.illumi.kraft.service.OwnedTestService
import sh.illumi.kraft.service.TestService
import sh.illumi.kraft.service.registering

object TestLayer : Layer() {
    override val coroutineScope = TestScope()
    
    val testOwnedService by registering(OwnedTestService)
    
    val testService by registering(TestService)
    
    fun getTestValue() = testOwnedService.someOtherValue
}