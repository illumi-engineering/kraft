package sh.illumi.kraft.x.http.call

import com.oxyggen.net.HttpURL
import sh.illumi.kraft.x.http.HttpMethod
import sh.illumi.kraft.x.http.routing.Route

abstract class Call(
    val uri: HttpURL,
    val method: HttpMethod,
    val route: Route
) {
    val pathParams: Map<String, String> = route.path.parts.zip(uri.path.parts).mapNotNull { (routePart, uriPart) ->
        if (routePart.startsWith("{") && routePart.endsWith("}")) {
            routePart.substring(1, routePart.length - 1) to uriPart
        } else null
    }.toMap()
    val queryParams by uri.queryParam

    suspend fun handle() {
        route.handlers[method]?.invoke(this)
    }
}