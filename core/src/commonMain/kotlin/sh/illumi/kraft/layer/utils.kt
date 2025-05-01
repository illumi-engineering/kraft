package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope

fun <TLayer : Layer> CoroutineScope.launchLayer(produceLayer: (CoroutineScope) -> TLayer): TLayer {
    val layer = produceLayer(this)
    return layer
}