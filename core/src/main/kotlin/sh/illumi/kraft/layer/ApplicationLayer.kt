package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sh.illumi.kraft.KraftException
import sh.illumi.kraft.resource.ResourceProvider
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
 * @param TLayer The type of the layer
 *
 * @property coroutineScope The coroutine scope for the layer
 * @property depth The depth of the layer in the layer tree
 * @property handle An identifying handle for the layer
 * @property resourceProviders The resource providers for the layer
 * @property services The services for the layer
 */
interface ApplicationLayer<TLayer : ApplicationLayer<TLayer>> {
    val coroutineScope: CoroutineScope
    val depth: Int
    val handle: Int // todo: come up with a better system for identifying layers

    val resourceProviders: MutableMap<String, ResourceProvider<*>>
    val services: ServiceContainer

    /**
     * Get the class for a given [index] in the layer tree
     *
     * @param index The index of the layer in the layer tree
     * @return The class of the layer at the given index
     * @throws KraftException If the layer has no parent, and we are not at the root layer.
     */
    fun getClassForIndex(index: Int): KClass<out ApplicationLayer<*>> {
        val layers = getLayersToRoot().reversed()
//        println(layers.map { it.javaClass.kotlin.simpleName })
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
    fun getLayersToRoot(): List<ApplicationLayer<*>> {
        val layers = mutableListOf<ApplicationLayer<*>>()
        var currentLayer: ApplicationLayer<*> = this

        while (currentLayer is LayerWithParent<*> || currentLayer is RootLayer<*>) {
            layers += currentLayer

            if (currentLayer is LayerWithParent<*>) currentLayer = currentLayer.parentLayer
            else break
        }

        return layers
    }

    val log: Logger get() = LoggerFactory.getLogger(this::class.java)

    companion object {
        /**
         * The depth of the root layer
         */
        const val ROOT_DEPTH = 0

        /**
         * The handle for the root layer
         */
        const val ROOT_HANDLE = 0

        /**
         * The maximum depth of a layer
         * todo: allow this to be set by the caller so that it can be adjusted based on the application's needs
         */
        private const val MAX_DEPTH = 16 // a depth of 16 layers should be more than enough for any practical use case

        /**
         * The maximum number of handles, and consequently layers that may be created
         * todo: allow this to be set by the caller so that it can be adjusted based on the application's needs
         */
        private const val MAX_HANDLES: Int = Int.MAX_VALUE

        private var nextHandle: Int = ROOT_HANDLE + 1

        /**
         * Get the next handle for a layer
         *
         * @return The next handle in the auto-incrementing sequence
         *
         * @throws KraftException If the number of handles exceeds [MAX_HANDLES]
         */
        fun nextHandle(): Int {
            if (nextHandle - 1 == MAX_HANDLES) {
                throw KraftException("ApplicationLayer handles exhausted")
            }

            val handle = nextHandle
            nextHandle++

            return handle
        }
    }
}

/**
 * Get all the layers from this layer to a layer of a specific type.
 *
 * @return A list of layers from this layer to the target layer
 *
 * @see LayerWithParent
 */
inline fun <TLayer : ApplicationLayer<TLayer>, reified TTargetLayer : ApplicationLayer<*>> ApplicationLayer<TLayer>.getLayersToTyped(): List<ApplicationLayer<*>> {
    val layers = mutableListOf<ApplicationLayer<*>>()
    var currentLayer: ApplicationLayer<*> = this

    while (currentLayer !is TTargetLayer && (currentLayer is LayerWithParent<*> || currentLayer is RootLayer<*>)) {
        layers += currentLayer

        if (currentLayer is LayerWithParent<*>) currentLayer = currentLayer.parentLayer
        else break
    }

    if (currentLayer !is TTargetLayer) {
        throw KraftException("${this.javaClass.kotlin.simpleName}[handle=$handle,depth=$depth] has no parent of type ${TTargetLayer::class.simpleName}")
    }

    return layers
}

inline fun <TLayer : ApplicationLayer<TLayer>, reified TService : Service> ApplicationLayer<TLayer>.service() = services.get<TService>()