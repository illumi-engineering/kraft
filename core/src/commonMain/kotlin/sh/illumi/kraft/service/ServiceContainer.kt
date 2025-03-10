package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer
import kotlin.reflect.KClass

class ServiceContainer(
    val parentLayer: Layer
) {
    val serviceFactories = mutableMapOf<KClass<out Service>, ServiceFactory<*, *>>()
    val services = mutableMapOf<ServiceKey, Service>()
    
    fun <TService : Service, TConfig : Any> register(
        serviceClass: KClass<out TService>,
        factory: ServiceFactory<TService, TConfig>,
        configure: TConfig.() -> Unit
    ) {
        factory.configure = configure
        serviceFactories[serviceClass] = factory
    }
    
    fun <TService : Service> getService(factory: ServiceFactory<TService, *>, accessor: ServiceAccessor? = null) =
        factory.get(accessor ?: parentLayer)
}