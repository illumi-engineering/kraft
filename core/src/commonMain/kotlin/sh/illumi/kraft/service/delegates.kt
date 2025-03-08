package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer
import kotlin.reflect.KProperty

class LayerServiceDelegate<TService : Service, TConfig : Any>(
    private val serviceFactory: ServiceFactory<TService, TConfig>
) {
    operator fun getValue(thisRef: Layer, property: KProperty<*>): TService = 
        serviceFactory.get(thisRef)
}

class OwnedServiceDependencyDelegate<TService : Service, TConfig : Any>(
    private val serviceFactory: ServiceFactory<TService, TConfig>
) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService = 
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

@Suppress("UNCHECKED_CAST")
inline fun <reified TService : Service, reified TConfig : Any> Service.dependingOnOwned(
    factory: ServiceFactory.Owned<TService, TConfig>
): OwnedServiceDependencyDelegate<TService, TConfig> {
    val factory = serviceContainer.serviceFactories[TService::class]?.let {
        it as? ServiceFactory.Owned<TService, TConfig> ?: throw IllegalArgumentException("Service ${TService::class.qualifiedName} is not an Owned service")
    } ?: throw IllegalArgumentException("Service ${TService::class.qualifiedName} is not registered in layer ${this.layer::class.simpleName}")

    return OwnedServiceDependencyDelegate<TService, TConfig>(factory)
}
