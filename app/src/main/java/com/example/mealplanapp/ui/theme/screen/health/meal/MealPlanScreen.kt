package com.example.mealplanapp.ui.theme.screen.health.meal

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import com.example.mealplanapp.ui.theme.screen.health.MealItemCard
import dagger.hilt.android.lifecycle.HiltViewModel


@Composable
fun MealPlanScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = hiltViewModel()
) {
    val mealPlan by viewModel.mealPlan
    val customGoal by viewModel.customGoal

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
                text = "Your Meal Plan",
                style = MaterialTheme.typography.headlineLarge
            )

            if (customGoal.isNotBlank()) {
                FilterChip(
                    selected = true,
                    onClick = { navController.navigate("customGoal") },
                    label = { (customGoal) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit goal"
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (customGoal.isBlank()) {
            Text(
                text = "No health goal set yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(
                onClick = { navController.navigate("customGoal") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Your Health Goal")
            }
        } else {
            // Display meal plan
            MealItemCard("Breakfast", mealPlan.breakfast.first, mealPlan.breakfast.second)
            MealItemCard("Lunch", mealPlan.lunch.first, mealPlan.lunch.second)
            MealItemCard("Supper", mealPlan.supper.first, mealPlan.supper.second)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.generateMealPlan() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Regenerate Meal Plan")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveCurrentMealPlan() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Save This Meal Plan")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.navigate("savedMeals") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Saved Meals")
        }
    }
}