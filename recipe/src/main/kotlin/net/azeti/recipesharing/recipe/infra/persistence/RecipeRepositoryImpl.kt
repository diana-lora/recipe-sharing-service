package net.azeti.recipesharing.recipe.infra.persistence

import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RecipeRepositoryImpl(
    private val recipeJpaRepo: RecipeJpaRepository,
    private val ingredientJpaRepo: IngredientRepository,
) : RecipeRepository {
    override fun findByUsername(username: String): List<RecipeAggregate> {
        return recipeJpaRepo.findByUsername(username).map { it.toAggregate() }
    }

    override fun findRandom(): RecipeAggregate? = recipeJpaRepo.findRandom()?.toAggregate()

    override fun findByIdAndFetchAll(id: Long): RecipeAggregate? = recipeJpaRepo.findByIdAndFetchAll(id)?.toAggregate()

    override fun search(
        username: String?,
        title: String?,
    ): List<RecipeAggregate> = recipeJpaRepo.findAll(RecipeJpaRepository.search(username, title)).map { it.toAggregate() }

    @Transactional
    override fun save(aggregate: RecipeAggregate): RecipeAggregate {
        val recipeEntity =
            recipeJpaRepo.save(
                RecipeEntity(
                    title = aggregate.title,
                    username = aggregate.username,
                    instructions = aggregate.instructions,
                    description = aggregate.description,
                    servings = aggregate.servings,
                ),
            )
        recipeEntity.ingredients = aggregate.ingredients.map { it.toEntity(recipeEntity.id) }
        ingredientJpaRepo.saveAll(recipeEntity.ingredients)
        return recipeEntity.toAggregate()
    }

    @Transactional
    override fun update(aggregate: RecipeAggregate): RecipeAggregate {
        ingredientJpaRepo.deleteByRecipeId(aggregate.id)
        val recipeEntity =
            recipeJpaRepo.save(
                RecipeEntity(
                    title = aggregate.title,
                    username = aggregate.username,
                    instructions = aggregate.instructions,
                    description = aggregate.description,
                    servings = aggregate.servings,
                    id = aggregate.id,
                ),
            )
        recipeEntity.ingredients = aggregate.ingredients.map { it.toEntity(recipeEntity.id) }
        ingredientJpaRepo.saveAll(recipeEntity.ingredients)
        return recipeEntity.toAggregate()
    }

    @Transactional
    override fun delete(recipeId: Long) {
        ingredientJpaRepo.deleteByRecipeId(recipeId)
        recipeJpaRepo.deleteById(recipeId)
    }

    override fun findByNoBackingRequired(): RecipeAggregate? = recipeJpaRepo.findByNoBackingRequired()?.toAggregate()

    override fun findByNoFrozenIngredients(): RecipeAggregate? = recipeJpaRepo.findByNoFrozenIngredients()?.toAggregate()
}
