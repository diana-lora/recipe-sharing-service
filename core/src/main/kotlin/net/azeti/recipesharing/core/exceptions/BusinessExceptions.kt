package net.azeti.recipesharing.core.exceptions

open class BusinessException(val code: String, message: String) : RuntimeException(message)

class ActionNotAllowedException(message: String) : BusinessException("action.notAllowed", message)

class DuplicateUserException(code: String, message: String) : ConflictException(code, message)

open class ConflictException(code: String? = null, message: String) : BusinessException(code ?: "state.invalid", message)

class InvalidParameterException(code: String? = null, message: String) : BusinessException(code ?: "parameter.invalid", message)

class UserNotFoundException(username: String) : BusinessException("user.notFound", "User $username not found")

class RecipeNotFoundException(recipeId: Long? = null) : BusinessException("recipe.notFound", "Recipe $recipeId not found")

open class ExternalServiceException(service: String, code: String? = null) : BusinessException(code ?: "service.unavailable", "Service $service is unavailable")

class ExternalServiceDataUnavailableException(service: String, code: String = "data.unavailable") : ExternalServiceException(service, code)
