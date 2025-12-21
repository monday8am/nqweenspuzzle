# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

N-Queens Puzzle is an Android application built with Kotlin and Jetpack Compose. The project uses modern Android development practices with Material 3 design.

## Build Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.monday8am.nqueenspuzzle.ExampleUnitTest"

# Update screenshot reference images
./gradlew updateDebugScreenshotTest

# Validate screenshots against references
./gradlew validateDebugScreenshotTest

# Clean build
./gradlew clean

# Install debug APK to connected device
./gradlew installDebug
```

## Architecture

- **Single module Android app** (`app/`)
- **UI Framework**: Jetpack Compose with Material 3
- **Min SDK**: 26, Target SDK: 36
- **Package**: `com.monday8am.nqueenspuzzle`

### Key Directories

- `app/src/main/java/` - Main Kotlin source code
- `app/src/main/java/.../ui/theme/` - Compose theming (colors, typography, theme configuration)
- `app/src/test/` - Unit tests (JUnit 4, Roboelectric)
- `app/src/screenshotTest/` - Screenshot tests (Compose Preview)

### Dependencies

Managed via version catalog (`gradle/libs.versions.toml`):
- Kotlin 2.0.21
- Compose BOM 2024.09.00
- AGP 8.13.2
