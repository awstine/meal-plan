package com.example.mealplanapp.ui.theme.screen.health.meal

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.R
import com.example.mealplanapp.data.entity.Meal
import com.example.mealplanapp.di.DependencyContainer
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import com.example.mealplanapp.ui.theme.screen.health.MealItemCard


@Composable
fun MealPlanScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = viewModel(factory = DependencyContainer.provideHealthConditionViewModelFactory())
) {
    val meals by viewModel.allMeals.collectAsState()
    val currentBreakfast by viewModel.currentBreakfast
    val currentLunch by viewModel.currentLunch
    val currentSupper by viewModel.currentSupper
    val toastMessage by viewModel.toastMessage
    val context = LocalContext.current

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
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Your Meal Plan",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Meal cards section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentBreakfast?.let { meal ->
                ClickableMealCard(
                    time = "Breakfast",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", "),
                    onClick = { /* Handle click */ }
                )
            }

            currentLunch?.let { meal ->
                ClickableMealCard(
                    time = "Lunch",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", "),
                    onClick = { /* Handle click */ }
                )
            }

            currentSupper?.let { meal ->
                ClickableMealCard(
                    time = "Dinner",
                    meal = meal.name,
                    ingredients = meal.ingredients.joinToString(", "),
                    onClick = { /* Handle click */ }
                )
            }

            if (currentBreakfast == null && currentLunch == null && currentSupper == null) {
                if (meals.isEmpty()) {
                    Text(
                        text = "Loading meals...",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Text(
                        text = "No meal plan generated yet",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.navigate("customGoal") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate New Meal Plan")
            }

            OutlinedButton(
                onClick = { navController.navigate("savedMeals") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Saved Meals")
            }
        }
    }
}

@Composable
fun ClickableMealCard(
    time: String,
    meal: String,
    ingredients: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = meal,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = ingredients,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}