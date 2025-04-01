package com.example.mealplanapp.ui.theme.data2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun MealPlanScreen(
    navController: NavController,
    viewModel: MealViewModel
) {
    val mealPlan by viewModel.mealPlan.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Enables scrolling
    ) {
        Text(
            text = "Your Meal Plan",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        mealPlan?.forEach { (mealTime, meals) ->
            Text(
                text = mealTime,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    onClick = {
                        viewModel.loadMealById(meal.id)
                        navController.navigate("meal_detail/${meal.id}")
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp)) // Fixed issue
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Health Goals")
        }
    }
}
