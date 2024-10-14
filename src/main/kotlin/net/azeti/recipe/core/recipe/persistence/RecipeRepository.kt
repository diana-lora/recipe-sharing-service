package net.azeti.recipe.core.recipe.persistence

import jakarta.persistence.criteria.Predicate
import net.azeti.recipe.core.user.persistence.UserEntity
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<RecipeEntity, Long>, JpaSpecificationExecutor<RecipeEntity> {
    fun existsByUserIdAndTitle(
        userId: Long,
        title: String,
    ): Boolean

    @Query("SELECT r FROM RecipeEntity r LEFT JOIN FETCH r.ingredients JOIN FETCH r.user WHERE r.user.id = :userId")
    fun findByUserId(userId: Long): List<RecipeEntity>

    @Query("SELECT r FROM RecipeEntity r LEFT JOIN FETCH r.ingredients JOIN FETCH r.user WHERE r.id = :id")
    fun findByIdAndFetchAll(id: Long): RecipeEntity?

    companion object {
        fun search(
            username: String?,
            title: String?,
        ): Specification<RecipeEntity> {
            return Specification { root, _, criteriaBuilder ->
                val userJoin = root.join<RecipeEntity, UserEntity>("user")
                root.fetch<RecipeEntity, IngredientEntity>("ingredients")
                val predicates = mutableListOf<Predicate>()
                username?.let { predicates.add(criteriaBuilder.like(userJoin.get<String>("username"), "%$it%")) }
                title?.let {
                    it.split("+").forEach { word -> predicates.add(criteriaBuilder.like(root.get<String>("title"), "%$word%")) }
                }
                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }

        fun hasInstructionsNotLike(words: List<String>): Specification<RecipeEntity> {
            return Specification { root, _, criteriaBuilder ->
                root.fetch<RecipeEntity, IngredientEntity>("ingredients")
                root.fetch<RecipeEntity, UserEntity>("user")
                criteriaBuilder.and(*words.map { criteriaBuilder.notLike(criteriaBuilder.lower(root.get<String>("instructions")), "%$it%") }.toTypedArray())
            }
        }

        fun hasIngredientsTypeNotLike(words: List<String>): Specification<RecipeEntity> {
            return Specification { root, _, criteriaBuilder ->
                val ingredientJoin = root.join<RecipeEntity, IngredientEntity>("ingredients")
                root.fetch<RecipeEntity, UserEntity>("user")
                criteriaBuilder.and(*words.map { criteriaBuilder.notLike(criteriaBuilder.lower(ingredientJoin.get<String>("type")), "%$it%") }.toTypedArray())
            }
        }
    }
}

@Repository
interface IngredientRepository : JpaRepository<IngredientEntity, Long> {
    @Modifying
    @Query("DELETE FROM IngredientEntity i WHERE i.recipeId = :recipeId")
    fun deleteByRecipeId(recipeId: Long)
}
