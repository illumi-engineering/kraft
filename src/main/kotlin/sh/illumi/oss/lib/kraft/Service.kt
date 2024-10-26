package sh.illumi.oss.lib.kraft

import kotlinx.coroutines.CoroutineScope


abstract class Service<TConfig>(
    val coroutineScope : CoroutineScope
) {
    abstract fun getConfig(): TConfig
}