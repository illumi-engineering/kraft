package sh.illumi.oss.lib.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import sh.illumi.oss.lib.kraft.KraftException
import sh.illumi.oss.lib.kraft.RootLayer

/**
 * An ApplicationEngine is the entry point for a Kraft application. It is
 * responsible for initializing the coroutine scope and starting the root layer.
 *
 * The included [Default] object can be used as a default application engine if
 * no extra functionality is needed, or a custom application engine can be
 * created by extending this class.
 */
abstract class ApplicationEngine {
    lateinit var rootLayer: RootLayer<*> private set

    fun <TRootLayer : RootLayer<TRootLayer>> startRoot(createRoot: suspend CoroutineScope.() -> TRootLayer) = runBlocking {
        rootLayer = createRoot()
        rootLayer.start()
    }

    inline fun <reified TRootLayer : RootLayer<TRootLayer>> startRoot() = startRoot {
        TRootLayer::class.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters[0].type.classifier == CoroutineScope::class
        }?.call(this) ?: throw KraftException("Root layer has no suitable constructor")
    }

    companion object Default : ApplicationEngine()
}