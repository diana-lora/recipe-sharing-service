package net.azeti.recipe.core.recipe.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import net.azeti.recipe.core.user.model.UserEntity

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
