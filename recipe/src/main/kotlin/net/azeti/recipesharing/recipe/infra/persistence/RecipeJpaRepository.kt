package net.azeti.recipesharing.recipe.infra.persistence

import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RecipeJpaRepository : JpaRepository<RecipeEntity, Long>, JpaSpecificationExecutor<RecipeEntity> {
    @Query("SELECT r FROM RecipeEntity r LEFT JOIN FETCH r.ingredients WHERE r.username = :username ORDER BY r.id")
    fun findByUsername(username: String): List<RecipeEntity>

    @Query("SELECT r FROM RecipeEntity r LEFT JOIN FETCH r.ingredients ORDER BY FUNCTION('RANDOM') LIMIT 1")
    fun findRandom(): RecipeEntity?

    @Query("SELECT r FROM RecipeEntity r LEFT JOIN FETCH r.ingredients WHERE r.id = :id")
    fun findByIdAndFetchAll(id: Long): RecipeEntity?

    @Query(
        """
        SELECT r FROM RecipeEntity r
        LEFT JOIN FETCH r.ingredients i
        WHERE LOWER(r.instructions) NOT LIKE '%freezer%' AND LOWER(r.instructions) NOT LIKE '%fridge%'
            and LOWER(i.type) NOT LIKE '%frozen%' AND LOWER(i.type) NOT LIKE '%ice%'
        ORDER BY RANDOM()
        LIMIT 1
    """,
    )
    fun findByNoFrozenIngredients(): RecipeEntity?

    @Query(
        """
        SELECT r FROM RecipeEntity r
        LEFT JOIN FETCH r.ingredients
        WHERE LOWER(r.instructions) NOT LIKE '%bake%' AND LOWER(r.instructions) NOT LIKE '%baking%'
            AND LOWER(r.instructions) NOT LIKE '%oven%' AND LOWER(r.instructions) NOT LIKE '%degrees%'
            AND LOWER(r.instructions) NOT LIKE '%fahrenheit%' AND LOWER(r.instructions) NOT LIKE '%celsius%'
        ORDER BY RANDOM()
        LIMIT 1
    """,
    )
    fun findByNoBackingRequired(): RecipeEntity?

    companion object {
        fun search(
            username: String?,
            title: String?,
        ): Specification<RecipeEntity> {
            return Specification { root, query, criteriaBuilder ->
                query.orderBy(criteriaBuilder.asc(root.get<Long>("id")))
                root.fetch<RecipeEntity, IngredientEntity>("ingredients", JoinType.LEFT)
                val predicates = mutableListOf<Predicate>()
                username?.let {
                    predicates.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("username")),
                            criteriaBuilder.lower(criteriaBuilder.literal("%$it%")),
                        ),
                    )
                }
                title?.let {
                    it.split("+").forEach { word ->
                        predicates.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                criteriaBuilder.lower(criteriaBuilder.literal("%$word%")),
                            ),
                        )
                    }
                }
                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}
