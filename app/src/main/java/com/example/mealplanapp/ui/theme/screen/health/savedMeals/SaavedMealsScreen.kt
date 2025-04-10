package com.example.mealplanapp.ui.theme.screen.health.savedMeals

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.data.converters.DateTimeUtils
import com.example.mealplanapp.data.entity.MealPlanDetails
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMealsScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = hiltViewModel()
) {
    val savedMealPlans by viewModel.allSavedMealPlansWithDetails.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Meal Plans") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (savedMealPlans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No saved meal plans yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(savedMealPlans) { plan ->
                    SavedMealPlanItem(
                        mealPlanDetails = plan,
                        onDelete = { viewModel.deleteMealPlan(plan.savedMealPlan) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedMealPlanItem(
    mealPlanDetails: MealPlanDetails, // Accept MealPlanDetails
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- DISPLAY THE GOAL ---
            Text(
                // Access goal from the nested savedMealPlan object
                text = "Goal: ${mealPlanDetails.savedMealPlan.goal}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Make it stand out
                modifier = Modifier.padding(bottom = 4.dp) // Add space below the goal
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    // Access date from the nested savedMealPlan
                    text = DateTimeUtils.formatLocalDate(mealPlanDetails.savedMealPlan.date), // Make sure DateTimeUtils handles LocalDate
                    style = MaterialTheme.typography.bodyLarge // Adjusted style slightly for hierarchy
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            // Use AnimatedVisibility for smoother expand/collapse
            androidx.compose.animation.AnimatedVisibility(visible = expanded) {
                Column { // Wrap details in a column for AnimatedVisibility
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider() // Add a divider
                    Spacer(modifier = Modifier.height(12.dp))
                    MealPlanDetail(
                        // Safely access details from nullable Meal objects
                        // Use elvis operator ?: to provide default empty strings
                        breakfastName = mealPlanDetails.breakfast?.name ?: "N/A",
                        breakfastIngredients = mealPlanDetails.breakfast?.ingredients?.joinToString(", ") ?: "-",
                        lunchName = mealPlanDetails.lunch?.name ?: "N/A",
                        lunchIngredients = mealPlanDetails.lunch?.ingredients?.joinToString(", ") ?: "-",
                        supperName = mealPlanDetails.supper?.name ?: "N/A",
                        supperIngredients = mealPlanDetails.supper?.ingredients?.joinToString(", ") ?: "-"
                    )
                }
            }
        }
    }
}

@Composable
fun MealPlanDetail(
    breakfastName: String,
    breakfastIngredients: String,
    lunchName: String,
    lunchIngredients: String,
    supperName: String,
    supperIngredients: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Add spacing
        MealDetailItem("Breakfast", breakfastName, breakfastIngredients)
        MealDetailItem("Lunch", lunchName, lunchIngredients)
        MealDetailItem("Supper", supperName, supperIngredients)
    }
}

// Helper composable for cleaner MealPlanDetail
@Composable
fun MealDetailItem(mealType: String, name: String, ingredients: String) {
    Column {
        Text(mealType, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        Text(name, style = MaterialTheme.typography.bodyMedium)
        if (ingredients.isNotBlank() && ingredients != "-") {
            IngredientsList(ingredients)
        } else {
            Text("Ingredients: -", style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun IngredientsList(ingredients: String) {
    // Handle case where ingredients might be empty or just "-"
    if (ingredients.isBlank() || ingredients == "-") {
        Text("Ingredients: -", style = MaterialTheme.typography.bodySmall)
        return
    }
    Column(modifier = Modifier.padding(top = 2.dp)) {
        Text("Ingredients:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            ingredients.split(",").forEach { ingredient ->
                val trimmed = ingredient.trim()
                if(trimmed.isNotEmpty()){ // Avoid displaying empty bullet points
                    Text(
                        text = "• $trimmed",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 1.dp) // Smaller padding
                    )
                }
            }
        }
    }
}

// --- Add DateTimeUtils ---
// Create DateTimeUtils.kt if needed
// src/main/java/com/example/mealplanapp/data/converters/DateTimeUtils.kt
