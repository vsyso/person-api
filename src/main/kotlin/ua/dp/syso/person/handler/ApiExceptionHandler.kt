package ua.dp.syso.person.handler

import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Handles API exceptions
 */
@ControllerAdvice
class ApiExceptionHandler: ResponseEntityExceptionHandler() {

    override fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val apiError = ApiError(
            status = HttpStatus.NOT_FOUND,
            message = "No resource found",
            errors = listOf("Resource ${ex.resourcePath} doesn't exist"))
        return ResponseEntity(apiError, apiError.status)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val apiError = ApiError(
            status = HttpStatus.NOT_FOUND,
            message = "No handler found",
            errors = listOf("Method ${ex.httpMethod} is not supported"))
        return ResponseEntity(apiError, apiError.status)
    }

    @ExceptionHandler( value = [Exception::class])
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<Any> {

        val apiError = if (ex is PropertyReferenceException) {
            ApiError(
                status = HttpStatus.BAD_REQUEST,
                message = "Invalid argument",
                errors = listOf(ex.localizedMessage))
        } else {
            ApiError(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                message = "An error occurred",
                errors = listOf("The error has been reported"))
            // log ex.localizedMessage
        }

        return ResponseEntity(apiError, apiError.status)
    }
}
