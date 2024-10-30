package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.resource.ResourceProvider
import sh.illumi.kraft.service.ServiceContainer

/**
 * The root layer in the application layer stack
 *
 * @property depth The depth of this layer in the tree. Defaults to [ApplicationLayer.ROOT_DEPTH]
 */
abstract class RootLayer<TRootLayer : RootLayer<TRootLayer>>(
    override val coroutineScope: CoroutineScope,
) : ApplicationLayer<RootLayer<TRootLayer>> {
    override val depth: Int = ApplicationLayer.ROOT_DEPTH

    @Suppress("LeakingThis")
    override val services = ServiceContainer(this)
    override val resourceProviders = mutableMapOf<String, ResourceProvider<*>>()

    abstract suspend fun start()
}