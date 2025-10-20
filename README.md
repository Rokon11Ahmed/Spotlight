# Spotlight [![Maven Central](https://img.shields.io/maven-central/v/io.github.rokon11ahmed/spotlight.svg)](https://central.sonatype.com/artifact/io.github.rokon11ahmed/spotlight)

A lightweight, fully customizable **Spotlight** library for Android.  
Highlight any view in your app, guide users with info cards, pulse animations, and step navigation — all programmatically without XML.  

---

## ✨ Features

- Highlight any `View`, `RecyclerView` item, or `TabLayout` tab
- Multiple shapes: `Circle`, `Rectangle`, `RoundedRect`, `Oval`
- Auto-position info cards (above, below, left, right, or auto)
- Info card includes **Skip** and **Next** buttons (UX friendly, aligned below card)
- Smooth animations (fade, pulse, breathing, bounce)
- Optional **blurred overlay** effect
- Fully configurable via Builder pattern
- No XML required — 100% programmatic
- Works seamlessly with dynamic layouts and RecyclerView
- Listener support (`onStepShown`, `onStepDismissed`, `onStepNext`, `onStepSkipped`, `onFinished`)
- Builder allows customization for overlay color, card background, text styling, padding, animations, and more

---

## 📦 Installation (via MavenCentral)

## Add dependency in your module `build.gradle`:

```
dependencies {
    implementation("io.github.rokon11ahmed:spotlight:1.0.2")
}
```
## Usage Example:

```
// Create Spotlight instance using Builder
val spotlight = Spotlight.Builder(this)
    .addStep(
        SpotlightStep(
            SpotlightTarget.ViewTarget(fabIcon),
            "Quick Action",
            "Tap here to add a new item",
            SpotlightShape.CIRCLE
        )
    )
    .addStep(
        SpotlightStep(
            SpotlightTarget.TabTarget(tabLayout, 0),
            "First Tab",
            "Tap here to see first tab item",
            SpotlightShape.RECTANGLE
        )
    )
    .addStep(
        SpotlightStep(
            SpotlightTarget.TabTarget(tabLayout, 1),
            "Second Tab",
            "Tap here to see second tab item",
            SpotlightShape.RECTANGLE
        )
    )
    .addStep(
        SpotlightStep(
            SpotlightTarget.ViewTarget(submitButton),
            "Submit",
            "Tap here to submit data",
            SpotlightShape.ROUNDED_RECT
        )
    )
    .overlayColor(Color.parseColor("#99000000"))  // semi-transparent overlay
    .cardBackground(Color.parseColor("#CC333333")) // info card background
    .shapeAnimation(ShapeAnimation.PULSE) // optional animations
    .useBlur(true) // enable blurred overlay
    .showButtons(true) // show skip/next
    .listener(object : SpotlightListener {
        override fun onStepShown(index: Int, step: SpotlightStep) { }
        override fun onStepDismissed(index: Int, step: SpotlightStep) { }
        override fun onStepNext(index: Int, step: SpotlightStep) { }
        override fun onStepSkipped(index: Int, step: SpotlightStep) { }
        override fun onFinished() { }
    })
    .build()

spotlight.start()
```

### Mandatory
| Usage         | Description | 
| ------------- |-------------| 
| `builder.context(Context)`        |  Required context             |
| `builder.addStep(SpotlightStep)` |  Add one step (can be called multiple times)|
| `builder.build()` |  Prepare the Spotlight sequence| 
| `builder.start()` |  Start displaying Spotlight| 

### Optional
| Usage                                | Description                                                                           | 
| ------------------------------------ |-------------------------------------------------------------------------------------- | 
| `builder.overlayColor(Int)`     |  Overlay background color (default semi-transparent black) | 
| `builder.cardBackground(Int)`   |  Info card background color                                | 
| `builder.titleStyle(TextStyle)`         |  Customize title text style (size, color, typeface)                                                       | 
| `builder.descStyle(TextStyle)`  |  Customize description text style                                            | 
| `builder.showButtons(Boolean)`  |  Show/hide skip/next buttons (default true)                                 | 
| `builder.shapeAnimation(ShapeAnimation)`     |  Choose target animation (NONE, PULSE, BREATHING, BOUNCE)                                                           | 
| `builder.useBlur(Boolean)`     |  Enable blurred overlay background                  |
| `builder.highlightPadding(Int)`     |  Extra padding around highlight area           |
| `builder.cardPosition(CardPosition)`     |  Position info card (ABOVE, BELOW, LEFT, RIGHT, CENTER, AUTO)       |
| `builder.listener(SpotlightListener)`       |  Attach callbacks                                                        | 

# License

    Copyright 2018-2020 erkutaras

    Licensed under the Apache License, Version 2.0 (the "License");
    You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
