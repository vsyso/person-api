package ua.dp.syso.person.endpoint

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import ua.dp.syso.person.repository.PersonRepository

/**
 * Actuator endpoint to display count of persons in the database
 */
@Component
@Endpoint(id = "total-persons")
class TotalPersonsEndpoint(private val personRepository: PersonRepository) {

    @Bean
    @ReadOperation
    fun totalPersons(): Long {
        return personRepository.count()
    }
}
