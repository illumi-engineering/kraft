package sh.illumi.kraft.x.http.extensions

import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.layer.lazyProperty
import sh.illumi.kraft.service.Service

import sh.illumi.kraft.x.http.HttpServer
import sh.illumi.kraft.x.http.routing.Route

val RootLayer<*>.httpServer: HttpServer by lazyProperty {
    HttpServer(this as RootLayer<*>) // i am going to kill myself!!!!
}

fun Service.httpRouting(block: Route.() -> Unit) {
    this.rootLayer.httpServer.rootRoute.apply(block)
}
