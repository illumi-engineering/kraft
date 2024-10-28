package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass

import kotlinx.coroutines.CoroutineScope
import sh.illumi.oss.lib.kraft.service.ServiceContainer

/**
 * Each layer in an application represents a different scope of services,
 * resources, and logic. Layers can be nested, with each layer having a parent
 * layer and zero or more child layers. [ApplicationLayer] is the base class for
 * all layers in the application. [RootLayer] is the root layer in the runtime,
 * having no parent and zero or more child layers. [MidLayer] is a layer with a
 * parent and zero or more child layers. [LeafLayer] is a layer with a parent
 * and no child layers. In this way, applications can derive layers based off of
 * necessary context and functionality, while also keeping outer context and
 * [services] in scope.
 *
 * todo: provide an example here
 *
 * @param TLayer The type of the layer
 *
 * @property depth The depth of the layer in the layer tree
 * @property handle An identifying handle for the layer
 *
 */
abstract class ApplicationLayer<TLayer : ApplicationLayer<TLayer>> : CoroutineScope {
    abstract val depth: Int
    abstract val handle: Int // todo: come up with a better system for identifying layers

    private val resourceProviders = mutableMapOf<String, ResourceProvider<*>>()

    @Suppress("LeakingThis")
    private val services = ServiceContainer(this)
    /**
     * Get the class for a given [index] in the layer tree
     *
     * @param index The index of the layer in the layer tree
     * @return The class of the layer at the given index
     * @throws KraftException If the layer has no parent, and we are not at the root layer.
      */
    fun getClassForIndex(index: Int): KClass<out ApplicationLayer<*>> =
        if (index == 0) this.javaClass.kotlin
        else if (this is LayerWithParent<*>) this.parentLayer.getClassForIndex(index - 1)
        // todo: better error handling
        else throw KraftException("${this.javaClass.kotlin.simpleName}[handle=$handle,depth=$depth] does not have a parent")

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

    /**
     * Get all the layers from this layer to a layer of a specific type.
     *
     * @return A list of layers from this layer to the target layer
     *
     * @see LayerWithParent
     */
    inline fun <reified TTargetLayer : ApplicationLayer<*>> getLayersToTyped(): List<ApplicationLayer<*>> {
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

    companion object {
        const val ROOT_DEPTH = 0
        const val ROOT_HANDLE = 0
        private const val MAX_DEPTH = 16 // a depth of 16 layers should be more than enough for any practical use case
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
 * A service layer with a parent
 *
 * @param TParentLayer The type of the parent layer
 * @property parentLayer The parent layer
 */
interface LayerWithParent<TParentLayer : ApplicationLayer<TParentLayer>> {
    val parentLayer: TParentLayer
}

/**
 * A service layer with children
 *
 * @param TChildLayer The type of the child layer
 *
 * @property activeChildLayer The currently active child layer
 * @property backgroundChildLayers The background child layers
 */
interface LayerWithChildren<TChildLayer : ApplicationLayer<TChildLayer>> {
    var activeChildLayer: TChildLayer
    val backgroundChildLayers: MutableList<TChildLayer>
}

/**
 * The root layer in the service layer tree
 *
 * @param TChildLayer The type of the child layer
 *
 * @property depth The depth of this layer in the tree. Defaults to [ApplicationLayer.ROOT_DEPTH]
 * @property handle The handle for the root layer. Defaults to [ApplicationLayer.ROOT_HANDLE]
 */
abstract class RootLayer<TChildLayer : ApplicationLayer<TChildLayer>> :
    ApplicationLayer<RootLayer<TChildLayer>>(),
    LayerWithChildren<TChildLayer>
{
    override val depth: Int = ROOT_DEPTH
    override val handle: Int = ROOT_HANDLE
}

/**
 * A mid-layer in the service layer tree
 *
 * @param TParentLayer The type of the parent layer
 * @param TChildLayer The type of the child layer
 *
 * @property parentLayer The parent layer
 * @property depth The depth of this layer in the tree
 * @property handle The handle for this layer
 * @property activeChildLayer The currently active child layer
 * @property backgroundChildLayers The background child layers
 */
abstract class MidLayer<
    TParentLayer : ApplicationLayer<TParentLayer>,
    TChildLayer : ApplicationLayer<TChildLayer>
> (
    final override val parentLayer: TParentLayer,
) : ApplicationLayer<MidLayer<TParentLayer, TChildLayer>>(),
    LayerWithParent<TParentLayer>,
    LayerWithChildren<TChildLayer>
{
    override val depth: Int = parentLayer.depth + 1
    override val handle: Int = nextHandle()
}

/**
 * A leaf layer in the service layer tree
 *
 * @param TParentLayer The type of the parent layer
 *
 * @property parentLayer The parent of this leaf layer
 * @property depth The depth of this layer in the tree
 * @property handle The handle for this layer
 */
abstract class LeafLayer<TParentLayer : ApplicationLayer<TParentLayer>>(
    final override val parentLayer: TParentLayer,
) : ApplicationLayer<LeafLayer<TParentLayer>>(),
    LayerWithParent<TParentLayer>
{
    override val depth: Int = parentLayer.depth + 1
    override val handle: Int = nextHandle()
}