package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sh.illumi.kraft.layer.Layer

class KraftEngine {
    private val rootDefinitions = mutableListOf<RootDefinition>()
    
    fun addForegroundRoot(produceLayer: (CoroutineScope) -> Layer, setup: Layer.() -> Unit) {
        rootDefinitions += RootDefinition(produceLayer, Dispatchers.Main, setup) 
    }
    
    fun start() {
        rootDefinitions.forEach { definition ->
            CoroutineScope(definition.dispatcher).launch {
                val layer = definition.produceLayer(this)
                layer.apply(definition.setup)
            }
        }
    }
}