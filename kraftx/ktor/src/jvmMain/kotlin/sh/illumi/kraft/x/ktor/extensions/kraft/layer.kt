package sh.illumi.kraft.x.ktor.extensions.kraft

import io.ktor.server.application.*
import io.ktor.server.engine.*
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.x.ktor.KraftKtor

fun <TEngine, TConfiguration> Layer.embeddedServer(
    factory: ApplicationEngineFactory<TEngine, TConfiguration>,
    port: Int = 80,
    host: String = "0.0.0.0",
    watchPaths: List<String> = listOf(),
    module: Application.() -> Unit
): EmbeddedServer<TEngine, TConfiguration>
where
    TEngine : ApplicationEngine,
    TConfiguration : ApplicationEngine.Configuration
= this.let { root -> // wired
    root.coroutineScope.embeddedServer(factory, port, host, watchPaths, this.coroutineScope.coroutineContext) {
        install(KraftKtor) {
            rootLayer = root
        }
        module()
    }
}

