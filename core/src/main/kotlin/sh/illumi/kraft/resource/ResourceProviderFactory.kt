package sh.illumi.kraft.resource

import sh.illumi.kraft.layer.ApplicationLayer
import java.util.*

class ResourceProviderFactory(val applicationLayer: ApplicationLayer<*>) {
    val serviceLoader = ServiceLoader.load(ResourceProvider::class.java)

    inline fun <
        reified TResource : Resource<TResource, TResourceProvider>,
        reified TResourceProvider : ResourceProvider<TResource>
    > getProvider(): TResourceProvider = serviceLoader
        .filterIsInstance<TResourceProvider>()
        .firstOrNull() ?: throw Exception("No provider found for ${TResource::class.simpleName}")
}