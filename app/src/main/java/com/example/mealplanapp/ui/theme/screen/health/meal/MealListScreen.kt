@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.mealplanapp.ui.theme.screen.health.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mealplanapp.data.entity.Meal


@Composable
@Preview
fun MealListScreen(
    navController: NavController,
    meals: List<Meal>,
    onMealSelected: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier
        .padding(8.dp)) {
        items(meals) { meal ->
            MealCard(meal = meal, onClick = { onMealSelected(meal.id) }, modifier = Modifier)
        }
    }
}

@Composable
fun MealCard(
    modifier: Modifier,
    meal: Meal,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = meal.imageResId),
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = "${meal.calories} kcal",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}