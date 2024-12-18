package net.azeti.recipesharing.recipe.infra.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<IngredientEntity, Long> {
    @Modifying
    @Query("DELETE FROM IngredientEntity i WHERE i.recipeId = :recipeId")
    fun deleteByRecipeId(recipeId: Long)
}
