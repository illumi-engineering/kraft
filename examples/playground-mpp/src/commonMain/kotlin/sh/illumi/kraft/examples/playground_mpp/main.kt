package sh.illumi.kraft.examples.playground_mpp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import sh.illumi.kraft.examples.playground_mpp.layer.PlaygroundLayer

fun main() = runBlocking(Dispatchers.Default) {
    PlaygroundLayer(this).start()
}