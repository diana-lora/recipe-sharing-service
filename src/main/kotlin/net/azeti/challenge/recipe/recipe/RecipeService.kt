package net.azeti.challenge.recipe.recipe

import net.azeti.challenge.recipe.api.exception.DuplicateRecipeException
import net.azeti.challenge.recipe.api.recipe.dto.IngredientResponse
import net.azeti.challenge.recipe.api.recipe.dto.RecipeRequest
import net.azeti.challenge.recipe.api.recipe.dto.RecipeResponse
import net.azeti.challenge.recipe.api.recipe.dto.toApi
import net.azeti.challenge.recipe.api.recipe.dto.toEnum
import net.azeti.challenge.recipe.extensions.expectTrueOr
import net.azeti.challenge.recipe.user.UserEntity
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface RecipeService {
    fun create(
        recipeRequest: RecipeRequest,
        user: UserEntity,
    ): RecipeResponse

    fun findById(id: Long): RecipeResponse?

    fun update(
        id: Long,
        recipeRequest: RecipeRequest,
    ): RecipeResponse?

    fun delete(id: Long)

    fun findByUser(userId: Long): List<RecipeResponse>

    fun existsById(id: Long): Boolean

    fun findByNoBackingRequired(): List<RecipeResponse>

    fun findByNoFrozenIngredients(): List<RecipeResponse>
}

@Service
class RecipeServiceImpl(
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
) : RecipeService {
    @Transactional
    override fun create(
        request: RecipeRequest,
        user: UserEntity,
    ): RecipeResponse {
        expectTrueOr(!recipeRepository.existsByUserIdAndTitle(user.id, request.title)) { throw DuplicateRecipeException(request.title) }
        val recipe =
            RecipeEntity(
                title = request.title,
                description = request.description,
                user = user,
                instructions = request.instructions,
                servings = request.servings,
            )

        recipeRepository.save(recipe)
        recipe.ingredients =
            request.ingredients.map {
                IngredientEntity(
                    recipeId = recipe.id,
                    value = it.value,
                    unit = it.unit.toEnum(),
                    type = it.type,
                )
            }
        ingredientRepository.saveAll(recipe.ingredients)
        return recipe.toApi()
    }

    override fun findById(id: Long): RecipeResponse? {
        val recipe = recipeRepository.findByIdAndFetchAll(id) ?: return null
        return recipe.toApi()
    }

    @Transactional
    override fun update(
        recipeId: Long,
        request: RecipeRequest,
    ): RecipeResponse? {
        val recipe = recipeRepository.findByIdAndFetchAll(recipeId) ?: return null
        expectTrueOr(!recipeRepository.existsByUserIdAndTitle(recipe.user.id, request.title)) { throw DuplicateRecipeException(request.title) }

        ingredientRepository.deleteByRecipeId(recipeId)

        recipe.title = request.title
        recipe.description = request.description
        recipe.instructions = request.instructions
        recipe.servings = request.servings

        recipe.ingredients =
            request.ingredients.map {
                IngredientEntity(
                    recipeId = recipe.id,
                    value = it.value,
                    unit = it.unit.toEnum(),
                    type = it.type,
                )
            }

        return recipe.toApi()
    }

    override fun existsById(id: Long): Boolean = recipeRepository.existsById(id)

    @Transactional
    override fun delete(id: Long) {
        ingredientRepository.deleteByRecipeId(id)
        recipeRepository.deleteById(id)
    }

    override fun findByUser(userId: Long): List<RecipeResponse> = recipeRepository.findByUserId(userId).map { it.toApi() }

    override fun findByNoBackingRequired(): List<RecipeResponse> =
        recipeRepository.findAll(
            RecipeRepository.hasInstructionsNotLike(listOf("bake", "baking", "oven", "degrees", "fahrenheit", "celsius")),
        )
            .map { it.toApi() }

    override fun findByNoFrozenIngredients(): List<RecipeResponse> =
        recipeRepository.findAll(
            Specification
                .where(RecipeRepository.hasIngredientsTypeNotLike(listOf("frozen", "ice")))
                .and(RecipeRepository.hasInstructionsNotLike(listOf("freezer", "fridge"))),
        )
            .map { it.toApi() }
}

fun RecipeEntity.toApi() =
    RecipeResponse(
        id = "${user.username}_$id",
        title = title,
        description = description,
        ingredients =
            ingredients.map { ingredient ->
                IngredientResponse(
                    value = ingredient.value,
                    unit = ingredient.unit.toApi(),
                    type = ingredient.type,
                )
            },
        instructions = instructions,
        servings = servings,
        username = user.username,
    )
