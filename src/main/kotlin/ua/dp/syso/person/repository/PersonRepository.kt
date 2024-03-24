package ua.dp.syso.person.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import ua.dp.syso.person.model.Person

@Repository
interface PersonRepository : JpaSpecificationExecutor<Person>, JpaRepository<Person, Long>
