package sh.illumi.kraft.service

class OwnedTestService(accessor: ServiceAccessor) : Service(accessor) {
    override val displayName: String = "Owned Test Service"
    
    val someOtherValue = 500
    
    companion object Factory : ServiceFactory.Owned<OwnedTestService, Nothing>(OwnedTestService::class) {
        override fun construct(accessor: ServiceAccessor, configure: Nothing.() -> Unit) = OwnedTestService(accessor)
    }
}