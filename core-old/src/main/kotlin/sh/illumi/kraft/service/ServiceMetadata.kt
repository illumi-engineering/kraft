package sh.illumi.kraft.service

import sh.illumi.kraft.KraftException
import sh.illumi.kraft.layer.ApplicationLayer
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * Metadata annotation for services
 *
 * @param key The key to use for identifying the service
 * @param layers The layers that this service is available in
 * @param dependencies The services that this service depends on
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ServiceMetadata(
    val key: String,
    val layers: Array<KClass<out ApplicationLayer>>,
    val dependencies: Array<KClass<out Service>> = [],
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
        fun resolveAnnotation(
            serviceClass: KClass<out Service>,
            serviceContainer: ServiceContainer,
        ) = serviceClass.annotations.filterIsInstance<ServiceMetadata>().first { annotation ->

            val layerClass = annotation.layers[serviceContainer.applicationLayer.depth]

            return@first layerClass == serviceContainer.applicationLayer.javaClass.kotlin ||
                    serviceContainer.applicationLayer.javaClass.kotlin.allSuperclasses.contains(layerClass)
        } as? ServiceMetadata ?: throw KraftException("Service ${serviceClass.simpleName} has no suitable ServiceMetadata annotation")
    }
}