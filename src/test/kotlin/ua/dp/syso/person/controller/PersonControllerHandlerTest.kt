package ua.dp.syso.person.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
class PersonControllerHandlerTest @Autowired constructor(
    val personRepository: PersonRepository,
    val mockMvc: MockMvc
) {

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD respond with BAD_REQUEST WHEN incorrect argument provided`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$baseUrl/list?sort=any_field"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("message").value("Invalid argument" ))
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD respond with NOT_FOUND WHEN incorrect resource requested`() {
        mockMvc
            .perform(MockMvcRequestBuilders.get("$baseUrl/incorrect_resource"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("message").value("No resource found" ))
    }
}
