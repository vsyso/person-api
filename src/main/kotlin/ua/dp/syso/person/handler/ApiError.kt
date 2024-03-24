package ua.dp.syso.person.handler

import org.springframework.http.HttpStatus

/**
 * API error response object
 */
data class ApiError(val status: HttpStatus,
                    val message: String,
                    val errors: List<String>)
