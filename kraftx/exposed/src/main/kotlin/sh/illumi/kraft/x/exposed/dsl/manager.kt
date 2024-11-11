package sh.illumi.kraft.x.exposed.dsl

import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.x.exposed.DbManager
import sh.illumi.kraft.x.exposed.DbManagerFactory

abstract class ExposedDbManagerConfigDsl(
    private val layer: ApplicationLayer,
    var jdbcUrl: String = "",
    var driverClassName: String = "",
    var username: String = "",
    var password: String = "",
    var transactionParallelism: Int = 10,
) {

    open fun check() {
        require(jdbcUrl.isNotBlank()) { "jdbcUrl must not be blank" }
        require(driverClassName.isNotBlank()) { "driverClassName must not be blank" }
        require(username.isNotBlank()) { "username must not be blank" }
        require(password.isNotBlank()) { "password must not be blank" }
        require(transactionParallelism > 0) { "transactionParallelism must be greater than 0" }
    }

    class Hikari(
        layer: ApplicationLayer,
        jdbcUrl: String = "",
        driverClassName: String = "",
        username: String = "",
        password: String = "",
        transactionParallelism: Int = 10
    ) : ExposedDbManagerConfigDsl(layer, jdbcUrl, driverClassName, username, password, transactionParallelism) {
        var maximumPoolSize = 10
        var isReadOnly = false
        var transactionIsolation: String? = null

        override fun check() {
            require(maximumPoolSize > 0) { "maximumPoolSize must be greater than 0" }
        }
    }

    class Single(
        layer: ApplicationLayer,
        jdbcUrl: String = "",
        driverClassName: String = "",
        username: String = "",
        password: String = "",
        transactionParallelism: Int = 10
    ) :
        ExposedDbManagerConfigDsl(layer, jdbcUrl, driverClassName, username, password, transactionParallelism) {

        override fun check() {
            // No checks needed
        }
    }
}

object Hikari : DbManagerFactory<ExposedDbManagerConfigDsl.Hikari, DbManager.Hikari> {
    override fun create(layer: ApplicationLayer, configure: ExposedDbManagerConfigDsl.Hikari.() -> Unit) =
        ExposedDbManagerConfigDsl.Hikari(layer)
            .apply(configure)
            .also { it.check() }
            .let { DbManager.Hikari(layer, it) }
}

object Single : DbManagerFactory<ExposedDbManagerConfigDsl.Single, DbManager.Single> {
    override fun create(layer: ApplicationLayer, configure: ExposedDbManagerConfigDsl.Single.() -> Unit) =
        ExposedDbManagerConfigDsl.Single(layer)
            .apply(configure)
            .also { it.check() }
            .let { DbManager.Single(layer, it) }
}