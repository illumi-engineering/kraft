package sh.illumi.kraft.x.ktor.extensions

import io.ktor.server.application.*
import io.ktor.server.engine.*
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.ktor.KraftKtor

fun <TEngine, TConfiguration> RootLayer.embeddedServer(
    factory: ApplicationEngineFactory<TEngine, TConfiguration>,
    port: Int = 80,
    host: String = "0.0.0.0",
    watchPaths: List<String> = listOf(),
    module: Application.() -> Unit
): EmbeddedServer<TEngine, TConfiguration>
where
    TEngine : ApplicationEngine,
    TConfiguration : ApplicationEngine.Configuration
{
    val root = this // tired
    return this.coroutineScope.embeddedServer(factory, port, host, watchPaths, this.coroutineScope.coroutineContext) {
        install(KraftKtor) {
            rootLayer = root
        }
        module()
    }
}

