package sh.illumi.kraft.x.exposed.dsl

open class ExposedConfigurationDsl {
    var jdbcUrl: String = ""
    var driverClassName: String = ""
    var username: String = ""
    var password: String = ""

    open fun check() {
        require(jdbcUrl.isNotBlank()) { "jdbcUrl must be set" }
        require(driverClassName.isNotBlank()) { "driverClassName must be set" }
        require(username.isNotBlank()) { "username must be set" }
        require(password.isNotBlank()) { "password must be set" }
    }

    class Hikari : ExposedConfigurationDsl() {
        lateinit var hikari: ExposedHikariConfigurationDsl

        fun hikari(block: ExposedHikariConfigurationDsl.() -> Unit) {
            hikari = ExposedHikariConfigurationDsl().apply(block)
        }

        override fun check() {
            super.check()
            hikari.check()
        }
    }
}

class ExposedHikariConfigurationDsl {
    var maximumPoolSize = 10
    var isReadOnly = false
    var transactionIsolationLevel: String? = null

    fun check() {
        require(maximumPoolSize > 0) { "maximumPoolSize must be greater than 0" }
    }
}