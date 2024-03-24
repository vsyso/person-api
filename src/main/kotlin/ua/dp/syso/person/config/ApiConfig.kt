package ua.dp.syso.person.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "config.api")
data class ApiConfig(var users: List<ApiUser> = listOf() ) {
    data class ApiUser(val name: String,
                       val password: String,
                       val roles: List<String>
    )
}
