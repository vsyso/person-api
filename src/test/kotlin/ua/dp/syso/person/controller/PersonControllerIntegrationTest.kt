package ua.dp.syso.person.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ua.dp.syso.person.config.TestConfiguration
import ua.dp.syso.person.repository.PersonRepository
import ua.dp.syso.person.security.UserRoles
import ua.dp.syso.person.utils.TestUtils
import ua.dp.syso.person.utils.TestUtils.Companion.baseUrl
import ua.dp.syso.person.utils.TestUtils.Companion.toModel
import ua.dp.syso.person.utils.TestUtils.Companion.toModelWithId
import java.time.format.DateTimeFormatter

@SpringBootTest(classes = [TestConfiguration::class])
@AutoConfigureMockMvc
class PersonControllerIntegrationTest @Autowired constructor(
    val personRepository: PersonRepository,
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    @BeforeEach
    fun beforeEach() {
        personRepository.deleteAll()
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD fetch all added persons WHE get request sent`() {

        // Verify empty database first
        mockMvc.get("$baseUrl/persons")
            .andExpect {
                status { isNoContent() }
            }

        // Add initial persons
        val firstPersonDto = TestUtils.createTestPersonDto("Tom", "Lee", 20)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(firstPersonDto)
            }

        val secondPersonDto = TestUtils.createTestPersonDto("Rob", "Paul", 30)
        mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(secondPersonDto)
            }

        // Verify
        mockMvc.get("$baseUrl/persons")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
            .andExpect { jsonPath("content[0].id") { value("1" )}}
            .andExpect { jsonPath("content[0].name") { value(firstPersonDto.name )}}
            .andExpect { jsonPath("content[0].surname") { value(firstPersonDto.surname )}}
            .andExpect { jsonPath("content[0].email") { value(firstPersonDto.email )}}
            .andExpect { jsonPath("content[0].phone") { value(firstPersonDto.phone )}}
            .andExpect { jsonPath("content[0].dateOfBirth") { value(firstPersonDto.dateOfBirth.format(DateTimeFormatter.ISO_DATE) )}}
            .andExpect { jsonPath("content[0].age") { value( 20 ) }}

            .andExpect { jsonPath("content[1].id") { value("2" )}}
            .andExpect { jsonPath("content[1].name") { value(secondPersonDto.name )}}
            .andExpect { jsonPath("content[1].surname") { value(secondPersonDto.surname )}}
            .andExpect { jsonPath("content[1].email") { value(secondPersonDto.email )}}
            .andExpect { jsonPath("content[1].phone") { value(secondPersonDto.phone )}}
            .andExpect { jsonPath("content[1].dateOfBirth") { value(secondPersonDto.dateOfBirth.format(DateTimeFormatter.ISO_DATE) )}}
            .andExpect { jsonPath("content[1].age") { value( 30 ) }}

            .andExpect { jsonPath("page.size") { value( 20 ) }}
            .andExpect { jsonPath("page.number") { value( 0 ) }}
            .andExpect { jsonPath("page.totalElements") { value( 2 ) }}
            .andExpect { jsonPath("page.totalPages") { value( 1 ) }}
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD add person and return redirected url WHEN post request with dto sent`() {

        // Add a person
        val personDto = TestUtils.createTestPersonDto("Test", "Person")

        val result = mockMvc
            .post("$baseUrl/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(personDto)
            }.andExpect {
                status { isCreated() }
                redirectedUrlPattern("http://localhost$baseUrl/persons/*")
            }.andReturn()

        val url = result.response.redirectedUrl!!
        val id = url.substring(url.lastIndexOf('/') + 1).toLong()
        val person = personDto.toModelWithId(id)
        // Verify
        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(person))
                }
            }
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD update existing person WHEN put request with dto sent`() {

        // Add initial person
        val personDto = TestUtils.createTestPersonDto("John", "Doe", 20)
        val id = personRepository.save(personDto.toModel()).id

        // Update the person
        val updatePersonDto = TestUtils.createTestPersonDto("Johnny", "Doer", 21)
        mockMvc
            .put("$baseUrl/persons/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatePersonDto)
            }.andExpect {
                status { isOk() }
            }

        // Verify
        val updatedPerson = updatePersonDto.toModelWithId(id)
        mockMvc.get("$baseUrl/persons/$id")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(updatedPerson))
                }
            }
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULD fetch person by id WHEN get request with path variable sent`() {

        // Add a person
        val personDto = TestUtils.createTestPersonDto("Test", "Person")
        val id = personRepository.save(personDto.toModel()).id!!


        val person = personDto.toModelWithId(id)
        // Verify
        mockMvc.get("$baseUrl/persons/$id")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(person))
                }
            }

        val nextId = id + 1
        mockMvc.get("$baseUrl/persons/$nextId")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @WithMockUser(roles = [UserRoles.ADMIN_ROLE])
    fun `SHOULT delete person by id WHEN delete request with path variable sent`() {

        // Add a person
        val personDto = TestUtils.createTestPersonDto("Test", "Person")
        val id = personRepository.save(personDto.toModel()).id


        mockMvc.get("$baseUrl/persons/$id")
            .andExpect {
                status { isOk() }
            }

        // Verify
        mockMvc.delete("$baseUrl/persons/$id")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$baseUrl/persons/$id")
            .andExpect {
                status { isNotFound() }
            }

    }

    @ParameterizedTest
    @ValueSource(strings = [UserRoles.ADMIN_ROLE, UserRoles.USER_ROLE])
    fun `SHOULD list persons for specific roles WHEN get request to list endpoint sent`(userRole: String) {

        // Add initial persons
        for (i in 1..3) {
            val person = TestUtils.createTestPersonDto("Name_$i", "Test").toModelWithId(i.toLong())
            personRepository.save(person)
        }

        // Verify
        mockMvc.perform(MockMvcRequestBuilders.get("$baseUrl/list").with(user("test").roles(userRole)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("content[0].name").value("Name_1" ))
            .andExpect(jsonPath("content[1].name").value("Name_2" ))
            .andExpect(jsonPath("content[2].name").value("Name_3" ))

            .andExpect(jsonPath("page.size").value( 20 ))
            .andExpect(jsonPath("page.number").value( 0 ))
            .andExpect(jsonPath("page.totalElements").value( 3 ))
            .andExpect(jsonPath("page.totalPages").value( 1 ))
    }

    @Test
    fun `SHOULD restrict access to endpoints WHEN user is not provided`() {

        mockMvc.get("$baseUrl/persons")
            .andExpect {
                status { isUnauthorized() }
            }

        mockMvc.get("$baseUrl/list")
            .andExpect {
                status { isUnauthorized() }
            }

        mockMvc.get("$baseUrl/persons/1")
            .andExpect {
                status { isUnauthorized() }
            }

        mockMvc.delete("$baseUrl/persons/1")
            .andExpect {
                status { isUnauthorized() }
            }

        val testPerson = TestUtils.createTestPersonDto("Test", "Person")

        mockMvc.post("$baseUrl/persons") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testPerson)
        }
            .andExpect {
                status { isUnauthorized() }
            }

        mockMvc.put("$baseUrl/persons/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testPerson)
        }
            .andExpect {
                status { isUnauthorized() }
            }
    }
}