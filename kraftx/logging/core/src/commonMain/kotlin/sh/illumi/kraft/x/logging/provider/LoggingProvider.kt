package sh.illumi.kraft.x.logging.provider

import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.x.logging.LogLevel

interface LoggingProvider<TConfig : ProviderConfiguration> {
    val defaultConfig: TConfig

    fun onInitialize() {
        // No-op
    }

    fun configure(config: TConfig) {
        // No-op
    }

    /**
     * Log a message with the given [level] and [accessor].
     *
     * @param level The log level.
     * @param accessor The service accessor.
     * @param message The message to log.
     */
    fun log(level: LogLevel, accessor: ServiceAccessor, message: String)

    companion object {
        object StdOut : LoggingProvider<ProviderConfiguration.Empty> {
            override val defaultConfig = ProviderConfiguration.Empty
            override fun log(level: LogLevel, accessor: ServiceAccessor, message: String) {
                println("[${level.display}] ${accessor.label}: $message")
            }
        }

        object NoOp : LoggingProvider<ProviderConfiguration.Empty> {
            override val defaultConfig = ProviderConfiguration.Empty
            override fun log(level: LogLevel, accessor: ServiceAccessor, message: String) {}
        }
    }
}