package sh.illumi.oss.lib.kraft.resource

import sh.illumi.oss.lib.kraft.ApplicationLayer
import java.util.ServiceLoader

class ResourceProviderFactory(val applicationLayer: ApplicationLayer<*>) {
    val serviceLoader = ServiceLoader.load(ResourceProvider::class.java)

    inline fun <
        reified TResource : Resource<TResource, TResourceProvider>,
        reified TResourceProvider : ResourceProvider<TResource>
    > getProvider(): TResourceProvider = serviceLoader
        .filterIsInstance<TResourceProvider>()
        .firstOrNull() ?: throw Exception("No provider found for ${TResource::class.simpleName}")
}