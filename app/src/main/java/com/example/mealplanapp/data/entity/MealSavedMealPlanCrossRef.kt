package com.example.mealplanapp.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation


@Entity(
    tableName = "meal_saved_meal_plan_cross_ref",
    primaryKeys = ["savedMealPlanId", "mealId"],
    foreignKeys = [
        ForeignKey(
            entity = SavedMealPlan::class,
            parentColumns = ["id"],
            childColumns = ["savedMealPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("savedMealPlanId"),
        Index("mealId")
    ]
)

data class MealSavedMealPlanCrossRef(
    val savedMealPlanId: Int,
    val mealId: Int
)

//@Entity(primaryKeys = ["savedMealPlanId", "mealId"])
//data class MealSavedMealPlanCrossRef(
//    val savedMealPlanId: Int,
//    val mealId: Int
//)


//data class MealPlanWithMeals(
//    @Embedded val savedMealPlan: SavedMealPlan,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id",
//        associateBy = Junction(MealSavedMealPlanCrossRef::class)
//    )
//    val meals: List<Meal>
//)