# Spotlight

A lightweight, fully customizable **Spotlight** library for Android.  
Highlight any view in your app, show info cards, pulse animations, and guide your users through your UI—all programmatically without XML.

---

## Features

- Highlight any `View`, `RecyclerView` item, or `TabLayout` tab
- Multiple shapes: `Circle`, `Rectangle`, `RoundedRect`, `Oval`
- Auto-position info cards (above or below target depending on available space)
- Smooth fade-in/out and pulse animations
- Fully configurable via Builder pattern
- No XML required—100% programmatic
- Works seamlessly with dynamic layouts and RecyclerView
- Builder allows customization for overlay color, card background, text styling, padding, animations, and more

---

## Installation (via JitPack)

### Step 1: Add JitPack repository in your root `build.gradle`:

```
gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
## Step 2: Add dependency in your module `build.gradle`:

```
dependencies {
    implementation 'com.github.rokon11ahmed:spotlight:0.0.1'
}
```
## Usage Example:

```
// Create Spotlight instance using Builder
val spotlight = Spotlight.Builder(requireContext())
    .addStep(
        SpotlightStep(
            SpotlightTarget.ViewTarget(binding.fab),
            "Quick Action",
            "Tap here to add a new item",
            SpotlightShape.CIRCLE
        )
    )
    .addStep(
        SpotlightStep(
            SpotlightTarget.TabTarget(binding.tabLayout, 0),
            "Tab Action",
            "This is your first tab",
            SpotlightShape.ROUNDED_RECT
        )
    )
    .addStep(
        SpotlightStep(
            SpotlightTarget.ViewTarget(binding.addExpenditureSectorTextView),
            "Add Sector",
            "Tap here to add a new expenditure sector",
            SpotlightShape.RECTANGLE
        )
    )
    .overlayColor(Color.parseColor("#99000000")) // semi-transparent overlay
    .cardBackground(Color.parseColor("#333333")) // info card background
    .build()

// Start Spotlight
spotlight.start()
```

## License

This library is MIT licensed. See LICENSE for details.
