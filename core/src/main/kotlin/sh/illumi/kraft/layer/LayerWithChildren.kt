package sh.illumi.kraft.layer

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import sh.illumi.kraft.KraftException

/**
 * A service layer with children
 *
 * @property childLayers The background child layers
 */
interface LayerWithChildren {
    val childLayers: MutableList<ApplicationLayer>
}

/**
 * Get a child layer of a specific type
 *
 * @param TChildLayer The type of the child layer
 * @return The child layer of the specified type
 *
 * @throws KraftException If no child layer of the specified type is found
 * todo: better exception
 */
inline fun <
    reified TChildLayer : ApplicationLayer
> LayerWithChildren.getChildLayer() = childLayers
    .filterIsInstance<TChildLayer>()
    .firstOrNull() ?: throw KraftException("No child layer of type ${TChildLayer::class.simpleName} found")

/**
 * Run the given [block] on a child layer of a specific type [TChildLayer]
 *
 * @param TChildLayer The type of the child layer
 * @param TReturn The return type of the block
 * @param block The block to run on the child layer
 *
 * @return The result of the block
 *
 * @throws KraftException If no child layer of the specified type is found
 */
inline fun <reified TChildLayer, TReturn> LayerWithChildren.withChildLayerReturning(
    noinline block: suspend TChildLayer.() -> TReturn,
) where TChildLayer : ApplicationLayer = getChildLayer<TChildLayer>().let { child ->
    child.coroutineScope.async { child.block() }
}


inline fun <reified TChildLayer> LayerWithChildren.withChildLayer(
    noinline block: suspend TChildLayer.() -> Unit,
) where TChildLayer : ApplicationLayer = getChildLayer<TChildLayer>().let { child ->
    child.coroutineScope.launch { child.block() }
}