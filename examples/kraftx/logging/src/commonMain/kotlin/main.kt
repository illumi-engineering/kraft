import kotlinx.coroutines.runBlocking
import sh.illumi.kraft.layer.launchLayer

fun main(args: Array<String>) {
    when (args[0]) {
        "prettylog" -> {
            println("Starting PrettyLog example...")
            runBlocking {
                launchLayer { PrettyLogExampleLayer }
            }
        }
    }
}