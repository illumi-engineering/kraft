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
import sh.illumi.kraft.layer.RootLayer
import sh.illumi.kraft.x.exposed.dsl.ExposedDbManagerConfigDsl

abstract class DbManager<TConfig : ExposedDbManagerConfigDsl>(
    private val layer: RootLayer,
    config: TConfig,
) {
    abstract val db: Database
    abstract val isPooled: Boolean

    private val transactionContext: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(config.transactionParallelism)
    protected val log: Logger get() = LoggerFactory.getLogger(this::class.java)

    class Hikari(
        layer: RootLayer,
        config: ExposedDbManagerConfigDsl.Hikari,
    ) : DbManager<ExposedDbManagerConfigDsl.Hikari>(layer, config) {
        override val isPooled = true
        override val db by lazy {
            Database.connect(HikariDataSource(HikariConfig().apply {
                this.jdbcUrl = config.jdbcUrl
                this.driverClassName = config.driverClassName
                this.username = config.username
                this.password = config.password
                this.maximumPoolSize = config.maximumPoolSize
                this.isReadOnly = config.isReadOnly
                this.transactionIsolation = config.transactionIsolation
            }))
        }
    }

    class Single(
        layer: RootLayer,
        config: ExposedDbManagerConfigDsl.Single,
    ) : DbManager<ExposedDbManagerConfigDsl.Single>(layer, config) {
        override val isPooled = false
        override val db by lazy {
            Database.connect(config.jdbcUrl, config.driverClassName, config.username, config.password)
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