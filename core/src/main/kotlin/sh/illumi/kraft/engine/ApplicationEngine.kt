package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import sh.illumi.kraft.KraftException
import sh.illumi.kraft.engine.ApplicationEngine.Default
import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.util.argsMatchParams

/**
 * An ApplicationEngine is the entry point for a Kraft application. It is
 * responsible for initializing coroutine scopes and starting root layers.
 *
 * The included [Default] class can be used as a default application engine if
 * no extra functionality is needed, or a custom application engine can be
 * created by extending this interface.
 */
interface ApplicationEngine {
    fun registerShutdownHook()

    fun <TLayer : ApplicationLayer> createRoot(createRoot: () -> TLayer): TLayer {
        val rootLayer = createRoot()
        return rootLayer
    }

     interface Default : ApplicationEngine {
         val coroutineScope get() = CoroutineScope(Dispatchers.Default)
         var rootLayer: ApplicationLayer

         override fun registerShutdownHook() {
             Runtime.getRuntime().addShutdownHook(Thread {
                 rootLayer.shutdown()
             })
         }

     }
}

inline fun <reified TLayer : ApplicationLayer> Default.startRoot(vararg constructorArgs: Any) {
    rootLayer = createRoot<TLayer>(coroutineScope, *constructorArgs)
    rootLayer.start()
}

inline fun <reified TLayer : ApplicationLayer> ApplicationEngine.createRoot(
    coroutineScope: CoroutineScope,
    vararg constructorArgs: Any
) = createRoot {
    TLayer::class.constructors.firstOrNull {
        it.parameters.size == 1 + constructorArgs.size &&
        it.parameters[0].type.classifier == CoroutineScope::class &&
        argsMatchParams(constructorArgs, it.parameters.drop(1).toTypedArray())
    }?.call(coroutineScope, *constructorArgs) ?: throw KraftException("Root layer has no suitable constructor")
}
