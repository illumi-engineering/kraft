package sh.illumi.kraft.x.logging.provider

import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.x.logging.LogLevel

interface LoggingProvider {
    fun log(level: LogLevel, accessor: ServiceAccessor, message: String)
    
    object StdOut : LoggingProvider {
        override fun log(level: LogLevel, accessor: ServiceAccessor, message: String) {
            println("[${level.display}] ${accessor.label}: $message")
        }
    }
    
    object NoOp : LoggingProvider {
        override fun log(level: LogLevel, accessor: ServiceAccessor, message: String) {}
    }
}