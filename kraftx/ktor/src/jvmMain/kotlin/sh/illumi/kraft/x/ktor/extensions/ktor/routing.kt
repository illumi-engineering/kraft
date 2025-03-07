package sh.illumi.kraft.x.ktor.extensions.ktor

import io.ktor.server.routing.*
import sh.illumi.kraft.service.ServiceContainer
import sh.illumi.kraft.x.ktor.KraftKtor

val Route.services: ServiceContainer
    get() = KraftKtor.instance.rootLayer.services