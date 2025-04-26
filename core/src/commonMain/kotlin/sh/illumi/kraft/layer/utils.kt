package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope

fun <TLayer : Layer> CoroutineScope.launchLayer(produceLayer: (CoroutineScope) -> TLayer): Layer {
    val layer = produceLayer(this)
    return layer
}