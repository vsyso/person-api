package ua.dp.syso.person.model

import com.fasterxml.jackson.annotation.JsonFilter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
@Table(name = "persons")
@JsonFilter("sensitiveDataFilter")
@Schema(hidden = true)
data class Person(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val dateOfBirth: LocalDate,
    @Column(unique = true)
    val username: String,
    val password: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    ) {

    /**
     * Dynamic field to calculate age based on DOB
     */
    fun getAge(): Short {
        return ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now()).toShort()
    }
}
