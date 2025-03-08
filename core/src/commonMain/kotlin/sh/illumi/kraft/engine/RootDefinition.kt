package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import sh.illumi.kraft.layer.Layer
import kotlin.coroutines.CoroutineContext

class RootDefinition<TLayer : Layer>(
    val produceLayer: (CoroutineScope) -> TLayer,
    val dispatcher: CoroutineDispatcher,
    val setup: TLayer.() -> Unit
) {
    fun start() {
        CoroutineScope(dispatcher).launch {
            val layer = produceLayer(this)
            layer.apply(setup)
        } 
    }
}