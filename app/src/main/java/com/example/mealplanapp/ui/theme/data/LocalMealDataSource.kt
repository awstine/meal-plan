package com.example.mealplanapp.ui.theme.data

import com.example.mealplanapp.R

object LocalMealDataSource {
    private val kenyanMeals = listOf(
        Meal(1, "Ugali & Sukuma Wiki", R.drawable.ugalisukuma, "Kenyan staple food", "Dinner", 350, listOf("Maize flour", "Sukuma wiki")),
        Meal(2, "Chapati", R.drawable.chapati, "Soft Kenyan flatbread", "Breakfast", 280, listOf("Wheat flour", "Oil", "Salt")),
        Meal(3, "Githeri", R.drawable.githeri, "Maize and beans mix", "Lunch", 400, listOf("Maize", "Beans", "Carrots")),
        Meal(4, "Herbal Tea with Nuts", R.drawable.herbal_tea, "Low sugar tea", "Breakfast", 200, listOf("Lemongrass", "Ginger", "Almonds")),
        Meal(5, "Grilled Fish with Vegetables", R.drawable.grilled_fish, "High protein meal", "Lunch", 300, listOf("Tilapia", "Tomatoes", "Capsicum"))
    )

    fun getRandomMealPlan(userCondition: String): Map<String, List<Meal>> {
        val mealsByCategory = kenyanMeals.groupBy { it.category }

        return mapOf(
            "Breakfast" to (mealsByCategory["Breakfast"]?.let { it.shuffled().take(1) } ?: emptyList()),
            "Lunch" to (mealsByCategory["Lunch"]?.let { it.shuffled().take(1) } ?: emptyList()),
            "Dinner" to (mealsByCategory["Dinner"]?.let { it.shuffled().take(1) } ?: emptyList())
        )
    }


    fun getMealsByCategory(category: String): List<Meal> {
        return kenyanMeals.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getAllCategories(): List<String> {
        return listOf("Breakfast", "Lunch", "Dinner")
    }

    fun getMealById(id: Int): Meal? {
        return kenyanMeals.firstOrNull { it.id == id }
    }
    }