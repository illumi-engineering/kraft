package sh.illumi.kraft.x.exposed

import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

interface DbManagerFactory<TConfig : ExposedDbManagerConfigDsl> {
    fun create(layer: ApplicationLayer, configure: TConfig.() -> Unit): DbManager
}
