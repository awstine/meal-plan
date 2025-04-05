package com.example.mealplanapp.ui.theme.screen.health

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Keep existing layout imports
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.* // Keep existing material imports
import androidx.compose.runtime.* // Keep existing runtime imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
// Removed import androidx.lifecycle.viewmodel.compose.viewModel // Use hiltViewModel
import androidx.navigation.NavController
import com.example.mealplanapp.data.entity.Meal // Import Meal

@Composable
fun HealthConditionScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = viewModel()
) {
    // Observe the new state variables for the current plan
    val currentBreakfast by viewModel.currentBreakfast
    val currentLunch by viewModel.currentLunch
    val currentSupper by viewModel.currentSupper

    val toastMessage by viewModel.toastMessage
    val context = LocalContext.current

    // Show toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Add scroll for potentially long content
    ) {
        Text(text = "Today's Meal Plan", style = MaterialTheme.typography.headlineMedium) // Adjusted style

        Spacer(Modifier.height(16.dp))

        // Display current breakfast if available
        currentBreakfast?.let { meal ->
            MealItemCard(
                time = "Breakfast",
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", ")
            )
        } ?: MealItemPlaceholder("Breakfast") // Show placeholder if null

        Spacer(Modifier.height(8.dp)) // Space between cards

        // Display current lunch if available
        currentLunch?.let { meal ->
            MealItemCard(
                time = "Lunch",
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", ")
            )
        } ?: MealItemPlaceholder("Lunch") // Show placeholder if null

        Spacer(Modifier.height(8.dp)) // Space between cards

        // Display current supper if available
        currentSupper?.let { meal ->
            MealItemCard(
                time = "Supper",
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", ")
            )
        } ?: MealItemPlaceholder("Supper") // Show placeholder if null


        Spacer(Modifier.weight(1f)) // Push buttons to the bottom

        // --- Action Buttons ---
        Button(
            onClick = {
                // Call the generate function in ViewModel
                viewModel.generateNewMealPlan()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate New Meal Plan")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                // Call the parameterless save function in ViewModel
                viewModel.saveCurrentMealPlan()
            },
            // Enable button only if a complete plan is generated
            enabled = (currentBreakfast != null && currentLunch != null && currentSupper != null),
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
            onClick = { navController.navigate("savedMeals") }, // Navigate to saved meals screen
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
            // .padding(vertical = 4.dp) // Remove padding here if spacing added outside
            .animateContentSize(), // Animate size changes
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Slightly reduced elevation
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Subtle background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = time, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) // Label style for time
                    Text(
                        text = meal,
                        style = MaterialTheme.typography.titleMedium, // Title style for meal name
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Use AnimatedVisibility for smoother expansion
            androidx.compose.animation.AnimatedVisibility(visible = isExpanded) {
                Column{
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingredients:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    val ingredientList = ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() } // Filter empty strings
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        ingredientList.forEach { ingredient ->
                            Text(
                                text = "• $ingredient",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 1.dp) // Reduced padding
                            )
                        }
                    }
                }
            }
        }
    }
}

// Optional: Placeholder Card when a meal hasn't been generated yet
@Composable
fun MealItemPlaceholder(time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        // .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)) // Dimmed background
    ) {
        Row(modifier = Modifier.padding(16.dp).height(40.dp), // Fixed height for placeholder consistency
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$time: Not generated yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}