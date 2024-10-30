package sh.illumi.kraft.service

import sh.illumi.kraft.KraftException
import sh.illumi.kraft.layer.ApplicationLayer
import kotlin.reflect.KClass

/**
 * Metadata annotation for services
 *
 * @param key The key to use for identifying the service
 * @param dependencies The services that this service depends on
 * @param layers The scopes that this service is available in
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceMetadata(
    val key: String,
    val dependencies: Array<KClass<out Service>> = [],
    val layers: IntArray = [ApplicationLayer.ROOT_HANDLE],
) {
    companion object {
        /**
         * Resolve the [ServiceMetadata] annotation for a service class
         *
         * @param serviceClass The service class to resolve the annotation for
         * @param serviceContainer The service container that is attempting to resolve the service
         *
         * @return The resolved annotation
         */
        fun resolveAnnotation(serviceClass: KClass<out Service>, serviceContainer: ServiceContainer) =
            serviceClass.annotations.first {
                it is ServiceMetadata && it.layers.contains(serviceContainer.applicationLayer.handle)
            } as? ServiceMetadata ?: throw KraftException("Service ${serviceClass.simpleName} has no suitable ServiceMetadata annotation")
    }
}