package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass

annotation class ServiceMetadata(
    val key: String,
    val scopes: IntArray = [ServiceScope.ROOT_DEPTH],
) {
    companion object {
        fun <TScope : ServiceScope<*>> resolveAnnotation(serviceKlass: KClass<out Service<*>>, scope: TScope) =
            serviceKlass.annotations.first {
                it is ServiceMetadata && it.scopes.contains(scope.depth)
            } as? ServiceMetadata ?: throw ServiceMissingRegisterAnnotationException(serviceKlass, scope)
    }
}
