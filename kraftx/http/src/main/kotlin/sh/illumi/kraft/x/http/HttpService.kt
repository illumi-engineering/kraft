package sh.illumi.kraft.x.http

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sh.illumi.kraft.ApplicationLayer
import sh.illumi.kraft.RootLayer
import sh.illumi.kraft.service.Service

/**
 * A service that provides an HTTP server. It must be run on the [RootLayer] of
 * the application.
 */
class HttpService<TLayer : RootLayer<TLayer>>(
    override val coroutineScope: CoroutineScope,
    val root : TLayer,
) : Service {
    private lateinit var serverJob: Job

    fun startServer() {
        serverJob = coroutineScope.launch {

        }
    }
}