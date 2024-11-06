package sh.illumi.kraft.x.http.extensions

import io.ktor.server.application.*
import io.ktor.server.engine.*
import sh.illumi.kraft.layer.RootLayer

fun <TEngine, TConfiguration> RootLayer.embeddedServer(
    factory: ApplicationEngineFactory<TEngine, TConfiguration>,
    port: Int = 80,
    host: String = "0.0.0.0",
    watchPaths: List<String> = listOf(),
    module: Application.() -> Unit
) where
    TEngine : ApplicationEngine,
    TConfiguration : ApplicationEngine.Configuration
= this.coroutineScope.embeddedServer(factory, port, host, watchPaths, this.coroutineScope.coroutineContext, module)

