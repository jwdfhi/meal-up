package com.jwdfhi.meal_up.models

data class MealListByAreaServiceModel(
    val meals: List<Meal>
) {
    data class Meal(
        val idMeal: String,
        val strMeal: String,
        val strMealThumb: String
    )
}