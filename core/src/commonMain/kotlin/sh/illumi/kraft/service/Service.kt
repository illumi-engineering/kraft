package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer

abstract class Service(
    protected val accessor: ServiceAccessor
) : ServiceAccessor() {
    open val displayName: String? = null
    override val label: String
        get() = displayName ?: super.label
    
    override val layer: Layer
        get() = accessor.layer
    override val serviceContainer = accessor.serviceContainer
    override val coroutineScope = accessor.coroutineScope
    
    open fun onStart() {}
    open fun onShutdown() {}
}
