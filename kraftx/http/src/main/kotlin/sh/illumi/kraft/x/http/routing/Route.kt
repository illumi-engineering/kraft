package sh.illumi.kraft.x.http.routing

import sh.illumi.kraft.x.http.HttpMethod

open class Route(
    val path: String = "/",
    val childRoutes: MutableList<Route> = mutableListOf(),
    val handlers: MutableMap<HttpMethod, suspend () -> Unit> = mutableMapOf()
) {
    fun route(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        for (httpMethod in HttpMethod.entries) route.handlers[httpMethod] = handler
        childRoutes += route
    }

    fun get(handler: suspend () -> Unit) {
        handlers[HttpMethod.GET] = handler
    }

    fun post(handler: suspend () -> Unit) {
        handlers[HttpMethod.POST] = handler
    }

    fun put(handler: suspend () -> Unit) {
        handlers[HttpMethod.PUT] = handler
    }

    fun patch(handler: suspend () -> Unit) {
        handlers[HttpMethod.PATCH] = handler
    }

    fun delete(handler: suspend () -> Unit) {
        handlers[HttpMethod.DELETE] = handler
    }

    fun options(handler: suspend () -> Unit) {
        handlers[HttpMethod.OPTIONS] = handler
    }

    fun head(handler: suspend () -> Unit) {
        handlers[HttpMethod.HEAD] = handler
    }

    fun get(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.GET] = handler
        childRoutes += route
    }

    fun post(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.POST] = handler
        childRoutes += route
    }

    fun put(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.PUT] = handler
        childRoutes += route
    }

    fun patch(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.PATCH] = handler
        childRoutes += route
    }

    fun delete(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.DELETE] = handler
        childRoutes += route
    }

    fun options(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.OPTIONS] = handler
        childRoutes += route
    }

    fun head(path: String, handler: suspend () -> Unit) {
        val route = Route(path, mutableListOf())
        route.handlers[HttpMethod.HEAD] = handler
        childRoutes += route
    }
}