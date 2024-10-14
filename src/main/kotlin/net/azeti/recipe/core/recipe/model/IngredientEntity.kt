package net.azeti.recipe.core.recipe.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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
