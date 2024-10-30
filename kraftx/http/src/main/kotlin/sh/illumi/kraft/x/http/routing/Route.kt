package sh.illumi.kraft.x.http.routing

import com.oxyggen.io.Path
import sh.illumi.kraft.x.http.HttpMethod

open class Route(
    val path: Path = Path.parse("/"),
    val childRoutes: MutableList<Route> = mutableListOf(),
    val handlers: MutableMap<HttpMethod, suspend () -> Unit> = mutableMapOf()
) {
    fun route(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
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

    fun get(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.GET] = handler
        childRoutes += route
    }

    fun post(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.POST] = handler
        childRoutes += route
    }

    fun put(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.PUT] = handler
        childRoutes += route
    }

    fun patch(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.PATCH] = handler
        childRoutes += route
    }

    fun delete(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.DELETE] = handler
        childRoutes += route
    }

    fun options(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.OPTIONS] = handler
        childRoutes += route
    }

    fun head(routePath: String, handler: suspend () -> Unit) {
        val route = Route(path.resolve(routePath), mutableListOf())
        route.handlers[HttpMethod.HEAD] = handler
        childRoutes += route
    }
}