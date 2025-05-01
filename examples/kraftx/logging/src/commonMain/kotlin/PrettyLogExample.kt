import cz.lukynka.prettylog.LoggerStyle
import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.service.registering
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.prettylog.PrettyLog
import sh.illumi.kraft.x.logging.service.LoggingService

class PrettyLogExampleLayer(
    override val coroutineScope: CoroutineScope
) : Layer() {
    val log by registering(LoggingService) {
        withProvider(PrettyLog) {
            withLevel(LogLevel.All)

            configure {
                saveToFile = true
                saveDirectoryPath = "./logs/"
                logFileNameFormat = "yyyy-MM-dd-Hms"
                loggerStyle = LoggerStyle.PREFIX
            }
        }
    }

    fun start() {
        log.trace("This is a trace message")
        log.debug("This is a debug message")
        log.info("This is an info message")
        log.warning("This is a warning message")
        log.error("This is an error message")
        log.fatal("This is a fatal message")
    }
}