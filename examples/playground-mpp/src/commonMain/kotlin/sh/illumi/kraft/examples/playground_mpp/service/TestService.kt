package sh.illumi.kraft.examples.playground_mpp.service

import sh.illumi.kraft.service.Service
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.service.ServiceFactory
import sh.illumi.kraft.service.dependingOn
import sh.illumi.kraft.x.logging.service.LoggingService

class TestService(
    accessor: ServiceAccessor
) : Service(accessor) {
    override val displayName = "Test Service"
    
    val log by dependingOn(LoggingService)

    override fun onStart() {
        log.debug("This message should not be displayed")
        log.info("$displayName started")
    }
    
    companion object Factory : ServiceFactory.Singleton<TestService, Nothing>(TestService::class) {
        override fun construct(accessor: ServiceAccessor, configure: Nothing.() -> Unit) = TestService(accessor)
    }
}