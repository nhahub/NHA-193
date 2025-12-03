package com.depi.bookdiscovery.presentation.screens.category

/**
 * Filters a list of categories based on a search query.
 *
 * This is a pure, top-level function, making it easy to unit test without Android framework dependencies.
 *
 * @param categoriesWithNames The original list of categories paired with their resolved string names.
 * @param searchQuery The text to search for within the category names.
 * @return A new, filtered list of categories with their resolved names.
 */
fun filterCategoriesByName(
    categoriesWithNames: List<Pair<Category, String>>,
    searchQuery: String,
): List<Pair<Category, String>> {
    if (searchQuery.isBlank()) {
        return categoriesWithNames
    }
    return categoriesWithNames.filter { (_, categoryName) ->
        categoryName.contains(searchQuery, ignoreCase = true)
    }
}
