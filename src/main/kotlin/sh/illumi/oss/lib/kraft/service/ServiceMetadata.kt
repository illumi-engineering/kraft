package sh.illumi.oss.lib.kraft.service

import sh.illumi.oss.lib.kraft.ApplicationLayer
import sh.illumi.oss.lib.kraft.ServiceMissingMetadataException
import kotlin.reflect.KClass

/**
 * Metadata annotation for services
 *
 * @param key The key to use for identifying the service
 * @param scopes The scopes that this service is available in
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceMetadata(
    val key: String,
    val scopes: IntArray = [ApplicationLayer.ROOT_DEPTH],
) {
    companion object {
        /**
         * Resolve the [ServiceMetadata] annotation for a service class
         *
         * @param serviceClass The service class to resolve the annotation for
         * @param serviceContainer The service container that is attempting to resolve the service
         *
         * @return The resolved annotation
         *
         * @throws ServiceMissingMetadataException If the service class has no suitable annotation
         */
        fun resolveAnnotation(serviceClass: KClass<out Service<*>>, serviceContainer: ServiceContainer) =
            serviceClass.annotations.first {
                it is ServiceMetadata && it.scopes.contains(serviceContainer.applicationLayer.handle)
            } as? ServiceMetadata ?: throw ServiceMissingMetadataException(serviceClass, serviceContainer)
    }
}