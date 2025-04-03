package com.example.mealplanapp.ui.theme.screen.health.goalinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel

@Composable
fun CustomGoalInputScreen(
    navController: NavController,
    viewModel: HealthConditionViewModel = hiltViewModel()
) {
    var goalInput by remember { mutableStateOf("") }
    val customGoal by viewModel.customGoal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            value = goalInput,
            onValueChange = { goalInput = it },
            label = { Text("Your health goal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.setCustomGoal(goalInput)
                    navController.navigate("meal_plan")
                }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.setCustomGoal(goalInput)
                navController.navigate("meal_plan"){
                    popUpTo("meal_plan") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = goalInput.isNotBlank()
        ) {
            Text("Generate Meal Plan")
        }
    }
}