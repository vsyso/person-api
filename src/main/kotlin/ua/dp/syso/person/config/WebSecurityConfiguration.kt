package ua.dp.syso.person.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ua.dp.syso.person.security.UserRoles

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(private val apiConfiguration: ApiConfig) {

    /**
     * Use BCrypt algorithm to encode passwords
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Register users from a config
     */
    @Bean
    fun userDetailsService(): UserDetailsService {
        val manager = InMemoryUserDetailsManager()
        for( user in apiConfiguration.users) {
            manager.createUser(
                User.withUsername(user.name)
                    .password(user.password)
                    .roles(*user.roles.toTypedArray())
                    .build()
            )
        }
        return manager
    }

    /**
     * Restrict access for certain endpoints
     */
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf{ it.disable() }
            .authorizeHttpRequests { it
                .requestMatchers(AntPathRequestMatcher("/actuator/total-persons", HttpMethod.GET.name()))
                .hasAnyRole(UserRoles.ADMIN_ROLE, UserRoles.USER_ROLE)
                .requestMatchers(AntPathRequestMatcher("/api/v1/list", HttpMethod.GET.name()))
                .hasAnyRole(UserRoles.ADMIN_ROLE, UserRoles.USER_ROLE)
                .requestMatchers(AntPathRequestMatcher("/api/v1/**"))
                .hasRole(UserRoles.ADMIN_ROLE)
                .anyRequest()
                .permitAll()
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement{ it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            .build()
    }
}
