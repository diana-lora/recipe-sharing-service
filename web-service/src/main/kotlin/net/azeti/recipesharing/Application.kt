package net.azeti.recipesharing

import net.azeti.recipesharing.infra.config.VisualCrossingProperties
import net.azeti.recipesharing.recipe.infra.config.RecipeRecommendationProperties
import net.azeti.recipesharing.user.infra.security.SecurityJwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SecurityJwtProperties::class, VisualCrossingProperties::class, RecipeRecommendationProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
