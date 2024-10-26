package sh.illumi.oss.lib.kraft

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
    val scopes: IntArray = [ServiceLayer.ROOT_DEPTH],
) {
    companion object {
        /**
         * Resolve the [ServiceMetadata] annotation for a service class
         *
         * @param serviceClass The service class to resolve the annotation for
         * @param scope The scope to resolve the annotation for
         *
         * @return The resolved annotation
         *
         * @throws ServiceMissingMetadataException If the service class has no suitable annotation
         */
        fun <TScope : ServiceLayer<*>> resolveAnnotation(serviceClass: KClass<out Service<*>>, scope: TScope) =
            serviceClass.annotations.first {
                it is ServiceMetadata && it.scopes.contains(scope.depth)
            } as? ServiceMetadata ?: throw ServiceMissingMetadataException(serviceClass, scope)
    }
}
