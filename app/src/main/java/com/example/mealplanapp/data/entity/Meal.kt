package com.example.mealplanapp.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.example.mealplanapp.data.converters.Converters
import java.time.LocalDate
import java.time.LocalDateTime

// model/Meal.kt
//@Entity(tableName = "meals")
//@TypeConverters(Converters::class)
//data class MealEntity(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val name: String,
//    val imageResId: Int, // Reference to drawable resource
//    val description: String,
//    val category: String, // Breakfast, Lunch, Dinner
//    val calories: Int,
//    val ingredients: List<String>,
//    val instructions: String = ""
//)

//@Entity(tableName = "meal_plans")
//data class MealPlanEntity(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary Key
//    val mealIds: List<Int>
//)
//
//
//data class MealPlanWithMeals(
//    @Embedded val mealPlan: SavedMealPlan,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "mealPlanId"
//    )
//    val meals: List<Meal>
//)


@Entity(tableName = "meals")
@TypeConverters(Converters::class)
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val imageResId: Int, // Reference to drawable resource
    val description: String,
    val category: String, // Breakfast, Lunch, Dinner
    val calories: Int,
    val ingredients: List<String>,
    val instructions: String = "",
   // val mealPlanId: Int? = null
)


@Entity(tableName = "saved_meal_plan")
@TypeConverters(Converters::class) // Add TypeConverter for LocalDate
data class SavedMealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate, // Requires TypeConverter
    val breakfastId: Int,
    val lunchId: Int,
    val supperId: Int
)

data class MealPlanDetails(
    val savedMealPlan: SavedMealPlan,
    val breakfast: Meal?, // Nullable in case a meal was deleted
    val lunch: Meal?,
    val supper: Meal?
)

//data class MealPlanWithMeals(
//    @Embedded val mealPlan: SavedMealPlan,
//    @Relation(
//        parentColumn = "breakfastId",
//        entityColumn = "id"
//    )
//    val breakfast: Meal?,
//    @Relation(
//        parentColumn = "lunchId",
//        entityColumn = "id"
//    )
//    val lunch: Meal?,
//    @Relation(
//        parentColumn = "supperId",
//        entityColumn = "id"
//    )
//    val supper: Meal?
//)

data class MealPlanWithMeals(
    @Embedded val mealPlan: SavedMealPlan,
    @Relation(
        parentColumn = "breakfastId",
        entityColumn = "id"
    )
    val breakfast: Meal?,
    @Relation(
        parentColumn = "lunchId",
        entityColumn = "id"
    )
    val lunch: Meal?,
    @Relation(
        parentColumn = "supperId",
        entityColumn = "id"
    )
    val supper: Meal?
)