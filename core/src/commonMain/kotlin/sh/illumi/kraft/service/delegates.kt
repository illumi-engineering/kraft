package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer
import kotlin.reflect.KProperty

class LayerServiceDelegate<TService : Service, TConfig : Any>(
    private val serviceFactory: ServiceFactory<TService, TConfig>
) {
    operator fun getValue(thisRef: Layer, property: KProperty<*>): TService = 
        serviceFactory.get(thisRef)
}

class ServiceDependencyDelegate<TService : Service>(
    private val factory: ServiceFactory<TService, *>
) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService = 
        factory.get(thisRef)
}

class ServiceSoftDependencyDelegate<TService : Service>(
    private val factory: ServiceFactory<TService, *>
) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService? = 
        factory.get(thisRef)
}

inline fun <reified TService : Service, TConfig : Any> Layer.registering(
    factory: ServiceFactory<TService, TConfig>,
    noinline configure: TConfig.() -> Unit = { }
): LayerServiceDelegate<TService, TConfig> {
    serviceContainer.register(TService::class, factory, configure)
    
    return LayerServiceDelegate(factory)
}

inline fun <reified TService : Service> Service.dependingOn(factory: ServiceFactory<TService, *>) =
    ServiceDependencyDelegate(factory)

inline fun <reified TService : Service> Service.softDependingOn(factory: ServiceFactory<TService, *>) =
    ServiceSoftDependencyDelegate(factory)
