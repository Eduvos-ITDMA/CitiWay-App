# 🚀 Android Project Documentation (Temporary README)

Hi guys  
This file is just a **temporary README** to help us all stay on the same page while we build the
app. Once we’re closer to finishing, we’ll replace this with a polished version.  
This guide explains the basics of how the app works so far.

---

## 📌 Entry Point: `MainActivity.kt`

Every Android app needs a **starting point**. For us, that’s the [MainActivity.kt](https://github.com/Eduvos-ITDMA/CitiWay-App/blob/main/app/src/main/java/com/example/citiway/MainActivity.kt) file:

This file currently:

- Sets up the **user interface**.
- Loads the **navigation system** (explained below).
- Applies the **Material Theme** (so our app looks modern and consistent with Android design
  guidelines).

---

## 🧭 Navigation Graph

Instead of manually switching between screens, Android lets us use a **Navigation Graph**.

- Think of it as a **map** of all the screens in our app.
- It defines **where you can go** and **how to get there**.
- Each box in the diagram below represents a screen or group of screens.

We’re using the **Jetpack Navigation Component**, which makes it easier to manage complex navigation.

![Navigation Graph](readme-images/MyCiTi-nav-graph-2.png)

---

## 📂 Navigation Graph Breakdown

- **Nav Host**  
  This is the "container" that holds our screens. All navigation happens inside it.

- **Home Graph**  
  Handles the main app features:
  - 🏠 Home screen
  - 📅 Schedules
  - ⭐ Favourites

- **Journey Selection Graph**  
  Handles journey planning:
  - 🎯 Destination selection
  - 📍 Start location selection
  - 🚗 Journey selection

- **Trips Graph**  
  Handles information related to the user's completed journeys (trips)
  - 📜 Journey History
  - 📝 Journey Summary

- **Other Screens**
  - 🌊 Splash (the first screen you see when the app loads)
  - 📊 Progress tracker
  - ❓ Journey summary

---

## 📱 Bottom Navigation Bar

Our app uses a **bottom navigation bar** (like most modern apps).

- It lets users switch between **major sections** of the app quickly.
- It’s **linked to the navigation graph**, so when you tap a button, it tells the Nav Host which
  screen to show.
- Only some screens have a bottom navigation bar.

The current bottom nav bar differs from the figma design due to a number of design considerations. Furthermore, we are using a Menu (hamburger icon) in the Top Bar to offer additional navigation and settings options.

#### Old Design
![Old Design](readme-images/old-bottom-bar.png)

#### Current Design
![Current Design](readme-images/current-bottom-bar.png)

The following nav graph diagram shows the names of the **screens** (destinations) and **routes** used in the
actual code. Only the screens with a border get a dedicated bottom nav bar item/tab.

![Navigation Graph Implementation](readme-images/MyCiTi-nav-graph-implementation-2.png)

---

## 🎨 Material Theme

We’re using **Material Design 3 (Material You)**, which is Google’s official design system for
Android.

This means:

- **Color.kt** defines the colours we're using throughout the app.
- **Theme.kt** packages these colours into a _darkColorScheme_ and _lightColorScheme_, allowing the app to automatically adapt when the user changes their system theme.
- **Type.kt** defines a number of typography styles we can use, such as _displayLarge_, _headlineMedium_, _titleMedium_, etc.

To see the full setup, review these files in the [ui\theme\\](https://github.com/Eduvos-ITDMA/CitiWay-App/blob/main/app/src/main/java/com/example/citiway/ui/theme) directory.

---

## 🧩 Some Custom Components

A number of UI components have been made that can be reused in different screens through the app. More such components will be coded as we go along. These are intended to be reused while allowing flexibility through the arguments you pass. These custom composables are located in the [components/](https://github.com/Eduvos-ITDMA/CitiWay-App/blob/main/app/src/main/java/com/example/citiway/ui/components) directory

I included code blocks above each one to document the purpose, intended usage of the component and each of its parameters in detail. This documentation will appear in Android Studio when you hover over the component in code, or you can view it directly in the file its located in. Please ask me (Caleb) how to use them if you are confused.

### Example: LocationSearchField
A text input field designed for location searching, featuring an inline action icon. This composable provides a styled text field, typically used for entering location queries, and includes a placeholder for text and an icon slot for actions.

Refer to the figma design to see how this component will be reused.

#### Appearance on home screen:
![Location Search Field](readme-images/location-search-field-composable.png)

---

## 📝 Summary

- Start point = `MainActivity.kt`
- Navigation = handled by **Navigation Graph**
- Bottom Nav Bar = for switching major sections
- Material Theme = modern look, less manual styling
- Custom components = reduce development time and effort through reusability

## 🗄️ Database Setup (Room + Repository Pattern)

**Last Updated: October 17, 2025**

### Overview
Complete Room database architecture implementing ERD v2 design with repository pattern, multi-modal journey support, and trip favorites functionality.

### Database Schema (Version 3)
- **8 Entity Tables**: User, Provider, Route, Trip, MonthlySpend, MyCitiFare, MetrorailFare, SavedPlace
- **No Junction Tables**: Routes directly link to Trips for simplified architecture
- **Foreign Key Relationships**: Proper referential integrity with cascade deletes
- **Nullable Fields**: All fields nullable for flexible development and testing

### Architecture
```
data/
├── local/
│   ├── entities/           # Database table definitions
│   │   ├── User.kt
│   │   ├── Provider.kt
│   │   ├── Route.kt       # Route legs for journeys
│   │   ├── Trip.kt        # Complete journeys with mode & favorites
│   │   ├── MonthlySpend.kt
│   │   ├── MyCitiFare.kt
│   │   ├── MetrorailFare.kt
│   │   └── SavedPlace.kt
│   │
│   ├── dao/               # Data Access Objects (SQL queries)
│   │   ├── UserDao.kt
│   │   ├── ProviderDao.kt
│   │   ├── RouteDao.kt
│   │   ├── TripDao.kt
│   │   ├── MonthlySpendDao.kt
│   │   ├── MyCitiFareDao.kt
│   │   ├── MetrorailFareDao.kt
│   │   └── SavedPlaceDao.kt
│   │
│   ├── CitiWayDatabase.kt
│   └── DatabaseSeeder.kt
│
└── repository/
    └── CitiWayRepository.kt    # Single source of truth
```

### Enhanced Trip Entity
Trips now include comprehensive journey tracking:
- **mode**: `String` - Journey type: "Bus", "Train", or "Multi"
- **total_distance_km**: `Double` - Aggregate distance across all route legs
- **total_fare**: `Double` - Total cost of the journey
- **is_favourite**: `Boolean` - User can mark trips as favorites ⭐
- **created_at**: `Long` - Timestamp for sorting and analytics

### Enhanced Route Entity (Journey Legs)
Routes represent individual segments of a journey:
- **trip_id**: `Int` (FK) - Links route leg to parent trip
- **distance_km**: `Double` - Distance for this specific leg
- **fare_contribution**: `Double` - Fare amount for this leg
- **mode**: `String` - Transport mode for this leg ("bus" or "train")
- **myciti_fare_id**: `Int?` (FK) - Links to MyCiti fare structure
- **metrorail_fare_id**: `Int?` (FK) - Links to Metrorail fare structure

### Data Flow Models

#### Single-Mode Trip
```
User → Trip (mode="Bus") 
       └─→ Route (1 leg, links to Provider & MyCitiFare)
```

#### Multi-Modal Trip
```
User → Trip (mode="Multi", total_fare=18.50, distance=18.2km)
       ├─→ Route Leg 1 (mode="bus", 7.5km, R8.50, provider=MyCiTi)
       └─→ Route Leg 2 (mode="train", 10.7km, R10.00, provider=Metrorail)
```

### Repository Pattern Benefits
- ✅ **Separation of Concerns**: ViewModels don't directly access DAOs
- ✅ **Centralized Logic**: All data operations in one place
- ✅ **Easier Testing**: Mock repository instead of multiple DAOs
- ✅ **Future-Proof**: Easy to add API calls alongside database operations
- ✅ **Consistency**: Uniform data access patterns across the app

### Key Features
- **Trip Favorites**: Users can mark trips as favorites with toggle functionality
- **Multi-Modal Support**: Seamlessly handles single and multi-mode journeys
- **Route Leg Tracking**: Individual fare and distance tracking per journey segment
- **Flow-Based Queries**: Reactive queries for real-time UI updates
- **Realistic Sample Data**: South African transport providers (MyCiTi, Metrorail, Golden Arrow)
- **Coroutine-Based**: All async operations use Kotlin coroutines
- **ViewModelFactory Pattern**: Proper dependency injection

### Database Initialization & Seeding

In your `MainActivity.onCreate()`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize database and repository
    val database = CitiWayDatabase.getDatabase(this)
    val repository = CitiWayRepository(database)
    val seeder = DatabaseSeeder(repository)
    
    // Seed database on first launch
    lifecycleScope.launch {
        seeder.seedDatabase()  // ⚠️ Comment out after 1st launch to avoid duplicate data
    }
    
    setContent {
        // Your app content
    }
}
```

**Important**: After running the app once, comment out `seeder.seedDatabase()` to prevent duplicate data on subsequent launches.

### Sample Data Included
The database seeder creates:
- **1 User** (user_id = 1, no login required)
- **3 Transport Providers**: MyCiTi, Metrorail, Golden Arrow
- **10 Realistic Trips**:
  - 4 Bus-only trips (MyCiTi, Golden Arrow)
  - 3 Train-only trips (Metrorail)
  - 3 Multi-mode trips (Bus+Train, Bus+Bus, Train+Bus)
  - 5 trips marked as favorites
- **13 Route Legs**: Demonstrating multi-modal journey architecture
- **Fare Structures**: 3 MyCiti distance bands, 4 Metrorail zone fares
- **Cape Town Locations**: Realistic routes (Waterfront, CBD, Airport, Simon's Town, etc.)
- **2 Saved Places**: Home and Work locations

### Usage in ViewModels
```kotlin
// In your Composable/Screen
val context = LocalContext.current
val database = CitiWayDatabase.getDatabase(context)
val repository = CitiWayRepository(database)

val viewModel: CompletedJourneysViewModel = viewModel(
    factory = viewModelFactory { 
        CompletedJourneysViewModel(repository) 
    }
)

// Access trip data
val tripsState by viewModel.screenState.collectAsState()
val recentTrips = tripsState.recentJourneys
val favoriteTrips = tripsState.favouriteJourneys
```

### DAO Method Examples

**TripDao - Favorites Support:**
```kotlin
fun getFavoriteTrips(userId: Int): Flow<List<Trip>>
suspend fun updateFavoriteStatus(tripId: Int, isFavorite: Boolean)
```

**RouteDao - Journey Legs:**
```kotlin
fun getRoutesByTrip(tripId: Int): Flow<List<Route>>  // Get all legs for a trip
suspend fun deleteRoutesByTrip(tripId: Int)          // Cascade delete
```

### Database Version History
- **v1**: Initial setup with SavedPlace-based journeys
- **v2**: Changed Trip.start_stop and end_stop from Int to String
- **v3** (Current):
  - Removed TripRoute junction table
  - Added Trip.mode, total_distance_km, is_favourite, created_at
  - Added Route.trip_id, distance_km, fare_contribution, fare FKs
  - Simplified architecture with direct Trip → Route relationships

### Migration Strategy
Currently using `fallbackToDestructiveMigration()` for development:
- ✅ Automatically handles schema changes
- ✅ Drops and recreates database on version changes
- ⚠️ **Remove before production** - implement proper migrations
- 💾 User data will be lost on schema updates

### Future Enhancements
- [ ] Implement more database tables to handle trip data from api
- [ ] Story json data to be used in app

---

**Tech Stack**: Room 2.6.1, Kotlin Coroutines, Flow, Repository Pattern, MVVM Architecture