package sh.illumi.kraft.x.exposed.layer

import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.x.exposed.DbManager
import sh.illumi.kraft.x.exposed.DbManagerFactory
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

interface LayerWithExposed : ApplicationLayer {
    val db: DbManager

    fun <TConfig : ExposedDbManagerConfigDsl> exposed(
        dbManagerFactory: DbManagerFactory<TConfig>,
        configure: TConfig.() -> Unit
    ) = dbManagerFactory.create(this, configure)
}