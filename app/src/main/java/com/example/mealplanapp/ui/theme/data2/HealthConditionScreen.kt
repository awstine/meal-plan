package com.example.mealplanapp.ui.theme.data2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


//@Composable
//fun HealthConditionScreen(
//    navController: NavController,
//    viewModel: MealViewModel
//) {
//    var userCondition by remember { mutableStateOf("") }
//    val isLoading by viewModel.isLoading.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Enter Your Health Goal",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        // Input field for user to enter their health condition
//        OutlinedTextField(
//            value = userCondition,
//            onValueChange = { userCondition = it },
//            label = { Text("Health Goal (e.g., weight loss, diabetes)") },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading // Disable input when loading
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator()
//                Spacer(modifier = Modifier.height(8.dp))
//                Text("Analyzing your meal plan...")
//            }
//        } else {
//            Button(
//                onClick = {
//                    if (userCondition.isNotBlank()) {
//                        viewModel.generateMealPlan(userCondition.trim())
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = userCondition.isNotBlank() && !isLoading
//            ) {
//                Text("Generate Meal Plan")
//            }
//        }
//    }
//
//    // Navigate when loading is complete
//    LaunchedEffect(isLoading) {
//        if (!isLoading && viewModel.mealPlan.value != null) {
//            navController.navigate("meal_plan")
//        }
//    }
//}
@Composable
fun HealthConditionScreen(viewModel: HealthConditionViewModel = viewModel()) {
    val mealPlan by viewModel.mealPlan
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Meal Plan", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        MealItem("Breakfast", mealPlan.breakfast)
        MealItem("Lunch", mealPlan.lunch)
        MealItem("Supper", mealPlan.supper)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.generateMealPlan(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate New Meal Plan")
        }
    }
}

@Composable
fun MealItem(time: String, meal: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = time, style = MaterialTheme.typography.labelSmall)
        Text(
            text = meal,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        )
    }
}