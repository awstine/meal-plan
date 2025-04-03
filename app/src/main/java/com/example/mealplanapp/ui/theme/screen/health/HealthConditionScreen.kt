package com.example.mealplanapp.ui.theme.screen.health

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun HealthConditionScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = viewModel()
) {
    val mealPlan by viewModel.mealPlan
    val toastMessage by viewModel.toastMessage
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
            // Clear the message after showing
            viewModel.clearToast()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Meal Plan", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        MealItemCard("Breakfast", mealPlan.breakfast.first, mealPlan.breakfast.second)
        MealItemCard("Lunch", mealPlan.lunch.first, mealPlan.lunch.second)
        MealItemCard("Supper", mealPlan.supper.first, mealPlan.supper.second)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.generateMealPlan() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate New Meal Plan")
        }

        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { navController.navigate("savedMeals") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Saved Meals")
        }
    }
}

@Composable
fun MealItemCard(
    time: String,
    meal: String,
    ingredients: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(8.dp)
            .animateContentSize(),  // Add smooth animation for expansion
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = time, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = meal,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp))
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp))
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingredients:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp))

                // Split ingredients by comma and display each on a new line
                val ingredientList = ingredients.split(",").map { it.trim() }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    ingredientList.forEach { ingredient ->
                        Text(
                            text = "• $ingredient",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}
