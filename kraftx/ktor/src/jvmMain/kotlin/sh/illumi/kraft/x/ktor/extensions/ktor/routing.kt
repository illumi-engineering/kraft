package sh.illumi.kraft.x.ktor.extensions.ktor

import io.ktor.server.routing.*
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.x.ktor.KraftKtor

val Route.kraftLayer: Layer
    get() = KraftKtor.instance.rootLayer
