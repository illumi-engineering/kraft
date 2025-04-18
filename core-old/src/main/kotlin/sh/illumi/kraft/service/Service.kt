package sh.illumi.kraft.service

import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sh.illumi.kraft.layer.ApplicationLayer

/**
 * Base interface for services
 *
 * @property coroutineScope The coroutine scope of the service's layer
 * @property log The logger for the service
 */
interface Service {
    val coroutineScope: CoroutineScope
    val rootLayer: ApplicationLayer
    val log: Logger get() = LoggerFactory.getLogger(this::class.java)

    fun onStart() {
        log.info("Starting service ${this::class.simpleName}")
    }
    fun onShutdown() {}
}