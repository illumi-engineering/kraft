package sh.illumi.kraft.examples.playground

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import sh.illumi.kraft.examples.playground.layer.PlaygroundLayer
import sh.illumi.kraft.layer.launchLayer

fun main() = runBlocking(Dispatchers.Default) {
    val playgroundLayer = launchLayer { scope ->
        PlaygroundLayer(scope)
    }
    playgroundLayer.start()
}