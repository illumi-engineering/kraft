package sh.illumi.kraft.x.http

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.http.call.Call
import sh.illumi.kraft.x.http.routing.Route

/**
 * A service that provides an HTTP server. It must be run on the [RootLayer] of
 * the application.
 */
class HttpServer(
    private val layer: RootLayer,
) {
    private lateinit var serverJob: Job
    val rootRoute = Route()

    private val eventLoopGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val nettyServerBootstrap = ServerBootstrap()
        .group(eventLoopGroup, workerGroup)
        .handler(LoggingHandler(LogLevel.INFO))
        .childHandler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val p = ch.pipeline();
                p.addLast(HttpRequestDecoder());
                p.addLast(HttpResponseEncoder());
//                p.addLast(new CustomHttpServerHandler());
            }
        });

    fun route(routePath: String, block: Call.() -> Unit) {
        rootRoute.route(routePath, block)
    }

    fun startServer() {
        serverJob = layer.coroutineScope.launch {

        }
    }
}