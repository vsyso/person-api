package ua.dp.syso.person.specification

import org.springframework.data.jpa.domain.Specification
import ua.dp.syso.person.model.Person
import java.time.LocalDate

/**
 * Specifications to filter by Person entity fields
 */
class PersonSpecification {

    companion object {

        /**
         * Filter by name field with partial match
         */
        fun personNameLike(name: String?): Specification<Person> {
            return Specification { root, _, criteriaBuilder ->
                name?.let {
                    criteriaBuilder.like(criteriaBuilder.lower(root["name"]), "%${it.lowercase()}%")
                }
            }
        }

        /**
         * Filter by age
         * As this field is dynamic, calculated from dateOfBirth, the specification
         * is built on calculating minimum and maximum dates when certain age comes
         */
        fun personAgeEquals(age: Short?): Specification<Person> {
            return Specification { root, _, criteriaBuilder ->
                age?.let {
                    val maxDate = LocalDate.now().minusYears(age.toLong())
                    val minDate = maxDate.minusYears(1).plusDays(1)
                    criteriaBuilder.between(root["dateOfBirth"], minDate, maxDate)
                }
            }
        }
    }
}
