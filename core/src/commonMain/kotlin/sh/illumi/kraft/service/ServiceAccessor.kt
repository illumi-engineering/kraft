package sh.illumi.kraft.service

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer

abstract class ServiceAccessor {
    abstract val serviceContainer: ServiceContainer
    abstract val coroutineContext: CoroutineScope
    abstract val applicableLayer: Layer
    open val label: String get() = this::class.simpleName ?: "Abstract ServiceAccessor"
    
    inline fun <reified TService : Service> service(): TService? =
        serviceContainer.getService(TService::class)
    
    inline fun <reified TService : Service> requireService(): TService =
        serviceContainer.requireService(TService::class)
}