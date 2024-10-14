package net.azeti.recipe

import net.azeti.recipe.config.props.SecurityJwtProperties
import net.azeti.recipe.core.recipe.persistence.IngredientRepository
import net.azeti.recipe.core.recipe.persistence.RecipeRepository
import net.azeti.recipe.core.user.persistence.UserRepository
import net.azeti.recipe.helpers.RecipeTestHelper
import net.azeti.recipe.helpers.UserTestHelper
import net.azeti.recipe.security.JwtService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@EnableConfigurationProperties(SecurityJwtProperties::class)
@TestConfiguration
class RecipeSharingTestConfiguration {
    @Bean
    fun userTestHelper(
        userRepository: UserRepository,
        passwordEncoder: BCryptPasswordEncoder,
        jwtService: JwtService,
    ) = UserTestHelper(userRepository, passwordEncoder, jwtService)

    @Bean
    fun recipeTestHelper(
        userTestHelper: UserTestHelper,
        recipeRepository: RecipeRepository,
        ingredientRepository: IngredientRepository,
    ) = RecipeTestHelper(userTestHelper, recipeRepository, ingredientRepository)
}
