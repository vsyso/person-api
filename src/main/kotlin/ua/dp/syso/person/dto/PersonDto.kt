package ua.dp.syso.person.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * Data transfer object for Person model with validation constrains
 */
data class PersonDto(
    @field:NotBlank
    @field:Size(min = 3, max = 64)
    val name: String,
    @field:NotBlank
    @field:Size(min = 3, max = 64)
    val surname: String,
    @field:Email
    @field:Size(min = 5, max = 320)
    val email: String,
    @field:NotBlank
    @Size(min = 3, max = 32)
    @field:Pattern(regexp = "^([0-9+()#-])+\$", message = "Only digits and symbols like +,-,(,),#")
    val phone: String,
    @field:Past
    val dateOfBirth: LocalDate,
    @field:NotBlank
    @field:Size(min = 3, max = 64, message = "Username should have at least 8 characters")
    val username: String,
    @field:NotBlank
    @field:Size(min = 8, max = 128, message = "Password should have at least 8 symbols")
    val password: String) {
}
