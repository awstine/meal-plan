package com.example.mealplanapp.ui.theme.screen.health.meal

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.di.DependencyContainer
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import com.example.mealplanapp.ui.theme.screen.health.MealItemCard


@Composable
fun MealPlanScreen(
    navController: NavController,
    // Use the factory provided by your DI container
    viewModel: HealthConditionViewModel = viewModel(factory = DependencyContainer.provideHealthConditionViewModelFactory())
) {
    // State collection remains the same
    val meals by viewModel.allMeals.collectAsState() // Observe all meals if needed elsewhere
    val currentBreakfast by viewModel.currentBreakfast
    val currentLunch by viewModel.currentLunch
    val currentSupper by viewModel.currentSupper
    val toastMessage by viewModel.toastMessage

    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage// Use a dedicated function to clear the message
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Meal Plan", // This shows the plan managed by HealthConditionViewModel
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the meal plan currently held by HealthConditionViewModel
        if (currentBreakfast != null || currentLunch != null || currentSupper != null) {
            currentBreakfast?.let { meal ->
                MealItemCard(
                    time = "Breakfast",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", ")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            currentLunch?.let { meal ->
                MealItemCard(
                    time = "Lunch",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", ")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            currentSupper?.let { meal ->
                MealItemCard(
                    time = "Supper",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", ")
                )
            }
        } else if (meals.isEmpty()){
            Text(
                text = "Loading meals...", // Indicate loading or empty state
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
            )
        }
        else {
            Text(
                text = "No meal plan generated or saved yet.", // More accurate message
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // --- MODIFIED BUTTON ---
        Button(
            onClick = {
                // ONLY NAVIGATE to the screen where the user enters the goal
                navController.navigate("customGoal")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            // More descriptive text
            Text("Generate New Meal Plan Based on Goal")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- REMOVED SAVE BUTTON ---
        // The "Save This Meal Plan" button is removed from here.
        // Saving will happen on the CustomGoalInputScreen after generation.

        OutlinedButton(
            onClick = { navController.navigate("savedMeals") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Saved Meals")
        }
    }
}