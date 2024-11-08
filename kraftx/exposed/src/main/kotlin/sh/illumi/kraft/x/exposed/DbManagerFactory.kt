package sh.illumi.kraft.x.exposed

import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

interface DbManagerFactory<TConfig : ExposedDbManagerConfigDsl, TManager : DbManager<TConfig>> {
    fun create(layer: RootLayer, configure: TConfig.() -> Unit): TManager
}