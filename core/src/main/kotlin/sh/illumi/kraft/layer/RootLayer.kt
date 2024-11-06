package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.service.ServiceContainer

/**
 * The root layer in the application layer stack
 *
 * @property depth The depth of this layer in the tree. Defaults to [ApplicationLayer.ROOT_DEPTH]
 */
abstract class RootLayer(
    override val coroutineScope: CoroutineScope,
) : ApplicationLayer {
    override val depth: Int = ApplicationLayer.ROOT_DEPTH

    @Suppress("LeakingThis")
    override val services = ServiceContainer(this)

    abstract suspend fun start()
}