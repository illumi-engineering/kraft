package sh.illumi.kraft.x.ktor

import io.ktor.server.application.*
import io.ktor.util.*
import sh.illumi.kraft.layer.RootLayer

class KraftKtor(configuration: Configuration) {
    val rootLayer = configuration.rootLayer

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, KraftKtor> {
        override val key = AttributeKey<KraftKtor>("KraftKtor")
        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): KraftKtor {
            val plugin = KraftKtor(Configuration().apply(configure))
            instance = plugin
            return plugin
        }

        lateinit var instance: KraftKtor
    }

    class Configuration {
        lateinit var rootLayer: RootLayer
    }
}