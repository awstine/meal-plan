package com.example.mealplanapp.ui.theme.screen.health.goalinput

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.data.model.MealPlan
import com.example.mealplanapp.ui.theme.screen.health.MealItemCard
// ... other imports ...
import androidx.lifecycle.viewmodel.compose.viewModel // Use the standard viewModel function
// import androidx.hilt.navigation.compose.hiltViewModel // REMOVE Hilt import
import com.example.mealplanapp.di.DependencyContainer // Import your container
import com.example.mealplanapp.ui.theme.screen.health.MealItemCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomGoalInputScreen(
    navController: NavController,
    // Use standard viewModel() with the factory from DependencyContainer
    viewModel: CustomGoalInputViewModel = viewModel(
        factory = DependencyContainer.provideCustomGoalInputViewModelFactory()
    ),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    // The rest of the Composable function remains the same...
    val state by viewModel.state.collectAsStateWithLifecycle(lifecycleOwner)
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.error, state.saveSuccess) {
        // ... Toast logic remains the same ...
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... UI elements (Text, TextField, Buttons, DisplayMealPlan) remain the same ...

        Text(
            text = "Enter Your Health Goal",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Examples: 'Lose weight', 'Gain muscle', 'Maintain current weight'",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp)

        )

        OutlinedTextField(
            value = state.goal,
            onValueChange = { viewModel.onEvent(CustomInputEvent.OnGoalChanged(it)) },
            label = { Text("Your health goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // Hide keyboard
                    if (state.goal.isNotBlank()) { // Only submit if goal is not blank
                        viewModel.onEvent(CustomInputEvent.Submit)
                    }
                }
            ),

            isError = state.error != null // Show error state on text field

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                Log.d("CustomGoalInputDebug", "Generate button clicked with goal: ${state.goal}")
                viewModel.onEvent(CustomInputEvent.Submit)
            },
            // ... rest of button ...
        ) {
            Text("Generate Meal Plan")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Display Area ---
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        } else if (state.generatedPlan != null) {
            DisplayMealPlan(planDetails = state.generatedPlan!!)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.saveCurrentMealPlan() },
                // ... rest of save button ...
            ) {
                Text("Save This Meal Plan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

// DisplayMealPlan Composable remains the same

@Composable
fun DisplayMealPlan(planDetails: GeneratedPlanDetails) { // Accept GeneratedPlanDetails
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Generated Plan Suggestion:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp) // Add padding
        )

        // Use MealItemCard to display each meal
        planDetails.breakfast.firstOrNull()?.let { meal ->
            MealItemCard(
                time = "Breakfast",
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", "),
                // Add other details from 'meal' object if MealItemCard supports them
            )
        } ?: Box(modifier=Modifier.fillMaxWidth().padding(vertical=8.dp)) {
            Text("No breakfast suggestion found.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.Center))
        }


        Spacer(modifier = Modifier.height(8.dp))

        planDetails.lunch.firstOrNull()?.let { meal ->
            MealItemCard(
                time = "Lunch",
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", ")
            )
        } ?: Box(modifier=Modifier.fillMaxWidth().padding(vertical=8.dp)) {
            Text("No lunch suggestion found.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.Center))
        }


        Spacer(modifier = Modifier.height(8.dp))

        planDetails.supper.firstOrNull()?.let { meal ->
            MealItemCard(
                time = "Supper/Dinner", // Combine label
                meal = meal.name,
                ingredients = meal.ingredients.joinToString(", ")
            )
        } ?: Box(modifier=Modifier.fillMaxWidth().padding(vertical=8.dp)) {
            Text("No supper/dinner suggestion found.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.Center))
        }

    }
}