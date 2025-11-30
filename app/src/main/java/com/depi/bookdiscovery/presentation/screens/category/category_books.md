# Category Books Screen Documentation

## Overview
The `CategoryBooksScreen` is responsible for displaying books that belong to a specific, user-selected category. It fetches data based on the `categoryId` passed to it and provides options for how the content is displayed. Users can switch between a grid and a list view, manage favorites, and navigate to a book's detail page.

## UI Components

### 1. Top App Bar
- **Title:** Dynamically displays the `categoryName` (e.g., "Fantasy").
- **Navigation Icon:** An arrow that pops the back stack, returning the user to the previous screen.
- **Actions:** Contains a single `IconButton` that toggles the display format between a list and a grid.

### 2. Category Header
- A prominent header section below the app bar that visually represents the category.
- **Icon:** Displays an icon unique to the category, fetched via `getIconForCategory()`.
- **Text:** Shows the `categoryName` in a large, bold font.

### 3. Book Display
- The main content area, which can render books in one of two ways:
  - **Grid View (`LazyVerticalGrid`):** The default view. It displays books in a two-column grid using the `BookGridItem` composable.
  - **List View (`LazyColumn`):** An alternative view that displays books in a single, vertical list using the `BookCard` composable.

### 4. States
- **Loading:** A `CircularProgressIndicator` is shown in the center of the screen while the initial data is being fetched.
- **Success:** The books are displayed in either the grid or list view.
- **Empty:** If the API returns no books for the category, the message "No books found in this category" is shown.

## User Actions and Logic

- **View Toggle:** The user can tap the icon in the top app bar to switch between the grid and list layouts. This is managed by the `isGridView` state variable.
- **Favorite/Unfavorite:**
  - Each book item has a favorite icon.
  - If a user marks a book as a favorite, it is immediately updated.
  - If a user attempts to *unfavorite* a book, a `ConfirmUnfavoriteDialog` is presented to ensure the action is intentional. The book is only removed from favorites upon confirmation.
- **Infinite Scroll:**
  - Both the lazy list and lazy grid are configured to detect when the user has scrolled near the end of the loaded content.
  - When this threshold is crossed, `searchViewModel.loadMore()` is triggered to fetch and append the next page of results, allowing for seamless browsing.
- **Navigation:** Tapping on any `BookCard` or `BookGridItem` navigates the user to the `BookDetailsScreen`, passing the corresponding `Item` object to the destination.
- **Data Lifecycle:**
  - `LaunchedEffect` is used to trigger the initial category-specific search when the screen is first loaded.
  - `DisposableEffect` ensures that the `searchViewModel` is cleared of old search data when the user navigates away from the screen, preventing state leakage.
