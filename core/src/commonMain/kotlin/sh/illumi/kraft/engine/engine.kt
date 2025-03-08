package sh.illumi.kraft.engine

fun kraftEngine(block: KraftEngine.() -> Unit): KraftEngine {
    return KraftEngine().apply(block)
}