package com.example.mealplanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealplanapp.ui.theme.MealPlanAppTheme
import com.example.mealplanapp.ui.theme.screen.health.AuthViewModel
import com.example.mealplanapp.ui.theme.screen.health.HealthConditionViewModel
import com.example.mealplanapp.ui.theme.screen.health.category.CategoryScreen
import com.example.mealplanapp.ui.theme.screen.health.goalinput.CustomGoalInputScreen
import com.example.mealplanapp.ui.theme.screen.health.meal.MealDetailScreen
import com.example.mealplanapp.ui.theme.screen.health.meal.MealListScreen
import com.example.mealplanapp.ui.theme.screen.health.meal.MealPlanScreen
import com.example.mealplanapp.ui.theme.screen.health.meal.MealViewModel
import com.example.mealplanapp.ui.theme.screen.health.savedMeals.SavedMealsScreen
import com.example.mealplanapp.ui.theme.screen.health.signin.SignInScreen
import com.example.mealplanapp.ui.theme.screen.health.signup.SignUpScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealPlanAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "signUp"
                ) {
                    // Authentication Flow
                    composable("signIn") {
                        SignInScreen(
                            onSignInComplete = {
                                navController.navigate("meal_plan") {
                                    popUpTo("signIn") { inclusive = true }
                                }
                            },
                            onNavigateToSignUp = {
                                navController.navigate("signUp")
                            }
                        )
                    }

                    composable("signUp") {
                        val viewModel: AuthViewModel = hiltViewModel()
                        SignUpScreen(
                            viewModel = viewModel,
                            onSignUpComplete = {
                                navController.navigate("meal_plan") {
                                    popUpTo("signUp") { inclusive = true }
                                }
                            },
                            onNavigateToSignIn = {
                                navController.navigate("signIn") {
                                    popUpTo("signUp") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Main App Flow
                    composable("meal_plan") { navBackStackEntry ->
                        val viewModel: HealthConditionViewModel = hiltViewModel(navBackStackEntry)
                        MealPlanScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    composable("customGoal") { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry("meal_plan")
                        }

                        val viewModel: HealthConditionViewModel = hiltViewModel(parentEntry)
                        // ... use the viewModel
                        CustomGoalInputScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    composable("savedMeals") { navBackStackEntry ->
                        val parentEntry = remember(navBackStackEntry) {
                            navController.getBackStackEntry("meal_plan")
                        }
                        val viewModel: HealthConditionViewModel = hiltViewModel(parentEntry)
                        SavedMealsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    // Meal Exploration Flow
                    composable("categories") {
                        val viewModel: MealViewModel = hiltViewModel()
                        CategoryScreen(
                            categories = viewModel.getCategories(),
                            onCategorySelected = { category ->
                                navController.navigate("meals/$category")
                            }
                        )
                    }

                    composable("meals/{category}") { backStackEntry ->
                        val viewModel: MealViewModel = hiltViewModel()
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        viewModel.loadMealsByCategory(category)

                        MealListScreen(
                            navController = navController,
                            meals = viewModel.meals.collectAsState().value,
                            onMealSelected = { mealId ->
                                navController.navigate("meal_detail/$mealId")
                            }
                        )
                    }

                    composable("meal_detail/{mealId}") { backStackEntry ->
                        val viewModel: MealViewModel = hiltViewModel()
                        val mealId =
                            backStackEntry.arguments?.getString("mealId")?.toIntOrNull() ?: 0
                        viewModel.loadMealById(mealId)

                        MealDetailScreen(
                            navController = navController,
                            mealId = mealId,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}


