package sh.illumi.oss.lib.kraft

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

import kotlinx.coroutines.CoroutineScope

abstract class ApplicationLayer<TLayer : ApplicationLayer<TLayer>> {
    abstract val coroutineScope: CoroutineScope
    abstract val depth: Int
    abstract val handle: Int

    private val services = mutableMapOf<String, Service<*>>()

    val expectedParamsLength: Int
        get() = depth + 2

    /**
     * Instantiate a service of type [TService] in this layer
     *
     * @return The instantiated service
     *
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
     * @throws KraftException If the service layer has no parent
     *
     * @see getConstructor
     * @see getLayerStack
     */
    inline fun <reified TService : Service<*>> instantiateService(): TService =
        getConstructor<TService>()
            .call(
                coroutineScope,
                *getLayerStack().toTypedArray()
            )

    /**
     * Get the correct constructor for a service of type [TService] in this layer
     *
     * @return The constructor for the service
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
     * @throws KraftException If the service layer has no parent
     *
     * @see getClassForIndex
     */
    inline fun <reified TService : Service<*>> getConstructor(): KFunction<TService> =
        TService::class.constructors.firstOrNull {
            if (it.parameters.size != expectedParamsLength) return@firstOrNull false

            for ((index, parameter) in it.parameters.withIndex()) {
                when (index) {
                    0 -> if (parameter.type.classifier != CoroutineScope::class) return@firstOrNull false
                    else -> if (parameter.type.classifier != getClassForIndex(index)) return@firstOrNull false
                }
            }

            true
        } ?: throw ServiceHasNoSuitableConstructorException(this)

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
        else throw KraftException("ServiceLayer[handle=$handle,depth=$depth] does not have a parent") // todo: better error reporting & handling

    /**
     * Get the layer stack for this layer
     *
     * @return A list of layers from the root to this layer
     * @throws KraftException If the root layer is not present
     *
     * @see LayerWithParent
     */
    fun getLayerStack(): List<ApplicationLayer<*>> {
        val scopeStack = mutableListOf<ApplicationLayer<*>>()
        var currentScope: ApplicationLayer<*> = this

        while (currentScope is LayerWithParent<*>) {
            scopeStack += currentScope
            currentScope = currentScope.parentLayer
        }

        // todo: maybe there's a better way to do this?
        if (currentScope is RootLayer<*>) scopeStack += currentScope
        else throw KraftException("ServiceScope does not have a root layer!")

        // the root layer should be at the start of the list
        return scopeStack.reversed()
    }

    /**
     * Register a [service] in this layer. You can call this method with a
     * service that has already been registered. It should not have any effect.
     *
     * @param service The service to register
     */
    fun registerService(service: Service<*>) {
        val annotation = ServiceMetadata.resolveAnnotation(service.javaClass.kotlin, this)
        if (services.containsKey(annotation.key)) return
        services[annotation.key] = service
    }

    /**
     * Get a service by its [key][serviceKey]
     *
     * @return The service with the given key, or null if it doesn't exist
     */
    fun getService(serviceKey: String): Service<*>? = services[serviceKey]

    /**
     * Get a service by type or create and register one if it hasn't been
     * instantiated.
     *
     * @return The service of type [TService]
     * @throws ServiceHasNoSuitableConstructorException If the service has no suitable constructor
     * @throws KraftException If the service layer stack has no root or a parent is missing before the root layer
     */
    inline fun <reified TService : Service<*>> service(): TService {
        val annotation = ServiceMetadata.resolveAnnotation(TService::class, this)

        return getService(annotation.key) as? TService
            ?: instantiateService<TService>().also { registerService(it) }
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
                throw KraftException("ServiceScope handles exhausted")
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
 * @property coroutineScope The coroutine scope for this layer
 * @property depth The depth of this layer in the tree. Defaults to [ApplicationLayer.ROOT_DEPTH]
 * @property handle The handle for the root layer. Defaults to [ApplicationLayer.ROOT_HANDLE]
 */
abstract class RootLayer<TChildLayer : ApplicationLayer<TChildLayer>>(
    override val coroutineScope: CoroutineScope
) : ApplicationLayer<RootLayer<TChildLayer>>(),
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
 * @property coroutineScope The coroutine scope for this layer
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
    override val coroutineScope: CoroutineScope
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
 * @property coroutineScope The coroutine scope for this layer
 * @property depth The depth of this layer in the tree
 * @property handle The handle for this layer
 */
abstract class LeafLayer<TParentLayer : ApplicationLayer<TParentLayer>>(
    final override val parentLayer: TParentLayer,
    override val coroutineScope: CoroutineScope
) : ApplicationLayer<LeafLayer<TParentLayer>>(),
    LayerWithParent<TParentLayer>
{
    override val depth: Int = parentLayer.depth + 1
    override val handle: Int = nextHandle()
}