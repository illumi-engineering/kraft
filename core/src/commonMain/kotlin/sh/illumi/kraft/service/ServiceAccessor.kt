package sh.illumi.kraft.service

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer

abstract class ServiceAccessor {
    abstract val serviceContainer: ServiceContainer
    abstract val coroutineScope: CoroutineScope
    abstract val layer: Layer
    open val label: String get() = this::class.simpleName ?: "Abstract ServiceAccessor"
    
    inline fun <reified TService : Service> service(factory: ServiceFactory<TService, *>) =
        serviceContainer.getService(factory, this)
}