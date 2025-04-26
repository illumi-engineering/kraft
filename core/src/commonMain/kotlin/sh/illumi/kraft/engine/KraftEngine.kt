package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import sh.illumi.kraft.layer.Layer

class KraftEngine {
    private val rootDefinitions = mutableListOf<RootDefinition<*>>()
    
    fun <TLayer : Layer> addForegroundRoot(produceLayer: (CoroutineScope) -> TLayer, setup: TLayer.() -> Unit) {
        rootDefinitions += RootDefinition(produceLayer(CoroutineScope(Dispatchers.Main)), Dispatchers.Main, setup) 
    }
    
    fun <TLayer : Layer> addBackgroundRoot(produceLayer: (CoroutineScope) -> TLayer, setup: TLayer.() -> Unit) {
        rootDefinitions += RootDefinition(produceLayer(CoroutineScope(Dispatchers.Default)), Dispatchers.Default, setup)
    }
    
//    fun <TLayer : Layer> addCustomRoot(produceLayer: (CoroutineScope) -> TLayer, dispatcher: CoroutineDispatcher, setup: TLayer.() -> Unit) {
//        rootDefinitions += RootDefinition(produceLayer(CoroutineScope(dispatcher)), dispatcher, setup)
//    }
    
    fun start() {
        rootDefinitions.forEach { it.start() }
    }
}
