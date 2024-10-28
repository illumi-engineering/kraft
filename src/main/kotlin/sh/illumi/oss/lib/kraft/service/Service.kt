package sh.illumi.oss.lib.kraft.service

import kotlin.coroutines.CoroutineContext

/**
 * Base class for services
 *
 * @param TConfig The configuration type for the service
 *
 * @property coroutineContext The coroutine context to use for the service
 */
abstract class Service<TConfig>(
    protected val coroutineContext: CoroutineContext // todo: do we need this?
) {
    abstract fun getConfig(): TConfig
}