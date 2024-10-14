package net.azeti.recipe.recipe

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.azeti.recipe.api.exception.DuplicateRecipeException
import net.azeti.recipe.helpers.defaultCustomUserDetails
import net.azeti.recipe.helpers.defaultRecipeRequest
import net.azeti.recipe.recipe.persistence.IngredientRepository
import net.azeti.recipe.recipe.persistence.RecipeEntity
import net.azeti.recipe.recipe.persistence.RecipeRepository
import net.azeti.recipe.user.persistence.UserEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RecipeServiceImplTest {
    private val recipeRepository: RecipeRepository = mockk()
    private val ingredientRepository: IngredientRepository = mockk()
    private val service = RecipeServiceImpl(recipeRepository, ingredientRepository)

    @Test
    fun `User creates a recipe, but the title is taken, then throw duplicate exception`() {
        val recipeRequest = defaultRecipeRequest()
        val user = defaultCustomUserDetails()

        every { recipeRepository.existsByUserIdAndTitle(any(), any()) } returns true

        val exception = assertThrows<DuplicateRecipeException> { service.create(recipeRequest, user) }

        exception.code shouldBe "recipe.duplicate"
        exception.message shouldBe "Recipe ${recipeRequest.title} already exists"

        verify { recipeRepository.existsByUserIdAndTitle(user.id, recipeRequest.title) }
    }

    @Test
    fun `User updates a recipe, but the title is taken, then throw duplicate exception`() {
        val recipeRequest = defaultRecipeRequest()
        val user = defaultCustomUserDetails()

        every { recipeRepository.findByIdAndFetchAll(any()) } returns
            RecipeEntity(
                id = 1L,
                title = "Title",
                description = "Description",
                instructions = "Instructions",
                servings = 1,
                user = UserEntity(id = user.id, username = user.username, email = user.email, password = user.password),
            )
        every { recipeRepository.existsByUserIdAndTitle(any(), any()) } returns true

        val exception = assertThrows<DuplicateRecipeException> { service.update(1L, recipeRequest) }

        exception.code shouldBe "recipe.duplicate"
        exception.message shouldBe "Recipe ${recipeRequest.title} already exists"

        verify {
            recipeRepository.findByIdAndFetchAll(1L)
            recipeRepository.existsByUserIdAndTitle(user.id, recipeRequest.title)
        }
    }

    @Test
    fun `User updates a recipe but it doesn't exist, return null`() {
        val recipeRequest = defaultRecipeRequest()
        val user = defaultCustomUserDetails()

        every { recipeRepository.findByIdAndFetchAll(any()) } returns null

        service.update(1L, recipeRequest).shouldBeNull()

        verify { recipeRepository.findByIdAndFetchAll(1L) }
    }
}
