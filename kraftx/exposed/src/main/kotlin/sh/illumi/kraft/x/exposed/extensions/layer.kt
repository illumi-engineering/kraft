package sh.illumi.kraft.x.exposed.extensions

import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.exposed.DbManager
import sh.illumi.kraft.x.exposed.dsl.ExposedConfigurationDsl
import sh.illumi.kraft.x.exposed.layer.LayerWithExposed

fun <TLayer> TLayer.createExposedHikari(configure: ExposedConfigurationDsl.Hikari.() -> Unit)
where
    TLayer : RootLayer,
    TLayer : LayerWithExposed
= DbManager.Hikari(this, configure)


fun <TLayer> TLayer.createExposedSingle(configure: ExposedConfigurationDsl.() -> Unit)
where
    TLayer : RootLayer,
    TLayer : LayerWithExposed
= DbManager.Single(this, configure)
