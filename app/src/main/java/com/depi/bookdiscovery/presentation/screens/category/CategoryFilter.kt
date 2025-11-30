package com.depi.bookdiscovery.presentation.screens.category

/**
 * Filters a list of categories based on a search query.
 *
 * This is a pure function, making it easy to unit test without Android framework dependencies.
 *
 * @param categories The original list of categories to filter.
 * @param searchQuery The text to search for within the category names.
 * @param nameResolver A lambda function that takes a category's resource ID and returns the resolved string name.
 * @return A new, filtered list of categories.
 */
fun filterCategoriesByName(
    categories: List<Category>,
    searchQuery: String,
    nameResolver: (Int) -> String
): List<Category> {
    if (searchQuery.isBlank()) {
        return categories
    }
    return categories.filter {
        nameResolver(it.name).contains(searchQuery, ignoreCase = true)
    }
}
