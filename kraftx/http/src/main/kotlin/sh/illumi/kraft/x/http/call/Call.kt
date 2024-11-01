package sh.illumi.kraft.x.http.call

import com.oxyggen.net.HttpURL
import sh.illumi.kraft.x.http.HttpMethod
import sh.illumi.kraft.x.http.routing.Route

abstract class Call(
    val uri: HttpURL,
    val method: HttpMethod,
    val route: Route
) {
    val pathParams: Map<String, String> = TODO()
    val queryParams by uri.queryParam

    suspend fun handle() {
        route.handlers[method]?.invoke(route)
    }
}