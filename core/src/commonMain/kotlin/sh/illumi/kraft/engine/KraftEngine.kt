package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sh.illumi.kraft.layer.Layer

class KraftEngine {
    private val rootDefinitions = mutableListOf<RootDefinition<*>>()
    
    fun <TLayer : Layer> addForegroundRoot(produceLayer: (CoroutineScope) -> TLayer, setup: TLayer.() -> Unit) {
        rootDefinitions += RootDefinition(produceLayer, Dispatchers.Main, setup) 
    }
    
    fun <TLayer : Layer> addBackgroundRoot(produceLayer: (CoroutineScope) -> TLayer, setup: TLayer.() -> Unit) {
        rootDefinitions += RootDefinition(produceLayer, Dispatchers.Default, setup) 
    }
    
    fun start() {
        rootDefinitions.forEach { it.start() }
    }
}