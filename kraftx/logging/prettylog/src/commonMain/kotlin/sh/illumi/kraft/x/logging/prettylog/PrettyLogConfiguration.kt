package sh.illumi.kraft.x.logging.prettylog

import cz.lukynka.prettylog.LoggerStyle
import sh.illumi.kraft.x.logging.provider.ProviderConfiguration

data class PrettyLogConfiguration(
    var saveToFile: Boolean = false,
    var saveDirectoryPath: String? = null,
    var logFileNameFormat: String? = null,
    var loggerStyle: LoggerStyle = LoggerStyle.PREFIX,
) : ProviderConfiguration {
    fun loggerStyle(style: LoggerStyle) {
        this.loggerStyle = style
    }

    fun saveToFile(path: String, logFileNameFormat: String = "yyyy-MM-dd-Hms") {
        this.saveToFile = true
        this.saveDirectoryPath = path
        this.logFileNameFormat = logFileNameFormat
    }
}