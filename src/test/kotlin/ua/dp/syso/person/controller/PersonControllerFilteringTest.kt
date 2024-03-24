package ua.dp.syso.person.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ua.dp.syso.person.config.TestConfiguration
import ua.dp.syso.person.repository.PersonRepository
import ua.dp.syso.person.security.UserRoles
import ua.dp.syso.person.utils.TestUtils
import ua.dp.syso.person.utils.TestUtils.Companion.baseUrl
import ua.dp.syso.person.utils.TestUtils.Companion.toModel
import java.util.stream.Stream


@SpringBootTest(classes = [TestConfiguration::class])
@AutoConfigureMockMvc
class PersonControllerFilteringTest @Autowired constructor(
    val personRepository: PersonRepository,
    val mockMvc: MockMvc
) {

    companion object {
        @JvmStatic
        private fun personArguments(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(21, "John"),
                Arguments.of(33, "William"),
                Arguments.of(35, "Tim"),
                Arguments.of(46, "Robert"),
                Arguments.of(57, "Steven"),
            )
        }
    }

    @BeforeEach
    fun beforeEach() {
        personRepository.deleteAll()

        personArguments()
            .forEach{
                val args = it.get()
                val person = TestUtils.createTestPersonDto(args[1] as String,
                    "Test",
                    age = (args[0] as Int).toShort()).toModel()
                personRepository.save(person)
        }
    }

    @ParameterizedTest
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @ValueSource(strings = ["William", "william", "WILLIAM", "Will", "will", "WILL", "ILL", "liam", "LIAM", "am", "AM" ])
    fun `SHOULD filter all names but William WHEN following person names provided`(name: String) {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$baseUrl/list?name={name}", name))
            .andExpect(status().isOk)
            .andExpect(jsonPath("content[0].name").value("William" ))
            .andExpect(jsonPath("page.totalElements").value(1 ))
    }

    @ParameterizedTest
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    @MethodSource("personArguments")
    fun `SHOULD match person age WHEN age filter used`(age:Int, name: String) {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$baseUrl/list?age={age}", age))
            .andExpect(status().isOk)
            .andExpect(jsonPath("content[0].name").value(name ))
            .andExpect(jsonPath("page.totalElements").value(1 ))
    }


}
