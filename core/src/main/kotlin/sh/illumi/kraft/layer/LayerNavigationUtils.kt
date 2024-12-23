package sh.illumi.kraft.layer

import org.slf4j.LoggerFactory
import sh.illumi.kraft.KraftException

class LayerNavigationUtils(val applicationLayer: ApplicationLayer) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * All the layers from this layer to the root layer
     */
    val toRoot: List<ApplicationLayer> get() {
        val layers = mutableListOf<ApplicationLayer>()
        var currentLayer: ApplicationLayer = this.applicationLayer

        while (true) {
            layers += currentLayer
            if (!currentLayer.isRoot) currentLayer.parent?.let { currentLayer = it }
            else break
        }

        log.debug("Layers from ${this.applicationLayer.javaClass.simpleName} to root: ${layers.joinToString { it.javaClass.simpleName }}")

        return layers
    }

    /**
     * Get all the layers from this layer to a layer of a specific type.
     *
     * @return A list of layers from this layer to the target layer
     */
    inline fun <reified TTargetLayer : ApplicationLayer> toTyped(): List<ApplicationLayer> {
        val layers = mutableListOf<ApplicationLayer>()
        var currentLayer: ApplicationLayer = this.applicationLayer

        while (true) {
            layers += currentLayer

            if (currentLayer is TTargetLayer) break
            else {
                if (currentLayer.isRoot)
                    throw KraftException("${this.javaClass.kotlin.simpleName} has no parent of type ${TTargetLayer::class.simpleName}")
                else currentLayer.parent?.let { currentLayer = it }
            }
        }

        return layers
    }
}