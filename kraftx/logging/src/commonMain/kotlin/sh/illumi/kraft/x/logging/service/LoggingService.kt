package sh.illumi.kraft.x.logging.service

import sh.illumi.kraft.service.Service
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.service.ServiceFactory
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.provider.LoggingProvider

class LoggingService(
    accessor: ServiceAccessor,
    private val providerConfigs: Collection<Configuration.ProviderConfiguration>,
) : Service(accessor) {
    override val displayName = "LoggingService[${accessor::class.simpleName}]"
    
    fun log(level: LogLevel, message: String) {
        providerConfigs.forEach {
            if (level in it.levels) it.provider.log(level, accessor, message)
        }
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
            return LoggingService(accessor, cfg.providerConfigs)
        }
    }
    
    class Configuration {
        val providerConfigs = mutableSetOf<ProviderConfiguration>()
        
        fun withProvider(provider: LoggingProvider, configure: ProviderConfiguration.() -> Unit) {
            providerConfigs += ProviderConfiguration(provider).apply(configure)
        }
        
        class ProviderConfiguration(
            val provider: LoggingProvider
        ) {
            var levels = setOf<LogLevel>()
            
            fun withLevel(logLevel: LogLevel) {
                levels = LogLevel.allFrom(logLevel)
            }
            
            fun allowLevels(vararg logLevels: LogLevel) {
                levels = logLevels.toSet()
            }
        }
    }
}