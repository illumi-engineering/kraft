package sh.illumi.oss.lib.kraft

import kotlinx.coroutines.CoroutineScope

/**
 * Base class for services
 *
 * @param TConfig The configuration type for the service
 *
 * @property coroutineScope The coroutine scope to use for the service
 */
abstract class Service<TConfig>(
    private val coroutineScope : CoroutineScope
) {
    abstract fun getConfig(): TConfig
}