package sh.illumi.kraft.examples.kraftx.logging

import kotlinx.coroutines.runBlocking
import sh.illumi.kraft.layer.launchLayer

fun main(args: Array<String>) {
    when (args[0]) {
        "prettylog" -> {
            println("Starting PrettyLog example...")
            runBlocking {
                val prettyLogLayer = launchLayer { scope ->
                    PrettyLogExampleLayer(scope)
                }

                prettyLogLayer.start()
            }
        }
    }
}