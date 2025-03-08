package sh.illumi.kraft.examples.playground_mpp.layer

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.examples.playground_mpp.service.TestService
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.service.registering
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.provider.LoggingProvider
import sh.illumi.kraft.x.logging.service.LoggingService

class PlaygroundLayer(
    override val coroutineScope: CoroutineScope
) : Layer() {
    val log by registering(LoggingService) {
        withProvider(LoggingProvider.StdOut) {
            withLevel(LogLevel.Info)
        }
    }
    
    val testService by registering(TestService)
    
    fun start() {
        log.info("Hello, KRAFT Playground!")
        
        testService.onStart()
    }
}