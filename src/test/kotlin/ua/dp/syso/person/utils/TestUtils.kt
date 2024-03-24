package ua.dp.syso.person.utils

import ua.dp.syso.person.dto.PersonDto
import ua.dp.syso.person.model.Person
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class TestUtils {

    companion object {

        fun PersonDto.toModel(): Person {
            return this.toModelWithId(null)
        }

        fun PersonDto.toModelWithId(id: Long?): Person {
            return Person(
                this.name,
                this.surname,
                this.email,
                this.phone,
                this.dateOfBirth,
                this.username,
                this.password,
                id
            )
        }

        fun createTestPersonDto(name: String, surname: String, age: Short? = null, phone: String? = null): PersonDto {
            val years = age ?: Random.nextInt(18,99)
            val username = "${name.lowercase()}.${surname.lowercase()}"
            return PersonDto(
                name,
                surname,
                "$username@example.com",
                phone ?: String.format("+12345%06d", Random.nextInt(999999)),
                LocalDate.now().minusYears(years.toLong()),
                username,
                UUID.randomUUID().toString()
            )
        }
    }
}