package sh.illumi.kraft.x.exposed

import sh.illumi.kraft.layer.Layer
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

interface DbManagerFactory<TConfig : ExposedDbManagerConfigDsl> {
    fun create(layer: Layer, configure: TConfig.() -> Unit): DbManager
}
