package sh.illumi.kraft.examples.playground_mpp

import sh.illumi.kraft.engine.kraftEngine
import sh.illumi.kraft.examples.playground_mpp.layer.PlaygroundLayer

fun main() {
    val engine = kraftEngine { 
        addBackgroundRoot({ cs -> PlaygroundLayer(cs) }) { 
            start()
        }
    }
    
    engine.start()
}