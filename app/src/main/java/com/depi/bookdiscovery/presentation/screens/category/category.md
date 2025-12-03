# Category Screen Documentation

## Overview
The Category Screen displays a list of book categories that users can explore. When a user selects a category, they are navigated to a dedicated screen showing all books within that category.

## UI Components

### 1. Top App Bar
- **Title:** "Categories"
- **Navigation:** A back arrow to return to the previous screen.

### 2. Category List
- A vertically scrolling list (`LazyColumn`) of all available book categories.
- Each category is represented by a `CategoryItem`.

### 3. Category Item
- Displays the category name (e.g., "Fiction," "Science," "History").
- Is clickable, and tapping it navigates the user to the `CategoryBooksScreen` for that category.

## User Actions and Logic

- **Select Category:** Tapping on any category item in the list.
- **Navigation:**
  - On category selection, the app navigates to the `CategoryBooksScreen`.
  - The selected category's name is passed as an argument to the destination screen to fetch and display the relevant books.

## State Management
- The screen uses a `CategoryViewModel` to manage the list of categories.
- The categories are fetched and exposed to the UI, which simply displays them.
- The screen itself is largely stateless, with the primary logic centered around navigation.

## Navigation
- **From:** This screen is typically accessed from the `MainScreen`'s category chips.
- **To:** Navigates to the `CategoryBooksScreen`, passing the selected category's name as a parameter.
