package ua.dp.syso.person.handler

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


/**
 * Handles validation errors
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class ValidationHandler: ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {

        val apiError = ApiError(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation error",
            errors = ex.bindingResult.allErrors.stream()
                .filter{ it is FieldError && it.defaultMessage != null }
                .map { (it as FieldError).field + ": " + it.defaultMessage!! }
                .toList())

        return ResponseEntity(apiError, apiError.status)
    }
}
