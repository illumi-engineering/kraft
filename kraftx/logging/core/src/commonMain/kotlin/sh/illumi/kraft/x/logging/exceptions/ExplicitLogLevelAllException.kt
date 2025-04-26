package sh.illumi.kraft.x.logging.exceptions

object ExplicitLogLevelAllException : IllegalArgumentException(
    "LogLevel.All is intended to encompass all log levels, not to be used as a log level."
)