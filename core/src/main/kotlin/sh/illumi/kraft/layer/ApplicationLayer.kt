package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sh.illumi.kraft.KraftException
import sh.illumi.kraft.service.Service
import sh.illumi.kraft.service.ServiceContainer
import kotlin.reflect.KClass

/**
 * Each layer in an application represents a different scope of services,
 * resources, and logic. Layers can be nested, and can optionally expose their
 * resources to child layers programmatically. [ApplicationLayer] is the base
 * class for all layers in the application. [RootLayer] is the root layer in the
 * runtime, having no parent and zero or more child layers. In this way,
 * applications can derive layers based off of necessary context and
 * functionality, while also keeping outer context and [services] in scope.
 *
 * todo: provide an example here
 *
 * @property coroutineScope The coroutine scope for the layer
 * @property services The services for the layer
 */
interface ApplicationLayer {
    val coroutineScope: CoroutineScope
    val isRoot: Boolean get() = this is RootLayer
    val services: ServiceContainer get() = ServiceContainer(this)

    /**
     * Get the class for a given [index] in the layer tree
     *
     * @param index The index of the layer in the layer tree
     * @return The class of the layer at the given index
     * @throws KraftException If the layer has no parent, and we are not at the root layer.
     */
    fun getClassForIndex(index: Int): KClass<out ApplicationLayer> {
        val layers = getLayersToRoot().reversed()
        return layers[index].javaClass.kotlin
    }

    /**
     * Get all the layers from this layer to the root layer
     *
     * @return A list of layers from the root to this layer
     * @throws KraftException If the root layer is not present
     *
     * @see LayerWithParent
     */
    fun getLayersToRoot(): List<ApplicationLayer> {
        val layers = mutableListOf<ApplicationLayer>()
        var currentLayer: ApplicationLayer = this

        while (currentLayer is LayerWithParent<*> || currentLayer.isRoot) {
            layers += currentLayer

            if (currentLayer is LayerWithParent<*> && !currentLayer.isRoot)
                currentLayer = currentLayer.parentLayer
            else break
        }

        return layers
    }

    fun start() {
        services.each { it.onStart() }
    }

    val log: Logger get() = LoggerFactory.getLogger(this::class.java)
}

/**
 * Get all the layers from this layer to a layer of a specific type.
 *
 * @return A list of layers from this layer to the target layer
 *
 * @see LayerWithParent
 */
inline fun <reified TTargetLayer : ApplicationLayer> ApplicationLayer.getLayersToTyped(): List<ApplicationLayer> {
    val layers = mutableListOf<ApplicationLayer>()
    var currentLayer: ApplicationLayer = this

    while (currentLayer !is TTargetLayer && (currentLayer is LayerWithParent<*> || currentLayer.isRoot)) {
        layers += currentLayer

        if (currentLayer is LayerWithParent<*>) currentLayer = currentLayer.parentLayer
        else break
    }

    if (currentLayer !is TTargetLayer) {
        throw KraftException("${this.javaClass.kotlin.simpleName} has no parent of type ${TTargetLayer::class.simpleName}")
    }

    return layers
}

inline fun <reified TService : Service> ApplicationLayer.service() = services.get<TService>()