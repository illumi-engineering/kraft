package sh.illumi.kraft.service

import sh.illumi.kraft.layer.Layer

abstract class Service(
    protected val accessor: ServiceAccessor
) : ServiceAccessor() {
    abstract val displayName: String?
    override val label: String
        get() = displayName ?: super.label
    
    override val applicableLayer: Layer
        get() = accessor.applicableLayer
    override val serviceContainer = accessor.serviceContainer
    
    open fun onStart() {}
    open fun onShutdown() {}
}
