package sh.illumi.kraft.x.logging.prettylog

import cz.lukynka.prettylog.AnsiPair
import cz.lukynka.prettylog.CustomLogType
import sh.illumi.kraft.service.ServiceAccessor
import sh.illumi.kraft.x.logging.LogLevel
import sh.illumi.kraft.x.logging.exceptions.ExplicitLogLevelAllException

fun LogLevel.toPrettyLogType(accessor: ServiceAccessor): CustomLogType {
    val prefix = "[$display] ${accessor.label}"

    return when (this) {
        LogLevel.All -> throw ExplicitLogLevelAllException
        LogLevel.Trace -> CustomLogType(prefix, AnsiPair.LIGHT_GRAY)
        LogLevel.Debug -> CustomLogType(prefix, AnsiPair.LIGHT_BLUE)
        LogLevel.Info -> CustomLogType(prefix, AnsiPair.LIGHT_GREEN)
        LogLevel.Warning -> CustomLogType(prefix, AnsiPair.YELLOW)
        LogLevel.Error -> CustomLogType(prefix, AnsiPair.RED)
        LogLevel.Fatal -> CustomLogType(prefix, AnsiPair.DARK_RED)
    }
}