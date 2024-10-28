package sh.illumi.oss.lib.kraft.service

import kotlinx.coroutines.CoroutineScope

/**
 * Base interface for services
 *
 * @property coroutineScope The coroutine scope of the service's layer
 */
interface Service {
    val coroutineScope: CoroutineScope
}