package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass

import kotlinx.coroutines.CoroutineScope

/**
 * Base class for services
 *
 * @param TConfig The configuration type for the service
 *
 * @property coroutineScope The coroutine scope to use for the service
 */
abstract class Service<TConfig>(
    private val coroutineScope : CoroutineScope
) {
    abstract fun getConfig(): TConfig
}

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
         * @param scope The scope to resolve the annotation for
         *
         * @return The resolved annotation
         *
         * @throws ServiceMissingMetadataException If the service class has no suitable annotation
         */
        fun <TScope : ApplicationLayer<*>> resolveAnnotation(serviceClass: KClass<out Service<*>>, scope: TScope) =
            serviceClass.annotations.first {
                it is ServiceMetadata && it.scopes.contains(scope.depth)
            } as? ServiceMetadata ?: throw ServiceMissingMetadataException(serviceClass, scope)
    }
}
