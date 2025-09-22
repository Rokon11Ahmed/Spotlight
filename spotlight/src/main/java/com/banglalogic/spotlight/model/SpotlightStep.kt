package com.banglalogic.daymate.spotlight.model

import android.view.View

data class SpotlightStep(
    val target: SpotlightTarget,
    val title: String,
    val description: String,
    val shape: SpotlightShape
)

enum class SpotlightShape { CIRCLE, RECTANGLE, ROUNDED_RECT }
