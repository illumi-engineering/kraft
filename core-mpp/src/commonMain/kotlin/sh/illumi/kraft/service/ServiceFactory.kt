package sh.illumi.kraft.service

sealed interface ServiceFactory<TService : Service> {
    fun get(accessor: ServiceAccessor): TService
    
    class Singleton<TService : Service>(private val ctor: (ServiceAccessor) -> TService) : ServiceFactory<TService> {
        private var instance: TService? = null
        
        override fun get(accessor: ServiceAccessor) =
            instance ?: ctor(accessor).also { instance = it }
    }
    
    class Owned<TService : Service>(private val ctor: (ServiceAccessor) -> TService) : ServiceFactory<TService> {
        private val servicesForAccessors = mutableMapOf<ServiceAccessor, TService>()
        override fun get(accessor: ServiceAccessor) =
            servicesForAccessors.getOrPut(accessor) { ctor(accessor) }
    }
}