package sh.illumi.kraft.service

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer

interface ServiceAccessor {
    val serviceContainer: ServiceContainer
    val coroutineScope: CoroutineScope
    val label: String get() = this::class.simpleName ?: "Abstract ServiceAccessor"
}

inline fun <reified TService : Service> ServiceAccessor.service(factory: ServiceFactory<TService, *>) =
    serviceContainer.getService(factory, this)