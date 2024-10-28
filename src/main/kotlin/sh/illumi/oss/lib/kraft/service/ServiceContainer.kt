package sh.illumi.oss.lib.kraft.service

import kotlinx.coroutines.CoroutineScope
import sh.illumi.oss.lib.kraft.*
import kotlin.reflect.KFunction

class ServiceContainer(
    val applicationLayer: ApplicationLayer<*>
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
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
     * @throws KraftException If the service layer has no parent
     *
     * @see getServiceConstructor
     * @see ApplicationLayer.getLayersToRoot
     */
    inline fun <reified TService : Service> instantiateService(): TService =
        getServiceConstructor<TService>()
            .call(
                applicationLayer.coroutineScope,
                *applicationLayer.getLayersToRoot().reversed().toTypedArray()
            )

    /**
     * Get the correct constructor for a service of type [TService] in this layer
     *
     * @return The constructor for the service
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
     * @throws KraftException If the service layer has no parent
     *
     * @see ApplicationLayer.getClassForIndex
     */
    inline fun <reified TService : Service> getServiceConstructor(): KFunction<TService> =
        TService::class.constructors.firstOrNull {
            if (it.parameters.size != expectedParamsLength) return@firstOrNull false

            for ((index, parameter) in it.parameters.withIndex()) {
                when (index) {
                    0 -> if (parameter.type.classifier != CoroutineScope::class) return@firstOrNull false
                    else -> if (parameter.type.classifier != applicationLayer.getClassForIndex(index - 1)) return@firstOrNull false
                }
            }

            true
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
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
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
     *
     * @throws ServiceMissingMetadataException If the service is missing the [ServiceMetadata] annotation
     */
    inline fun <reified TService : Service> register() {
        put(instantiateService<TService>())
    }

    /**
     * Ensure a service is tracked by the container. This is useful for services
     * that are instantiated outside the container.
     *
     * @param service The service to track
     * @throws ServiceMissingMetadataException If the service is missing the [ServiceMetadata] annotation
     */
    fun put(service: Service) {
        val annotation = ServiceMetadata.resolveAnnotation(service.javaClass.kotlin, this)
        if (services.containsKey(annotation.key)) return
        services[annotation.key] = service
    }

    inline operator fun <reified TService : Service> getValue(thisRef: Any?, prop: Any): TService = get()
}