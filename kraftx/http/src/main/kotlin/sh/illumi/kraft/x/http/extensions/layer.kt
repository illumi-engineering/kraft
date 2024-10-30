package sh.illumi.kraft.x.http.extensions

import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.layer.lazyProperty
import sh.illumi.kraft.service.Service

import sh.illumi.kraft.x.http.HttpServer
import sh.illumi.kraft.x.http.routing.Route

var <TLayer : ApplicationLayer<TLayer>> TLayer.httpServer: HttpServer by lazyProperty<TLayer, HttpServer> {
    if (this !is RootLayer<*>) throw IllegalStateException("Cannot create HttpServer on non-root layer")
    HttpServer(this)
}

fun Service.httpRouting(block: Route.() -> Unit) {
    this.rootLayer.httpServer.rootRoute.apply(block)
}
