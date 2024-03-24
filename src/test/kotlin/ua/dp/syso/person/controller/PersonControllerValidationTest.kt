package ua.dp.syso.person.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.*
import ua.dp.syso.person.config.TestConfiguration
import ua.dp.syso.person.repository.PersonRepository
import ua.dp.syso.person.security.UserRoles
import ua.dp.syso.person.utils.TestUtils
import ua.dp.syso.person.utils.TestUtils.Companion.baseUrl
import java.time.LocalDate

@SpringBootTest(classes = [TestConfiguration::class])
@AutoConfigureMockMvc
class PersonControllerValidationTest @Autowired constructor(
    val personRepository: PersonRepository,
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "n", "absolutelyRidiculouslyLongNameWithMoreThan64CharsToFailValidation"])
    fun `SHOULD fail validation WHEN following person names provided`(name: String) {

        val personDto = TestUtils.createTestPersonDto(name, "Person")
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "s", "absolutelyRidiculouslyLongSurnameWithMoreThan64CharsToFailValidation"])
    fun `SHOULD fail validation WHEN following person surnames provided`(surname: String) {

        val personDto = TestUtils.createTestPersonDto("Name", surname)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "email", "email@", "email@."])
    fun `SHOULD fail validation WHEN following person email provided`(email: String) {

        val personDto = TestUtils.createTestPersonDto("Name", "Surname", email = email)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "123456p", "123456=", "123:"])
    fun `SHOULD fail validation WHEN following person phone provided`(phone: String) {

        val personDto = TestUtils.createTestPersonDto("Name", "Surname", phone = phone)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["2100-01-01"])
    fun `SHOULD fail validation WHEN following person dateOfBirth provided`(dob: String) {

        val dateOfBirth = LocalDate.parse(dob)
        val personDto = TestUtils.createTestPersonDto("Name", "Surname", dob = dateOfBirth)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "a", "ab", "absolutelyRidiculouslyLongUsernameWithMoreThan64CharsToFailValidation"])
    fun `SHOULD fail validation WHEN following person username provided`(username: String) {

        val personDto = TestUtils.createTestPersonDto("Name", "Surname", username = username)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }

    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ParameterizedTest
    @ValueSource(strings = ["", "abcdefg"])
    fun `SHOULD fail validation WHEN following person password provided`(password: String) {

        val personDto = TestUtils.createTestPersonDto("Name", "Surname", password = password)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }
}
