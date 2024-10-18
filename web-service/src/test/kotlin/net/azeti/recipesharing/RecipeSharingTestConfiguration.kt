package net.azeti.recipesharing

import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.user.domain.port.UserRepository
import net.azeti.recipesharing.user.infra.security.SecurityJwtProperties
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
    ) = UserTestHelper(userRepository, passwordEncoder)

    @Bean
    fun recipeTestHelper(recipeRepository: RecipeRepository) = RecipeTestHelper(recipeRepository)
}
