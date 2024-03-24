package ua.dp.syso.person

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class PersonApiApplication

fun main(args: Array<String>) {
	runApplication<PersonApiApplication>(*args)
}
