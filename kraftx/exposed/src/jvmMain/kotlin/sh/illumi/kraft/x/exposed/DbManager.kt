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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sh.illumi.kraft.layer.ApplicationLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

abstract class DbManager(
    private val layer: ApplicationLayer,
    parallelism: Int,
) {
    abstract val db: Database
    abstract val isPooled: Boolean

    private val transactionContext: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(parallelism)
    protected val log: Logger get() = LoggerFactory.getLogger(this::class.java)

    class Hikari(
        layer: ApplicationLayer,
        config: ExposedDbManagerConfigDsl.Hikari,
    ) : DbManager(layer, config.transactionParallelism) {
        override val isPooled = true
        override val db = Database.connect(HikariDataSource(HikariConfig().apply {
            this.jdbcUrl = config.jdbcUrl
            this.driverClassName = config.driverClassName
            this.username = config.username
            this.password = config.password
            this.maximumPoolSize = config.maximumPoolSize
            this.isReadOnly = config.isReadOnly
            this.transactionIsolation = config.transactionIsolation
        }))

        companion object : DbManagerFactory<ExposedDbManagerConfigDsl.Hikari> {
            override fun create(layer: ApplicationLayer, configure: ExposedDbManagerConfigDsl.Hikari.() -> Unit) =
                ExposedDbManagerConfigDsl.Hikari()
                    .apply(configure)
                    .also { it.check() }
                    .let { Hikari(layer, it) }
        }
    }

    class Single(
        layer: ApplicationLayer,
        config: ExposedDbManagerConfigDsl.Single,
    ) : DbManager(layer, config.transactionParallelism) {
        override val isPooled = false
        override val db = Database.connect(config.jdbcUrl, config.driverClassName, config.username, config.password)

        companion object : DbManagerFactory<ExposedDbManagerConfigDsl.Single> {
            override fun create(layer: ApplicationLayer, configure: ExposedDbManagerConfigDsl.Single.() -> Unit) =
                ExposedDbManagerConfigDsl.Single()
                    .apply(configure)
                    .also { it.check() }
                    .let { Single(layer, it) }
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