package sh.illumi.kraft.x.http.extensions

import io.ktor.server.routing.*
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.service.ServiceContainer

val RoutingContext.services: ServiceContainer get() = RootLayer.instance.services