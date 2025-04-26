package sh.illumi.kraft.x.logging.service

import sh.illumi.kraft.service.Service
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.service.ServiceFactory
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.provider.LoggingProvider
import sh.illumi.kraft.x.logging.provider.ProviderConfiguration
import sh.illumi.kraft.x.logging.provider.ProviderDefinition

class LoggingService(
    accessor: ServiceAccessor,
    private val providerDefinitions: Collection<ProviderDefinition<*>>,
) : Service(accessor) {
    override val displayName = "LoggingService[${accessor::class.simpleName}]"

    init {
        for (definition in providerDefinitions)
            definition.configureProvider()
    }

    override fun onStart() {
        for (definition in providerDefinitions)
            definition.provider.onInitialize()
    }
    
    fun log(level: LogLevel, message: String) {
        for (definition in providerDefinitions)
            if (level in definition.levels)
                definition.provider.log(level, accessor, message)
    }
    
    fun trace(message: String) {
        log(LogLevel.Trace, message)
    }
    
    fun debug(message: String) {
        log(LogLevel.Debug, message)
    }
    
    fun info(message: String) {
        log(LogLevel.Info, message)
    }
    
    fun warning(message: String) {
        log(LogLevel.Warning, message)
    }
    
    fun error(message: String) {
        log(LogLevel.Error, message)
    }
    
    fun fatal(message: String) {
        log(LogLevel.Fatal, message)
    }
    
    companion object Factory : ServiceFactory.Owned<LoggingService, Configuration>(LoggingService::class) {
        override fun construct(
            accessor: ServiceAccessor,
            configure: Configuration.() -> Unit
        ): LoggingService {
            val cfg = Configuration().apply(configure)
            return LoggingService(accessor, cfg.providerDefinitions)
        }
    }
    
    class Configuration {
        val providerDefinitions = mutableSetOf<ProviderDefinition<*>>()
        
        fun <TConfig : ProviderConfiguration> withProvider(provider: LoggingProvider<TConfig>, configure: ProviderDefinition<TConfig>.() -> Unit) {
            providerDefinitions += ProviderDefinition(provider).apply(configure)
        }
    }
}
