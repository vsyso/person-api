package ua.dp.syso.person.utils

import ua.dp.syso.person.dto.PersonDto
import ua.dp.syso.person.model.Person
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

class TestUtils {

    companion object {

        val baseUrl = "/api/v1"

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

        fun createTestPersonDto(name: String,
                                surname: String,
                                age: Short? = null,
                                phone: String? = null,
                                email: String? = null,
                                dob: LocalDate? = null,
                                username: String? = null,
                                password: String? = null): PersonDto {
            val years = age ?: Random.nextInt(18,99)
            val user = "${name.lowercase()}.${surname.lowercase()}"
            return PersonDto(
                name,
                surname,
                email ?: "$user@example.com",
                phone ?: String.format("+12345%06d", Random.nextInt(999999)),
                dob ?: LocalDate.now().minusYears(years.toLong()),
                username ?: user,
                password ?: UUID.randomUUID().toString()
            )
        }
    }
}
