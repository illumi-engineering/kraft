package sh.illumi.kraft.x.exposed

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedConfigurationDsl

sealed interface DbManager {
    val db: Database
    val isPooled: Boolean
    val layer: RootLayer

    var transactionContext: CoroutineDispatcher

    class Hikari(
        override val layer: RootLayer,
        configure: ExposedConfigurationDsl.Hikari.() -> Unit
    ) : DbManager {
        override var transactionContext: CoroutineDispatcher
        private val dataSource: HikariDataSource

        init {
            val dsl = ExposedConfigurationDsl.Hikari().apply(configure)
            dsl.check()

            transactionContext = Dispatchers.IO.limitedParallelism(dsl.transactionParallelism)

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
        override var transactionContext: CoroutineDispatcher
        private val jdbcUrl: String
        private val driverClassName: String
        private val username: String
        private val password: String

        init {
            val dsl = ExposedConfigurationDsl().apply(configure)
            dsl.check()

            transactionContext = Dispatchers.IO.limitedParallelism(dsl.transactionParallelism)

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

    fun <T> transaction(block: Transaction.() -> T) = layer.coroutineScope.async {
        withContext(transactionContext) { transaction(db, block) }
    }

    suspend fun <T> suspendTransaction(block: suspend Transaction.() -> T) =
        newSuspendedTransaction(
            context = transactionContext,
            db = db,
            statement = block
        )
}