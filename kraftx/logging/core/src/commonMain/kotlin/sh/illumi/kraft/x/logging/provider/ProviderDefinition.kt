package sh.illumi.kraft.x.logging.provider

import sh.illumi.kraft.x.logging.LogLevel

class ProviderDefinition<TConfig : ProviderConfiguration>(
    val provider: LoggingProvider<TConfig>,
) {
    var config = provider.defaultConfig
    var levels = DEFAULT_LEVELS

    fun withLevel(logLevel: LogLevel) {
        levels = LogLevel.allFrom(logLevel)
    }

    fun allowLevels(vararg logLevels: LogLevel) {
        levels = logLevels.toSet()
    }

    fun configure(block: TConfig.() -> Unit) {
        config = config.apply(block)
    }

    fun configureProvider() {
        provider.configure(config)
    }

    companion object {
        val DEFAULT_LEVELS = setOf(
            LogLevel.Info,
            LogLevel.Warning,
            LogLevel.Error,
            LogLevel.Fatal
        )

        val DEFAULT_PROVIDER = LoggingProvider.Companion.NoOp

        val Default = ProviderDefinition(DEFAULT_PROVIDER)
    }
}