package net.azeti.recipesharing.recipe.infra.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "recipes")
class RecipeEntity(
    var title: String,
    val username: String,
    var instructions: String,
    var description: String? = null,
    var servings: Int? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
) {
    @OneToMany
    @JoinColumn(name = "recipe_id", referencedColumnName = "id")
    var ingredients: List<IngredientEntity> = emptyList()
}

@Entity
@Table(name = "ingredients")
class IngredientEntity(
    val value: Double,
    @Enumerated(EnumType.STRING)
    val unit: IngredientEntityUnits,
    val type: String,
    @Column(name = "recipe_id")
    val recipeId: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
)

enum class IngredientEntityUnits {
    GRAM,
    KILOGRAM,
    MILLILITER,
    LITER,
    PIECE,
    TEASPOON,
    TABLESPOON,
    A_DASH,
}
