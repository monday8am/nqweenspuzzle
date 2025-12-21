# N-Queens Puzzle

An Android puzzle game based on the classic [N-Queens problem](https://en.wikipedia.org/wiki/Eight_queens_puzzle). Place n queens on an n×n chessboard so that no two queens threaten each other (same row, column, or diagonal).

## Demo

> Add your demo video here (MP4, GIF, or YouTube link)

## Features

- Interactive n×n chessboard (sizes 4-12)
- Three difficulty levels with varying hint systems
- Real-time conflict validation and visual feedback
- Win detection with celebration animations
- Best times leaderboar

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

The project follows a regular Google Jetpack architecture with a three-module structure:

### Module Structure

```
N-Queens Puzzle/
├── :logic      # Pure Kotlin JVM - game logic and models
├── :data       # Android library - persistence layer using DataStore
└── :app        # Android app - UI layer with components, ViewModels, navigation, theming
```

### Three Levels of Logic Separation

There are three levels of logic separation for the game code:

1. *Pure Chess Logic*: Pure functions with no state or side effects. Platform-agnostic and easily testable.
2. *Game Implementation*: Manages game state using Kotlin `StateFlow`. Orchestrates the chess logic and handles user moves.
3. *Presentation Layer*: Thin adapter between game engine and the Android platform. Transforms game state to presentation state and handles side effects.

### Data Flow

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

### Conversion to Presentation

The game state is converted to a pure presentation object: **NQueensState → BoardRenderState**

While these states share similar fields, they are kept separate to maintain a clear boundary between the game engine and the UI:

*   **Domain vs. Presentation Purity**: `NQueensState` tracks the *absolute truth* of the game (internal timing, domain events). `BoardRenderState` tracks only what needs to be *drawn* (flattened config, derived counts like `queensRemaining`).
*   **The Event Problem**: `NQueensState` contains `lastAction`. Stripping this when mapping to `BoardRenderState` prevents the UI from reacting to one-off logic events as persistent state changes.
*   **Decoupled Testing**: `BoardRenderState` includes view-specific helpers (like `testCells`). Keeping them out of the `:logic` module ensures game rules can be tested without view-centric clutter.
*   **Framework Independence**: This separation ensures the `:logic` module remains a pure Kotlin JVM library, completely unaware of Android or Compose implementation details.

## Optimizations and Performance

The project implements several optimizations to ensure smooth performance even for larger board sizes:

### 1. Rendering Optimization
The `GameBoard` replaces the traditional "grid of cells" approach (which yields $N^2$ composables) with a custom **Canvas-based** rendering system.
- **CanvasBoard**: Draws the entire checkerboard, conflicts, and markers in a single `Canvas` node.
- **`drawWithCache`**: Caches static drawing instructions (like the square pattern) to avoid redundant computation during recomposition.
- **PieceLayout**: A custom `Layout` that only renders active pieces (Queens) based on the state, drastically reducing the node count to $1 (Canvas) + 1 (Layout) + N (Queens)$.

### 2. State & Data Pipeline
- **O(1) State Transformation**: The `GameViewModel` maps domain state to `BoardRenderState` without any loops. It uses `Set<Position>` for queens, conflicts, and attacks, enabling $O(1)$ lookups during the drawing phase.
- **Zero-Allocation Loops**: The drawing loop uses primitive checks against the state sets, avoiding object allocations on every frame.
- **Lazy Testability**: While the production UI uses sets for speed, a lazy `testCells: List<CellState>` property allows unit tests to assert on the full board state without impacting production performance.

### 3. Interaction & Side Effects
- **Geometric Tap Detection**: A single `pointerInput` on the board container calculates coordinates geometrically, removing the overhead of $N^2$ individual click listeners.
- **Event-Driven Side Effects**: Sounds and navigation are treated as transient `GameSideEffect` events. This decouples the ViewModel from platform-specific services (like `SoundPool`) and ensures resources are properly managed by the Activity lifecycle.

### 4. Computational Efficiency
- **Bit-Packed `Position`**: The coordinate system uses an `@JvmInline value class`. This packs `row` and `col` into a single 32-bit `Int` (16 bits each), eliminating object allocations for every cell reference and significantly reducing pressure on the garbage collector.
- **$O(N)$ Conflict Detection**: Replaced $O(N^2)$ pair-wise comparisons with a frequency-tracking algorithm. By hashing row, column, and diagonal "occupancy", the engine detects conflicts in linear time relative to the number of queens, ensuring consistent performance even on the largest supported boards.

## Testing Strategy

The project maintains strong test coverage across all architectural layers:

**Test Files**:
- `logic/src/test/kotlin/.../NQueensLogicTest.kt` - Pure chess logic tests
- `logic/src/test/kotlin/.../NQueensGameTest.kt` - Game state management tests
- `app/src/test/java/.../GameViewModelTest.kt` - Presentation logic tests


**Testing approach**:
- **Unit tests** for pure chess logic (NQueensLogic)
- **StateFlow testing** for reactive game implementation (NQueensGame)
- **ViewModel testing** for state transformation and side effects
- **UI tests** for Compose components and user interactions

## Build & Test

Build and test like a regular Android project.

