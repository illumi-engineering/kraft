package sh.illumi.kraft.x.exposed

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedConfigurationDsl

sealed interface DbManager {
    val db: Database
    val isPooled: Boolean
    val layer: RootLayer

    class Hikari(
        override val layer: RootLayer,
        configure: ExposedConfigurationDsl.Hikari.() -> Unit
    ) : DbManager {
        private val dataSource: HikariDataSource

        init {
            val dsl = ExposedConfigurationDsl.Hikari().apply(configure)
            dsl.check()

            dataSource = HikariDataSource(HikariConfig().apply {
                jdbcUrl = dsl.jdbcUrl
                driverClassName = dsl.driverClassName
                username = dsl.username
                password = dsl.password
                maximumPoolSize = dsl.hikari.maximumPoolSize
                isReadOnly = dsl.hikari.isReadOnly
                transactionIsolation = dsl.hikari.transactionIsolationLevel
            })
        }

        override val isPooled = true
        override val db by lazy {
            Database.connect(dataSource)
        }
    }

    class Single(
        override val layer: RootLayer,
        configure: ExposedConfigurationDsl.() -> Unit
    ) : DbManager {
        private val jdbcUrl: String
        private val driverClassName: String
        private val username: String
        private val password: String

        init {
            val dsl = ExposedConfigurationDsl().apply(configure)
            dsl.check()

            jdbcUrl = dsl.jdbcUrl
            driverClassName = dsl.driverClassName
            username = dsl.username
            password = dsl.password
        }

        override val isPooled = false
        override val db by lazy {
            Database.connect(jdbcUrl, driverClassName, username, password)
        }
    }

    fun <T> transaction(block: Transaction.() -> T) = transaction(db, block)
}