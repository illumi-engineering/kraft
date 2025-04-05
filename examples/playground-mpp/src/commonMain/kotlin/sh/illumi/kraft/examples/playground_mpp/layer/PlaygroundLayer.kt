package sh.illumi.kraft.examples.playground_mpp.layer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sh.illumi.kraft.examples.playground_mpp.service.TestService
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.service.registering
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.provider.LoggingProvider
import sh.illumi.kraft.x.logging.service.LoggingService

class PlaygroundLayer(
    override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : Layer() {
    val log by registering(LoggingService) {
        withProvider(LoggingProvider.StdOut) {
            withLevel(LogLevel.Info)
        }
    }
    
    suspend fun getSomeData(): String {
        delay(1000)
        return "Some data"
    }
    
    suspend fun updateUi(value: String) {
        delay(1000)
        log.info("Updating UI with value: $value")
    }
    
    fun someBlockingIoCall() {
        log.info("This is a blocking IO call")
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                getSomeData()
            }
            
            updateUi(result)
        }
    }
    
    val testService by registering(TestService)
    
    fun start() {
        log.info("Hello, KRAFT Playground!")
        
        testService.onStart()
    }
}