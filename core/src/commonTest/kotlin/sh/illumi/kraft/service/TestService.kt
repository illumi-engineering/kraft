package sh.illumi.kraft.service

class TestService(accessor: ServiceAccessor) : Service(accessor) {
    override val displayName: String = "Test Service"
    
    val ownedTestService by dependingOn(OwnedTestService)
    
    val someValue = 42
    
    fun getTestValue() = ownedTestService.someOtherValue
    
    companion object Factory : ServiceFactory.Singleton<TestService, Nothing>(TestService::class) {
        override fun construct(accessor: ServiceAccessor, configure: Nothing.() -> Unit) = TestService(accessor)
    }
}