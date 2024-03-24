package ua.dp.syso.person.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApiConfig::class)
class AppConfiguration {
    @Bean
    fun apiConfiguration(): ApiConfig {
        return ApiConfig()
    }
}
