package sh.illumi.kraft.x.logging.prettylog

import cz.lukynka.prettylog.LoggerSettings
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.provider.LoggingProvider

object PrettyLog : LoggingProvider<PrettyLogConfiguration> {
    override val defaultConfig: PrettyLogConfiguration = PrettyLogConfiguration()

    private lateinit var configuration: PrettyLogConfiguration
    override fun configure(config: PrettyLogConfiguration) {
        configuration = config
    }

    override fun onInitialize() {
        if (!::configuration.isInitialized) {
            configuration = defaultConfig

            // todo: log warning
        }

        // File logging settings
        LoggerSettings.saveToFile = configuration.saveToFile
        configuration.saveDirectoryPath?.let { LoggerSettings.saveDirectoryPath = it }
        configuration.logFileNameFormat?.let { LoggerSettings.logFileNameFormat = it }

        // Logger style
        LoggerSettings.loggerStyle = configuration.loggerStyle
    }

    override fun log(
        level: LogLevel,
        accessor: ServiceAccessor,
        message: String
    ) {
        cz.lukynka.prettylog.log(message, level.toPrettyLogType(accessor))
    }
}

val LoggingProvider.Companion.PrettyLog: PrettyLog
    get() = PrettyLog
