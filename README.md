# N-Queens Puzzle

An Android puzzle game based on the classic [N-Queens problem](https://en.wikipedia.org/wiki/Eight_queens_puzzle). Place n queens on an n×n chessboard so that no two queens threaten each other (same row, column, or diagonal).

## Demo

> Add your demo video here (MP4, GIF, or YouTube link)

## Features

- Interactive n×n chessboard (sizes 4-12)
- Three difficulty levels with varying hint systems
- Real-time conflict validation and visual feedback
- Win detection with celebration animations
- Best times leaderboard
- Sound effects for queen placement and victory
- Clean Material 3 design with Jetpack Compose

## Difficulty Levels

The game offers three difficulty levels that control the amount of visual feedback:

### Easy
- **Conflict highlighting**: All conflicting queens are highlighted
- **Attack visualization**: Selected queen's attack pattern is shown on the board
- **Best for**: Learning the game mechanics

### Medium
- **Conflict highlighting**: All conflicting queens are highlighted
- **Attack visualization**: None
- **Best for**: Intermediate players who understand the rules

### Hard
- **Conflict highlighting**: Only the selected queen shows conflict (if conflicting)
- **Attack visualization**: None
- **Best for**: Advanced players seeking minimal hints

Implementation: See `NQueensGame.kt:121-147`

## Architecture

The project follows clean architecture principles with a three-module structure:

### Module Structure

```
N-Queens Puzzle/
├── :logic      # Pure Kotlin JVM - game logic
├── :data       # Android library - persistence
└── :app        # Android app - UI layer
```

**:logic** (Pure Kotlin JVM)
- Pure game logic with no Android dependencies
- Only depends on Kotlin Coroutines
- Contains: `NQueensLogic`, `NQueensGame`, models
- Location: `logic/src/main/kotlin/`

**:data** (Android Library)
- Persistence layer using DataStore
- Manages score/leaderboard storage
- Contains: `ScoreRepository`, `ScoreEntry`
- Location: `data/src/main/kotlin/`

**:app** (Android Application)
- Presentation layer with Jetpack Compose
- UI components, ViewModels, theming
- Depends on both `:logic` and `:data`
- Location: `app/src/main/java/`

### Three Levels of Logic Separation

The architecture separates concerns across three distinct layers:

#### 1. Pure Chess Logic (`NQueensLogic`)

**File**: `logic/src/main/kotlin/.../NQueensLogic.kt`

Pure functions with no state or side effects. Platform-agnostic and easily testable.

```kotlin
object NQueensLogic {
    fun hasConflict(a: Position, b: Position): Boolean
    fun findConflictingQueens(queens: Set<Position>): Set<Position>
    fun getAttackedCells(queen: Position, boardSize: Int): Set<Position>
    fun isSolved(queens: Set<Position>, boardSize: Int): Boolean
}
```

#### 2. Game Implementation (`NQueensGame`)

**File**: `logic/src/main/kotlin/.../NQueensGame.kt`

Manages game state using Kotlin `StateFlow`. Orchestrates the chess logic and handles user moves.

```kotlin
class NQueensGame {
    data class NQueensState(
        val queens: Set<Position>,
        val selectedQueen: Position?,
        val visibleConflicts: Set<Position>,
        val visibleAttackedCells: Set<Position>,
        // ... game state
    )

    val state: StateFlow<NQueensState>
    fun userMove(position: Position)
    fun restart(newConfig: GameConfig?)
}
```

#### 3. Presentation Layer (`GameViewModel`)

**File**: `app/src/main/java/.../ui/game/GameViewModel.kt`

Thin adapter between game engine and UI. Transforms game state to presentation state and handles side effects.

```kotlin
class GameViewModel {
    val renderState: StateFlow<BoardRenderState>
    val sideEffects: Flow<GameSideEffect>
    fun dispatch(action: UserAction)
}
```

## Data Flow

The application follows a unidirectional data flow pattern:

```
User Tap on Board
    ↓
UserAction.TapCell(position)
    ↓
GameViewModel.dispatch(action)
    ↓
NQueensGame.userMove(position)
    ↓
NQueensLogic algorithms (hasConflict, isSolved, etc.)
    ↓
NQueensState (updated via StateFlow)
    ↓
GameViewModel observes state changes
    ├─→ handleSideEffects() → GameSideEffect (sound/navigation)
    └─→ toRenderState() → BoardRenderState
         ↓
GameScreen UI (Compose rerenders)
```

**Key principle**: State flows down from logic to UI, actions flow up from UI to logic.

The ViewModel acts as a "think layer" that permits communication between all parts:
- **Logic ↔ Platform**: Translates between game state and UI state
- **User actions**: Dispatches user interactions to game logic
- **Side effects**: Triggers sounds and navigation based on game events

## State vs Actions

The architecture distinguishes between state (what *is*) and actions (what *happened*):

### State (What IS)

Immutable snapshots representing the current game condition.

**File**: `NQueensGame.NQueensState`

```kotlin
data class NQueensState(
    val config: GameConfig,
    val queens: Set<Position>,
    val selectedQueen: Position?,
    val gameStartTime: Long?,
    val visibleConflicts: Set<Position>,
    // ... persisted state
)
```

State persists and drives UI rendering.

### Actions (What HAPPENED)

Transient events that occurred during gameplay.

**File**: `logic/src/main/kotlin/.../models/GameAction.kt`

```kotlin
sealed class GameAction {
    data class QueenAdded(val position: Position, val causedConflict: Boolean)
    data class QueenRemoved(val position: Position)
    data class QueenMoved(val from: Position, val to: Position, val causedConflict: Boolean)
    data object GameWon
    data object GameReset
}
```

Actions are used for triggering side effects (sounds, animations) but are not stored.

### Conversion to Presentation

The game state is converted to a pure presentation object:

**NQueensState → BoardRenderState**

**File**: `app/src/main/java/.../ui/game/BoardRenderState.kt`

```kotlin
data class BoardRenderState(
    val boardSize: Int,
    val queens: Set<Position>,
    val queensRemaining: Int,
    val visibleConflicts: Set<Position>,
    val visibleAttackedCells: Set<Position>,
    val isSolved: Boolean,
)
```

**Why this separation?**
- Game state contains full game information (actions, timestamps, internal state)
- Presentation state contains only visual information needed by UI
- ViewModel acts as translator between logic layer and platform layer
- Clean separation enables independent testing of each layer

## Build & Test

### Prerequisites

- JDK 17 or higher
- Android SDK (Min SDK 26, Target SDK 36)

### Build

```bash
# Build the project
./gradlew build

# Install debug APK
./gradlew installDebug
```

### Run Tests

```bash
# All unit tests
./gradlew test

# Specific test class
./gradlew test --tests "com.monday8am.nqueenspuzzle.logic.NQueensLogicTest"

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

## Testing Strategy

The project maintains strong test coverage across all architectural layers:

**Test Files**:
- `logic/src/test/kotlin/.../NQueensLogicTest.kt` - Pure chess logic tests
- `logic/src/test/kotlin/.../NQueensGameTest.kt` - Game state management tests
- `app/src/test/java/.../GameViewModelTest.kt` - Presentation logic tests
- `app/src/androidTest/java/.../ExampleInstrumentedTest.kt` - UI integration tests

**Testing approach**:
- **Unit tests** for pure chess logic (NQueensLogic)
- **StateFlow testing** for reactive game implementation (NQueensGame)
- **ViewModel testing** for state transformation and side effects
- **UI tests** for Compose components and user interactions

## Tech Stack

**Language**: Kotlin 2.0.21

**UI**:
- Jetpack Compose (BOM 2024.09.00)
- Material 3

**Architecture**:
- Kotlin Coroutines & Flow
- StateFlow for reactive state management
- Clean Architecture (logic/data/presentation separation)

**Build**:
- Gradle with Version Catalog (`gradle/libs.versions.toml`)
- Android Gradle Plugin 8.13.2

## License

[Add your license here]
