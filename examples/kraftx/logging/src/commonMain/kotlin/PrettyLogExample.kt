import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.service.registering

class PrettyLogExampleLayer(
    override val coroutineScope: CoroutineScope
) : Layer() {
//    val loggingService by registering()

}