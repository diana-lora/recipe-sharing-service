package net.azeti.challenge.recipe.api.exception

import net.azeti.challenge.recipe.extensions.FullRecipeId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(ConflictException::class, DuplicateRecipeException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InvalidParameterException::class)
    fun handleInvalidParameterException(e: InvalidParameterException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorApi> {
        val codes = e.allErrors.joinToString { it.defaultMessage?.toString() ?: "" }
        return ResponseEntity(ErrorApi(code = codes, message = e.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<ErrorApi> {
        return ResponseEntity(ErrorApi(code = e.code, message = e.message), HttpStatus.UNAUTHORIZED)
    }

    // TODO create a not found business exception
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

open class BusinessException(val code: String, message: String) : RuntimeException(message)

class UnauthorizedException(message: String) : BusinessException("token.invalid", message)

class DuplicateUserException(code: String, message: String) : ConflictException(code, message)

open class ConflictException(code: String, message: String) : BusinessException(code, message)

class InvalidParameterException(code: String, message: String) : BusinessException(code, message)

class UserNotFoundException(username: String) : BusinessException("user.notFound", "User $username not found")

class RecipeNotFoundException(recipeId: FullRecipeId) : BusinessException("recipe.notFound", "Recipe $recipeId not found")

class DuplicateRecipeException(title: String) : ConflictException("recipe.duplicate", "Recipe $title already exists")

open class ExternalServiceException(service: String, code: String? = null) : BusinessException(code ?: "service.unavailable", "Service $service is unavailable")

class ExternalServiceDataUnavailableException(service: String, code: String = "data.unavailable") : ExternalServiceException(service, code)
