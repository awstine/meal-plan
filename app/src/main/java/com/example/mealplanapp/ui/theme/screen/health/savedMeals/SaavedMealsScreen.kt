package com.example.mealplanapp.ui.theme.screen.health.savedMeals

import android.text.format.DateUtils.formatDateTime
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.ui.theme.data2.DateTimeUtils
import com.example.mealplanapp.ui.theme.data2.DateTimeUtils.formatDateTime
import com.example.mealplanapp.ui.theme.data2.SavedMealPlan
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMealsScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = hiltViewModel()
) {
    val savedMealPlans by viewModel.allSavedMealPlans.collectAsState(initial = emptyList())

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
                items(savedMealPlans) { mealPlan ->
                    SavedMealPlanItem(mealPlan) {
                        viewModel.deleteMealPlan(mealPlan)
                    }
                    Divider()
                }
            }
        }
    }
}


@Composable
fun SavedMealPlanItem(
    mealPlan: SavedMealPlan,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateTimeUtils.formatDateTime(mealPlan.date),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                MealPlanDetail(
                    breakfastName = mealPlan.breakfastName,
                    breakfastIngredients = mealPlan.breakfastIngredients,
                    lunchName = mealPlan.lunchName,
                    lunchIngredients = mealPlan.lunchIngredients,
                    supperName = mealPlan.supperName,
                    supperIngredients = mealPlan.supperIngredients
                )
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
    Column {
        Text("Breakfast", style = MaterialTheme.typography.bodyLarge)
        Text(breakfastName, style = MaterialTheme.typography.bodyMedium)
        IngredientsList(breakfastIngredients)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Lunch", style = MaterialTheme.typography.bodyLarge)
        Text(lunchName, style = MaterialTheme.typography.bodyMedium)
        IngredientsList(lunchIngredients)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Supper", style = MaterialTheme.typography.bodyLarge)
        Text(supperName, style = MaterialTheme.typography.bodyMedium)
        IngredientsList(supperIngredients)
    }
}

@Composable
fun IngredientsList(ingredients: String) {
    Text("Ingredients:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
    Column(modifier = Modifier.padding(start = 8.dp)) {
        ingredients.split(",").forEach { ingredient ->
            Text(
                text = "• ${ingredient.trim()}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp))
        }
    }
}