package net.azeti.recipesharing.infra.api.exception

import net.azeti.recipesharing.core.exceptions.ActionNotAllowedException
import net.azeti.recipesharing.core.exceptions.BusinessException
import net.azeti.recipesharing.core.exceptions.ConflictException
import net.azeti.recipesharing.core.exceptions.DuplicateUserException
import net.azeti.recipesharing.core.exceptions.ExternalServiceException
import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.core.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ConflictException::class, DuplicateUserException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.CONFLICT)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidParameterException::class)
    fun handleInvalidParameterException(e: InvalidParameterException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.BAD_REQUEST)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorApi> {
        val codes = e.allErrors.joinToString { it.defaultMessage?.toString() ?: "" }
        return ResponseEntity(ErrorApi(code = codes, message = e.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ActionNotAllowedException::class)
    fun handleActionNotAllowedException(e: ActionNotAllowedException): ResponseEntity<ErrorApi> {
        return ResponseEntity(HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(UserNotFoundException::class, RecipeNotFoundException::class)
    fun handleNotFoundException(e: BusinessException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalServiceException(e: ExternalServiceException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.BAD_GATEWAY)
    }
}

data class ErrorApi(val code: String, val message: String?)
