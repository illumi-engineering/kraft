package sh.illumi.kraft.layer

import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.service.ServiceContainer

abstract class Layer : ServiceAccessor() {
    override val layer = this
    override val serviceContainer = ServiceContainer(this)
    
    var parentLayer: Layer? = null
        private set
    
    fun <TLayer : Layer, TReturn : Any> withChildLayer(
        createLayer: (CoroutineScope) -> TLayer,
        block: TLayer.() -> TReturn
    ): TReturn {
        val layer = createLayer(coroutineScope)
        layer.parentLayer = this
        return layer.block()
    }
}