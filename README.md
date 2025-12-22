# N-Queens Puzzle

An Android puzzle game based on the classic [N-Queens problem](https://en.wikipedia.org/wiki/Eight_queens_puzzle). Place n queens on an n×n chessboard so that no two queens threaten each other (same row, column, or diagonal).

## Demo

N-Queens Puzzle Demo video: [Download](https://github.com/monday8am/nqweenspuzzle/blob/main/assets/n-queens-puzzle.mp4) , [Google link](https://photos.app.goo.gl/rYPGTeHGHZWSTKii7)

Snapshots:

<p align="center">
  <img src="assets/n-queens-puzzle1.jpg" alt="Star" width="32%">
  <img src="assets/n-queens-puzzle2.jpg" alt="Game" width="32%">
  <img src="assets/n-queens-puzzle3.jpg" alt="Score" width="32%">
</p>

## Features

- Interactive n×n chessboard (sizes 4-12)
- Three difficulty levels with varying hint systems
- Queens are moved if a conflict is detected to avoid extra taps for remove/add
- Real-time conflict validation and visual feedback
- Win detection with celebration animations
- Best times leaderboard

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

1. *NQueensLogic*: Pure chess logic functions with no state or side effects. Platform-agnostic and easily testable.
2. *NQueensGame*: Manages game state using Kotlin `StateFlow`. Orchestrates the chess logic and handles user moves.
3. *GameViewModel*: Thin adapter between game engine and the Android platform. Transforms game state to presentation state and handles side effects.

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

The project implements some optimizations to ensure smooth performance even for larger board sizes:

1. Rendering Optimization:

The `GameBoard` replaces the traditional "grid of cells" approach (which yields $N^2$ composables) with a custom **Canvas-based** rendering system. There are two layers of composables, one for the board (CanvasBoard) and one for the pieces (PieceLayout). 

- **CanvasBoard**: Draws the entire checkerboard, conflicts, and markers in a single `Canvas` node. It uses **`drawWithCache`** to cache static drawing instructions (like the square pattern) to avoid redundant computation during recomposition.
- **PieceLayout**: A custom `Layout` that only renders active pieces (Queens) based on the state, drastically reducing the node count to $1 (Canvas) + 1 (Layout) + N (Queens)$.
- **Geometric Tap Detection**: A single `pointerInput` on the board container calculates coordinates geometrically, removing the overhead of $N^2$ individual click listeners.

2. Computational Efficiency:

- **Bit-Packed `Position`**: The coordinate system uses an `@JvmInline value class`. This packs `row` and `col` into a single 32-bit `Int` (16 bits each), eliminating object allocations for every cell reference and significantly reducing pressure on the garbage collector.
- **$O(N)$ Conflict Detection**: Replaced $O(N^2)$ pair-wise comparisons with a frequency-tracking algorithm. By hashing row, column, and diagonal "occupancy", the engine detects conflicts in linear time relative to the number of queens, ensuring consistent performance even on the largest supported boards.

## Testing

### Strategy

The project implements a multi-layered testing strategy that validates logic, presentation, user interactions, and visual rendering. The main application logic is kept outside Composables, enabling isolated testing at each layer.

**Pyramid Structure**:
1. **Most tests**: Pure logic (fast, focused, no dependencies)
2. **Medium tests**: ViewModel & UI interactions (Roboelectric)
3. **Fewest tests**: Screenshot tests (strategic visual regression coverage)

**Key Principles**:
- Test behavior, not implementation
- Keep screenshot tests minimal to avoid maintenance burden
- Use Roboelectric for fast UI interaction testing
- Validate user workflows, not just individual actions

### Testing Layers

**1. Pure Logic Tests (JVM Unit Tests)**
- Pure chess logic (conflict detection, solution validation)
- Game state management and StateFlow behavior

**2. ViewModel & Presentation Tests (Roboelectric)**
- Tests presentation logic, state transformations, and side effects.

**3. UI Interaction Tests (Roboelectric + Compose)**
- Tests user interactions with Compose components without requiring an emulator.

**4. EXPERIMENTAL: Visual Regression Tests (Screenshot Testing)**
- Uses Google's experimental Compose Preview Screenshot Testing framework to catch unintended visual changes.

### CI/CD Integration

**Automated Tests on Every PR**:
- Logic tests (pure JVM)
- App unit tests (Roboelectric + ViewModel + UI interaction tests)
- Debug APK build

## What's missing or ommited

- There's not dependency injection for keeping the code simple
- There's no Queen add / remove animation
- No sound On/Off toggle (probably needed after some time playing)

## Build & Run

Build and run it like a regular Android project!

