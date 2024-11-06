package sh.illumi.kraft.x.exposed.layer

import sh.illumi.kraft.x.exposed.DbManager

interface LayerWithExposed {
    val db: DbManager
}