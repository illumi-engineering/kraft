package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class ServiceContainer(
    val parentLayer: Layer
) {
    val serviceFactories = mutableMapOf<KClass<out Service>, ServiceFactory<*>>()
    
    fun <TService : Service> register(serviceClass: KClass<out TService>, factory: ServiceFactory<TService>) {
        serviceFactories[serviceClass] = factory
    }
    
    fun <TService : Service> getService(serviceClass: KClass<out TService>, accessor: ServiceAccessor? = null): TService? =
        serviceFactories[serviceClass]?.get(accessor ?: parentLayer)?.let { serviceClass.safeCast(it) }
    
    fun <TService : Service> requireService(serviceClass: KClass<out TService>): TService =
        getService(serviceClass) 
            ?: throw IllegalStateException("Service ${serviceClass.qualifiedName} not registered in layer ${parentLayer::class.simpleName}")
}