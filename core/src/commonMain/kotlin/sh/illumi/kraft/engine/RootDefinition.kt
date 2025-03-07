package sh.illumi.kraft.engine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import sh.illumi.kraft.layer.Layer
import kotlin.coroutines.CoroutineContext

class RootDefinition(
    val produceLayer: (CoroutineScope) -> Layer,
    val dispatcher: CoroutineDispatcher,
    val setup: Layer.() -> Unit
)