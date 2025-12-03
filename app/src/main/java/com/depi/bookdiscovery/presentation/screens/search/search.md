# Search Screen Documentation

## Overview
The Search Screen allows users to search for books, view their search history, and explore trending topics. It provides filtering options to refine search results and supports infinite scrolling to load more books as the user scrolls.

## UI Components

### 1. Top App Bar
- **Title:** "Search"
- **Navigation:** A back arrow to return to the previous screen.

### 2. Search Bar
- A text field for entering search queries.
- A leading search icon.
- A trailing "clear" icon that appears when text is entered.
- Triggers a search automatically as the user types and when the keyboard's search action is pressed.

### 3. Filter Button
- An icon button with a "tune" icon next to the search bar.
- Opens a modal bottom sheet with filtering options.

### 4. Filter Modal Bottom Sheet
- Allows users to filter searches by:
  - **Print Type:** All, Books, Magazines.
  - **E-book Type:** All, Free E-books, Paid E-books.
- Selecting a filter automatically triggers a new search with the chosen criteria.

### 5. Initial View (Empty Search Text)
- **Recent Searches:**
  - A list of the user's most recent search terms.
  - Tapping a term executes a search for it.
  - A "Clear All" button to erase the search history.
- **Trending Topics:**
  - A collection of filter chips with trending book-related subjects (e.g., "AI and Machine Learning," "Climate Science").
  - Tapping a topic executes a search for it.

### 6. Search Results
- Displayed as a vertical list of `BookCard` components.
- Each card shows book details and a favorite icon.
- **Infinite Scroll:** More results are loaded automatically as the user scrolls to the end of the list.
- **Loading State:** A `CircularProgressIndicator` is shown while results are being fetched.
- **Empty State:** A "No results found." message is displayed if a search yields no results.

## User Actions and Logic

- **Search:** Initiated by typing in the search bar, pressing the keyboard's search action, or tapping a recent/trending term.
- **Favorite/Unfavorite:**
  - Tapping the favorite icon on a `BookCard` adds the book to the user's favorites.
  - If the book is already a favorite, a confirmation dialog appears to prevent accidental removal.
- **Navigation:** Tapping a `BookCard` navigates to the `BookDetailsScreen`.
- **Filtering:** Applying filters from the bottom sheet refines the search results.
- **State Management:** The screen's state (loading, success, error) is managed by a `SearchViewModel`, which exposes a `UiState` object.
