package sh.illumi.kraft.x.exposed.extensions

import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.x.exposed.DbManager
import sh.illumi.kraft.x.exposed.DbManagerFactory
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl
import sh.illumi.kraft.x.exposed.layer.LayerWithExposed

// todo: Document this
// todo: Test this
inline fun <TLayer, reified TDbConfig, reified TDbManager> TLayer.createExposed(
    factory: DbManagerFactory<TDbConfig, TDbManager>,
    noinline configure: TDbConfig.() -> Unit
): TDbManager
where
    TLayer : ApplicationLayer,
    TLayer : LayerWithExposed,
    TDbConfig : ExposedDbManagerConfigDsl,
    TDbManager : DbManager<TDbConfig>
= factory.create(this, configure)