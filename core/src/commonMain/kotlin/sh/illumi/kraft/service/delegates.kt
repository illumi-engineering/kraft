package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class LayerServiceDelegate<TService : Service>(private val serviceClass: KClass<out TService>) {
    operator fun getValue(thisRef: Layer, property: KProperty<*>): TService = 
        thisRef.serviceContainer.requireService(serviceClass)
}

class ServiceAsNeededDependencyDelegate<TService : Service>(
    private val serviceFactory: ServiceFactory<TService>
) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService = 
        serviceFactory.get(thisRef)
}

class ServiceDependencyDelegate<TService : Service>(private val serviceClass: KClass<out TService>) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService = 
        thisRef.serviceContainer.requireService(serviceClass)
}

class ServiceSoftDependencyDelegate<TService : Service>(private val serviceClass: KClass<out TService>) {
    operator fun getValue(thisRef: Service, property: KProperty<*>): TService? = 
        thisRef.serviceContainer.getService(serviceClass)
}

inline fun <reified TService : Service> Layer.registering(factory: ServiceFactory<TService>): LayerServiceDelegate<TService> {
    serviceContainer.register(TService::class, factory)
    
    return LayerServiceDelegate(TService::class)
}

inline fun <reified TService : Service> Service.dependingOn() = ServiceDependencyDelegate(TService::class)

inline fun <reified TService : Service> Service.softDependingOn(): ServiceSoftDependencyDelegate<TService> {
    return ServiceSoftDependencyDelegate(TService::class)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified TService : Service> Service.dependingOnOwned(
    factory: ServiceFactory.Owned<TService>
): ServiceAsNeededDependencyDelegate<TService> {
    val factory = serviceContainer.serviceFactories.getOrPut(TService::class) { factory }

    if (factory !is ServiceFactory.Owned<*>)
        throw IllegalArgumentException("Service ${TService::class.qualifiedName} is not an Owned service")

    return ServiceAsNeededDependencyDelegate<TService>(factory as ServiceFactory.Owned<TService>)
}
