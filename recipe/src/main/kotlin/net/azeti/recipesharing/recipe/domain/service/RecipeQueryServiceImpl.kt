package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeQueryServiceImpl(
    private val recipeRepository: RecipeRepository,
) : RecipeQueryService, RecipeSearchService {
    override fun findRandom(): Recipe? = recipeRepository.findRandom()?.toDomain()

    override fun findById(recipeId: Long): Recipe? = recipeRepository.findByIdAndFetchAll(recipeId)?.toDomain()

    override fun findByUser(owner: CustomUserDetails): List<Recipe> = recipeRepository.findByUsername(owner.username).map { it.toDomain() }

    override fun findByNoBackingRequired(): Recipe? = recipeRepository.findByNoBackingRequired()?.toDomain()

    override fun findByNoFrozenIngredients(): Recipe? = recipeRepository.findByNoFrozenIngredients()?.toDomain()

    override fun search(
        username: String?,
        title: String?,
    ): List<Recipe> {
        return recipeRepository.search(username, title).map { it.toDomain() }
    }
}