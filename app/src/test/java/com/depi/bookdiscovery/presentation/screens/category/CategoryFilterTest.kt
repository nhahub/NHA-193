package com.depi.bookdiscovery.presentation.screens.category

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Search
import com.depi.bookdiscovery.R
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryFilterTest {

    private val categories = listOf(
        Category("sci-fi", R.string.category_science_fiction, Icons.Outlined.RocketLaunch) to "Science Fiction",
        Category("mystery", R.string.category_mystery, Icons.Outlined.Search) to "Mystery",
        Category("bio", R.string.category_biography, Icons.Outlined.Person) to "Biography"
    )

    @Test
    fun `filterCategoriesByName with matching query returns correct items`() {
        // Given
        val searchQuery = "sci"

        // When
        val filteredList = filterCategoriesByName(categories, searchQuery)

        // Then
        assertEquals(1, filteredList.size)
        assertEquals("Science Fiction", filteredList[0].second)
    }

    @Test
    fun `filterCategoriesByName with blank query returns all items`() {
        // Given
        val searchQuery = ""

        // When
        val filteredList = filterCategoriesByName(categories, searchQuery)

        // Then
        assertEquals(3, filteredList.size)
    }

    @Test
    fun `filterCategoriesByName with no matching query returns empty list`() {
        // Given
        val searchQuery = "xyz"

        // When
        val filteredList = filterCategoriesByName(categories, searchQuery)

        // Then
        assertEquals(0, filteredList.size)
    }
}
