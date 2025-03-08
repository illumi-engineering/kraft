package sh.illumi.kraft.service

import kotlin.reflect.KClass

sealed interface ServiceFactory<TService : Service, TConfig : Any> {
    var configure: TConfig.() -> Unit
    fun construct(accessor: ServiceAccessor, configure: TConfig.() -> Unit): TService
    fun get(accessor: ServiceAccessor): TService
    
    abstract class Singleton<TService : Service, TConfig : Any>(
        private val serviceClass: KClass<out TService>
    ) : ServiceFactory<TService, TConfig> {
        override lateinit var configure: TConfig.() -> Unit
        
        @Suppress("UNCHECKED_CAST")
        override fun get(accessor: ServiceAccessor): TService {
            val key = ServiceKey(ServiceKey.Tag.ServiceClass(serviceClass))
            val service = accessor.serviceContainer.services.getOrPut(key) { construct(accessor, configure) }
            return service as TService
        }
    }
    
    abstract class Owned<TService : Service, TConfig : Any>(
        private val serviceClass: KClass<out TService>
    ) : ServiceFactory<TService, TConfig> {
        override lateinit var configure: TConfig.() -> Unit
        
        @Suppress("UNCHECKED_CAST")
        override fun get(accessor: ServiceAccessor): TService {
            val key = ServiceKey(ServiceKey.Tag.ServiceClass(serviceClass), ServiceKey.Tag.Accessor(accessor))
            val service = accessor.serviceContainer.services.getOrPut(key) { construct(accessor, configure) }
            return service as TService
        }
    }
}