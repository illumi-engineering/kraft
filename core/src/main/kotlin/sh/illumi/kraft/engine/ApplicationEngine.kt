package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import sh.illumi.kraft.KraftException
import sh.illumi.kraft.engine.ApplicationEngine.Default
import sh.illumi.kraft.layer.ApplicationLayer

/**
 * An ApplicationEngine is the entry point for a Kraft application. It is
 * responsible for initializing the coroutine scope and starting the root layer.
 *
 * The included [Default] object can be used as a default application engine if
 * no extra functionality is needed, or a custom application engine can be
 * created by extending this class.
 */
abstract class ApplicationEngine {
    lateinit var rootLayer: ApplicationLayer private set

    fun <TLayer : ApplicationLayer> startRoot(createRoot: suspend CoroutineScope.() -> ApplicationLayer) = runBlocking {
        rootLayer = createRoot()
        rootLayer.start()
    }

    inline fun <reified TLayer : ApplicationLayer> startRoot() = startRoot<TLayer> {
        TLayer::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters[0].type.classifier == CoroutineScope::class
        }?.call(this) ?: throw KraftException("Root layer has no suitable constructor")
    }

    companion object Default : ApplicationEngine()
}
