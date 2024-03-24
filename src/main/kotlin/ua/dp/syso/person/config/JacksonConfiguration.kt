package ua.dp.syso.person.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.springframework.context.annotation.Configuration

/**
 * Configure object mapper to avoid exception on filter provider is missing
 * when it's not needed
 */
@Configuration
class JacksonConfiguration(objectMapper: ObjectMapper) {
    init {
        objectMapper.setFilterProvider(SimpleFilterProvider().setFailOnUnknownId(false))
    }
}
