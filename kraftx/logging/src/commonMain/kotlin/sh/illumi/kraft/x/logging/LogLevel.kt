package sh.illumi.kraft.x.logging

enum class LogLevel(val display: String) {
    All("ALL"),
    Trace("TRACE"),
    Debug("DEBUG"),
    Info("INFO"),
    Warning("WARNING"),
    Error("ERROR"),
    Fatal("FATAL")
    ;
    
    fun isAtLeast(level: LogLevel) = ordinal >= level.ordinal
    
    companion object {
        fun allFrom(level: LogLevel) = entries.filter { it.isAtLeast(level) }.toSet()
    }
}