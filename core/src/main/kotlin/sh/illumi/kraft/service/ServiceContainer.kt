package sh.illumi.kraft.service

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.KraftException
import sh.illumi.kraft.ServiceContainerException
import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.util.argsMatchParams
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
class ServiceContainer(
    val applicationLayer: ApplicationLayer
) {
    private val services = mutableMapOf<String, Service>()

    /**
     * The expected length of the parameters for a service constructor in this layer
     */
    val expectedParamsLength: Int
        // add 2 because root layer has depth 0 and CoroutineScope is the first parameter
        get() = applicationLayer.depth + 2

    /**
     * Instantiate a service of type [TService] in this layer
     *
     * @return The instantiated service
     *
     * @throws KraftException If the service layer has no parent
     *
     * @see getServiceConstructor
     */
    inline fun <reified TService : Service> instantiateService(): TService =
        getServiceConstructor<TService>()
            .call(
                applicationLayer.coroutineScope,
                *applicationLayer.layers.toRoot.reversed().toTypedArray()
            )

    /**
     * Get the correct constructor for a service of type [TService] in this layer
     *
     * @return The constructor for the service
     * @throws KraftException If the service layer has no parent
     */
    inline fun <reified TService : Service> getServiceConstructor(): KFunction<TService> =
        TService::class.constructors.firstOrNull {
            if (it.parameters.size != expectedParamsLength) return@firstOrNull false
            if (it.parameters[0].type.classifier != CoroutineScope::class) return@firstOrNull false
            argsMatchParams(
                applicationLayer.layers.toRoot.reversed().toTypedArray(),
                it.parameters.drop(1).toTypedArray()
            )
        } ?: throw ServiceContainerException(this, "Service ${TService::class.simpleName} has no suitable constructor")


    /**
     * Get a service by its [key][serviceKey]
     *
     * @return The service with the given key, or null if it doesn't exist
     */
    fun get(serviceKey: String): Service? = services[serviceKey]

    /**
     * Get a service by type or create and register one if it hasn't been
     * instantiated.
     *
     * @return The service of type [TService]
     * @throws KraftException If the service layer stack has no root or a parent is missing before the root layer
     */
    inline fun <reified TService : Service> get(): TService {
        val annotation = ServiceMetadata.resolveAnnotation(TService::class, this)

        return get(annotation.key) as? TService
            ?: instantiateService<TService>().also { put(it) }
    }

    /**
     * Register a [Service] in this layer. You can call this method with a
     * service that has already been registered. It should not have any effect.
     */
    inline fun <reified TService : Service> register() {
        put(instantiateService<TService>())
    }

    /**
     * Ensure a service is tracked by the container. This is useful for services
     * that are instantiated outside the container.
     *
     * @param service The service to track
     */
    fun put(service: Service) {
        val annotation = ServiceMetadata.resolveAnnotation(service.javaClass.kotlin, this)
        if (services.containsKey(annotation.key)) return
        services[annotation.key] = service
    }

    inline operator fun <reified TService : Service> getValue(thisRef: Nothing?, prop: KProperty<*>): TService = get()

    fun each(block: (Service) -> Unit) {
        services.values.forEach(block)
    }
}