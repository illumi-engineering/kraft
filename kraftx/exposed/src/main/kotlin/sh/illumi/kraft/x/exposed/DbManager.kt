package sh.illumi.kraft.x.exposed

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

sealed interface DbManager {
    val db: Database
    val isPooled: Boolean

    class Hikari() : DbManager {
        private val dataSource = HikariDataSource(HikariConfig())

        override val isPooled = true
        override val db by lazy {
            Database.connect(dataSource)
        }
    }
}