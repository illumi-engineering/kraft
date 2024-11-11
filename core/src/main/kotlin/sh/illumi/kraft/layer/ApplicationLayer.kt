package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sh.illumi.kraft.service.ServiceContainer

/**
 * Each layer in an application represents a different scope of services,
 * resources, and logic. Layers can be nested, and can optionally expose their
 * resources to child layers programmatically. [ApplicationLayer] is the base
 * class for all layers in the application. In this way, applications can derive
 * layers based off of necessary context and functionality, while also keeping
 * outer context and [services] in scope.
 *
 * todo: provide an example here
 *
 * @property coroutineScope The coroutine scope for the layer
 * @property parent The parent layer of this layer
 * @property isRoot Whether this layer is the root layer
 * @property services The services for the layer
 * @property depth The depth of the layer in the layer tree
 */
interface ApplicationLayer {
    val coroutineScope: CoroutineScope
    val parent: ApplicationLayer? get() = null
    val isRoot: Boolean get() = parent == null
    val services: ServiceContainer get() = ServiceContainer(this)
    val layers: LayerNavigationUtils get() = LayerNavigationUtils(this)
    val depth get() = layers.toRoot.size - 1 // root layer has depth 0

    fun start() {
        services.each { it.onStart() }
    }

    fun shutdown() {
        services.each { it.onShutdown() }
    }

    val log: Logger get() = LoggerFactory.getLogger(this::class.java)
}
