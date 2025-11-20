package com.depi.bookdiscovery.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.Screen

data class Category(
    val id: String,
    val name: Int,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    val categories = listOf(
        Category("fantasy", R.string.category_fantasy, Icons.Outlined.AutoAwesome),
        Category("science-fiction", R.string.category_science_fiction, Icons.Outlined.RocketLaunch),
        Category("mystery", R.string.category_mystery, Icons.Outlined.Search),
        Category("thriller", R.string.category_thriller, Icons.Outlined.LocalFireDepartment),
        Category("romance", R.string.category_romance, Icons.Outlined.FavoriteBorder),
        Category("westerns", R.string.category_westerns, Icons.Outlined.Explore),
        Category("dystopian", R.string.category_dystopian, Icons.Outlined.PublicOff),
        Category("contemporary", R.string.category_contemporary, Icons.Outlined.Apartment),
        Category("biography", R.string.category_biography, Icons.Outlined.Person),
        Category("history", R.string.category_history, Icons.Outlined.AccountBalance),
        Category("self-help", R.string.category_self_help, Icons.Outlined.SelfImprovement),
        Category("business", R.string.category_business, Icons.Outlined.BusinessCenter),
        Category("cooking", R.string.category_cooking, Icons.Outlined.Restaurant),
        Category("art", R.string.category_art, Icons.Outlined.Palette),
        Category("poetry", R.string.category_poetry, Icons.Outlined.TheaterComedy),
        Category("travel", R.string.category_travel, Icons.Outlined.Flight)
    )
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.categories_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search categories...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(8.dp)
            )

            Text(
                text = "All Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val filteredCategories = categories.filter {
                stringResource(it.name).contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredCategories) { category ->
                    val categoryName = stringResource(category.name)
                    CategoryRow(category = category, onClick = {
                        navController.navigate(Screen.CategoryBooksScreenRoute.createRoute(category.id, categoryName))
                    })
                }
            }
        }
    }
}

@Composable
fun CategoryRow(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(category.name),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
