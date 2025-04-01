package com.example.mealplanapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealplanapp.ui.theme.MealPlanAppTheme
import com.example.mealplanapp.ui.theme.data2.CategoryScreen
import com.example.mealplanapp.ui.theme.data2.HealthConditionScreen
import com.example.mealplanapp.ui.theme.data2.HealthConditionViewModel
import com.example.mealplanapp.ui.theme.data2.MealDetailScreen
import com.example.mealplanapp.ui.theme.data2.MealListScreen
import com.example.mealplanapp.ui.theme.data2.MealPlanScreen
import com.example.mealplanapp.ui.theme.data2.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealPlanAppTheme {
                val viewModel: MealViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "health_condition",
                ) {
                    composable("categories") {
                        CategoryScreen(
                            categories = viewModel.getCategories(),
                            onCategorySelected = { category ->
                                viewModel.loadMealsByCategory(category)
                                navController.navigate("meals/$category")
                            }
                        )
                    }

                    composable("meals/{category}") { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        MealListScreen(
                            navController,
                            meals = viewModel.meals.collectAsState().value,
                            onMealSelected = { mealId ->
                                viewModel.loadMealById(mealId)
                                navController.navigate("meal_detail/$mealId")
                            }
                        )
                    }

                    composable("meal_detail/{mealId}") { backStackEntry ->
                        val mealId = backStackEntry.arguments?.getString("mealId")?.toIntOrNull() ?: 0
                        MealDetailScreen(
                            navController = navController,
                            mealId = mealId,
                            viewModel()
                        )
                    }

                    composable("health_condition") {
                        HealthConditionScreen(
                            viewModel = HealthConditionViewModel(application = Application())
                        )
                    }

                    composable("meal_plan") {
                        MealPlanScreen(navController, viewModel)
                    }
                }
            }
        }
    }
}
