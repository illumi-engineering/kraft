package sh.illumi.kraft.layer

/**
 * A service layer with a parent
 *
 * @param TParentLayer The type of the parent layer
 * @property parentLayer The parent layer
 */
interface LayerWithParent<TParentLayer : ApplicationLayer> {
    val parentLayer: TParentLayer
}
