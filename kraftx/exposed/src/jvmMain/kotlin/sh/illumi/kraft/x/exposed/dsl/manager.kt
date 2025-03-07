package sh.illumi.kraft.x.exposed.dsl

abstract class ExposedDbManagerConfigDsl(
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
        jdbcUrl: String = "",
        driverClassName: String = "",
        username: String = "",
        password: String = "",
        transactionParallelism: Int = 10
    ) : ExposedDbManagerConfigDsl(jdbcUrl, driverClassName, username, password, transactionParallelism) {
        var maximumPoolSize = 10
        var isReadOnly = false
        var transactionIsolation: String? = null

        override fun check() {
            require(maximumPoolSize > 0) { "maximumPoolSize must be greater than 0" }
        }
    }

    class Single(
        jdbcUrl: String = "",
        driverClassName: String = "",
        username: String = "",
        password: String = "",
        transactionParallelism: Int = 10
    ) :
        ExposedDbManagerConfigDsl(jdbcUrl, driverClassName, username, password, transactionParallelism) {

        override fun check() {
            // No checks needed
        }
    }
}