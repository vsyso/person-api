package ua.dp.syso.person.controller

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import ua.dp.syso.person.dto.PersonDto
import ua.dp.syso.person.model.Person
import ua.dp.syso.person.repository.PersonRepository
import ua.dp.syso.person.specification.PersonSpecification.Companion.personAgeEquals
import ua.dp.syso.person.specification.PersonSpecification.Companion.personNameLike

/**
 * Person API v1 controller
 * Supports basic CRUD operations with access limited to specific roles (see ./config/WebSecurityConfiguration)
 */
@RestController
@OpenAPIDefinition(info = Info(title = "Person API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
@RequestMapping("/api/v1", produces = [MediaType.APPLICATION_JSON_VALUE])
class PersonController(private val personRepository: PersonRepository,
                       private val passwordEncoder: PasswordEncoder) {

    var sensitiveDataFilter: SimpleFilterProvider = SimpleFilterProvider()
        .addFilter("sensitiveDataFilter",
            SimpleBeanPropertyFilter.serializeAllExcept("password", "username"))

    /**
     * List persons with pagination
     */
    @GetMapping("/persons")
    fun fetchPersons(
        @SortDefault(sort = ["id"], direction = Sort.Direction.ASC)
        @PageableDefault(size = 20) pageable: Pageable )
    : ResponseEntity<Page<Person>> {

        val page = personRepository.findAll(pageable)
        if (page.isEmpty) {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.ok(page)
    }

    /**
     * Add new Person entity
     */
    @PostMapping("/persons")
    @ResponseStatus(HttpStatus.CREATED)
    fun addPerson(@Valid @RequestBody personDto: PersonDto,
                  uri: UriComponentsBuilder,
                  request: HttpServletRequest
    ): ResponseEntity<Person> {

        val person = Person(
            personDto.name,
            personDto.surname,
            personDto.email,
            personDto.phone,
            personDto.dateOfBirth,
            personDto.username,
            passwordEncoder.encode(personDto.password))

        val persistedPerson = personRepository.save(person)
        if (ObjectUtils.isEmpty(persistedPerson)) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        val headers = HttpHeaders()
        headers.location =  uri.path("${request.requestURI}/{personId}")
            .buildAndExpand(person.id).toUri()
        return ResponseEntity(headers, HttpStatus.CREATED)
    }

    /**
     * Update Person entity by its id
     */
    @PutMapping("/persons/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePersonById(@PathVariable("id") personId: Long, @Valid @RequestBody personDto: PersonDto): ResponseEntity<Person> {
        val storedPerson = personRepository.findById(personId)

        return storedPerson.map {
            val updatedPerson = it.copy(
                name = personDto.name,
                surname = personDto.surname,
                email = personDto.email,
                phone = personDto.phone,
                dateOfBirth = personDto.dateOfBirth,
                username = personDto.username,
                password = passwordEncoder.encode(personDto.password)
            )
            ResponseEntity(personRepository.save(updatedPerson), HttpStatus.OK)
        }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    /**
     * Find Person entity by id
     */
    @GetMapping("/persons/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun fetchPersonById(@PathVariable("id") personId: Long): ResponseEntity<Person> {
        val person = personRepository.findById(personId)
        if (person.isPresent) {
            return ResponseEntity(person.get(), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * Delete Person entity by id
     */
    @DeleteMapping("/persons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePersonById(@PathVariable("id") personId: Long): ResponseEntity<Unit> {
        val person = personRepository.findById(personId)
        if (person.isPresent) {
            personRepository.deleteById(personId)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/list")
    fun listPersons(@SortDefault(sort = ["id"], direction = Sort.Direction.ASC)
                    @PageableDefault(size = 20) pageable: Pageable,
                    @RequestParam(required = false) name: String?,
                    @RequestParam(required = false) age: Short?)
    : ResponseEntity<MappingJacksonValue> {

        val spec = personNameLike(name)
            .and(personAgeEquals(age))

        val page = personRepository.findAll(spec, pageable)
        if (page.isEmpty) {
            return ResponseEntity.noContent().build()
        }

        // We don't want to show username and password here
        val filteredValue = MappingJacksonValue(page)
        filteredValue.filters = sensitiveDataFilter

        return ResponseEntity(filteredValue, HttpStatus.OK)
    }
}