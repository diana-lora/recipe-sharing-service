package net.azeti.challenge.recipe.recipe

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import net.azeti.challenge.recipe.user.UserEntity

@Entity
@Table(name = "recipes")
class RecipeEntity(
    var title: String,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity,
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
    val unit: IngredientUnits,
    val type: String,
    @Column(name = "recipe_id")
    val recipeId: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
)

enum class IngredientUnits {
    GRAM,
    KILOGRAM,
    MILLILITER,
    LITER,
    PIECE,
    TEASPOON,
    TABLESPOON,
    A_DASH,
}
